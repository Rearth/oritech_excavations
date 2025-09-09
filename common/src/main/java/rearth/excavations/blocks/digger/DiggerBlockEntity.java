package rearth.excavations.blocks.digger;

import com.mojang.authlib.GameProfile;
import dev.architectury.registry.menu.ExtendedMenuProvider;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import rearth.excavations.client.init.ScreenContent;
import rearth.excavations.init.BlockEntitiesContent;
import rearth.oritech.api.energy.EnergyApi;
import rearth.oritech.api.energy.containers.DynamicEnergyStorage;
import rearth.oritech.api.item.ItemApi;
import rearth.oritech.api.item.containers.InOutInventoryStorage;
import rearth.oritech.api.networking.NetworkedBlockEntity;
import rearth.oritech.api.networking.SyncField;
import rearth.oritech.api.networking.SyncType;
import rearth.oritech.block.base.block.MultiblockMachine;
import rearth.oritech.client.ui.UpgradableMachineScreenHandler;
import rearth.oritech.util.*;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.*;

import static rearth.oritech.block.base.entity.MachineBlockEntity.*;

public class DiggerBlockEntity extends NetworkedBlockEntity
  implements EnergyApi.BlockProvider, ItemApi.BlockProvider, MultiblockMachineController, MachineAddonController,
               ScreenProvider, ExtendedMenuProvider, GeoBlockEntity {
    
    private static final int DEFAULT_ENERGY_USAGE = 256;
    
    @SyncField({SyncType.INITIAL, SyncType.TICK, SyncType.GUI_OPEN})
    public final DynamicEnergyStorage energyStorage = new DynamicEnergyStorage(getDefaultCapacity(), getDefaultInsertRate(), 0, this::markDirty);
    @SyncField({SyncType.INITIAL, SyncType.TICK})
    public final InOutInventoryStorage inventory = new InOutInventoryStorage(5, this::markDirty, new InventorySlotAssignment(0, 1, 1, 4));
    
    // multiblock
    private final ArrayList<BlockPos> coreBlocksConnected = new ArrayList<>();
    
    @SyncField({SyncType.GUI_OPEN})
    private float coreQuality = 1f;
    @SyncField(SyncType.GUI_OPEN)
    private final List<BlockPos> connectedAddons = new ArrayList<>();
    @SyncField(SyncType.GUI_OPEN)
    private final List<BlockPos> openSlots = new ArrayList<>();
    @SyncField(SyncType.GUI_OPEN)
    private BaseAddonData addonData = MachineAddonController.DEFAULT_ADDON_DATA;
    
    @SyncField(SyncType.GUI_OPEN)
    public float maxRange;
    
    @SyncField(SyncType.TICK)
    public BlockPos currentTarget;
    @SyncField(SyncType.TICK)
    public long lastWorkTime = 0;
    
    private Queue<BlockPos> nextTargets = new ArrayDeque<>();
    private long movingUntil = 0;
    private boolean immediateSearch = false;
    private float breakProgress = 0f;
    PlayerEntity diggerPlayerEntity;
    
    // anim
    protected final AnimatableInstanceCache animatableInstanceCache = GeckoLibUtil.createInstanceCache(this);
    
    public DiggerBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntitiesContent.DIGGER_BLOCK_ENTITY, pos, state);
        currentTarget = pos;
    }
    
    @Override
    public void serverTick(World world, BlockPos pos, BlockState state, NetworkedBlockEntity blockEntity) {
        
        if (!isAssembled(state)) return;
        
        if (energyStorage.getAmount() <= 0) return;
        
        // find new blocks to mine
        if (nextTargets.isEmpty() && (world.getTime() % 25 == 0 || immediateSearch)) {
            nextTargets = findNextTargets();
            immediateSearch = false;
        }
        
        // head is being moved to new pos
        if (world.getTime() < movingUntil) return;
        
        if (!nextTargets.isEmpty() && tryUseEnergy()) {
            
            var nextTarget = nextTargets.peek();
            
            var headMoveDist = nextTarget.getManhattanDistance(currentTarget);
            currentTarget = nextTarget;
            if (headMoveDist > 7) {
                var moveTime = Math.min(headMoveDist * 3, 24);
                movingUntil = world.getTime() + moveTime;
                triggerAnim("machine", "work");
                
                return;
            }
            
            var nextState = world.getBlockState(nextTarget);
            
            var nextHardness = state.getHardness(world, nextTarget);
            breakProgress += calculateBreakingPower();
            lastWorkTime = world.getTime();
            if (breakProgress > nextHardness) {
                breakProgress = 0;
                if (isStateMineable(nextState, nextTarget)) {
                    breakBlock(nextTarget, nextState);
                }
                
                immediateSearch = true;
                nextTargets.remove();
            }
        }
        
    }
    
    // todo shovel item consumption
    private float calculateBreakingPower() {
        var base = 0.1f;
        var speed = addonData.speed();
        var itemBonus = 1f;
        return base / speed * itemBonus;
    }
    
    private boolean tryUseEnergy() {
        var cost = (int) (DEFAULT_ENERGY_USAGE / addonData.speed() * addonData.efficiency());
        if (energyStorage.getAmount() < cost) return false;
        energyStorage.setAmount(energyStorage.getAmount() - cost);
        energyStorage.update();
        return true;
    }
    
    private void breakBlock(BlockPos candidate, BlockState candidateState) {
        
        var dropped = Block.getDroppedStacks(candidateState, (ServerWorld) world, candidate, null);
        
        
        for (var stack : dropped) {
            for (int i = 1; i < 5; i++) {
                var inserted = InsertToInvSlotAny(inventory, stack, i, false);
                stack.decrement(inserted);
                if (stack.getCount() <= 0) break;
            }
        }
        
        candidateState.getBlock().onBreak(world, candidate, candidateState, getDiggerPlayerEntity());
        world.playSound(null, candidate, candidateState.getSoundGroup().getBreakSound(), SoundCategory.BLOCKS, 1f, 1f);
        world.breakBlock(candidate, false);
    }
    
    public static int InsertToInvSlotAny(ItemApi.InventoryStorage inventory, ItemStack addedStack, int slot, boolean simulate) {
        
        var slotStack = inventory.getStackInSlot(slot);
        var slotLimit = Math.min(inventory.getSlotLimit(slot), addedStack.getMaxCount());
        
        if (slotStack.isEmpty()) {
            var toInsert = Math.min(slotLimit, addedStack.getCount());
            if (!simulate) inventory.setStackInSlot(slot, addedStack.copyWithCount(toInsert));
            return toInsert;
        }
        
        if (ItemStack.areItemsAndComponentsEqual(slotStack, addedStack)) {
            var available = slotLimit - slotStack.getCount();
            var toInsert = Math.min(available, addedStack.getCount());
            if (toInsert > 0) {
                if (!simulate) slotStack.increment(toInsert);
                return toInsert;
            }
        }
        
        return 0;
    }
    
    private Queue<BlockPos> findNextTargets() {
        
        // iterate outwards in circles, increasing radius and height offset. Ignores back.
        
        var maxRadius = 48;
        
        var back = getFacingForMultiblock().getOpposite();
        
        for (var candidate : BlockPos.iterateOutwards(pos.up(4), maxRadius, maxRadius / 2, maxRadius)) {
            var distSq = candidate.getSquaredDistance(pos);
            
            // too close to machine (8 blocks dist)
            if (distSq < 8 * 8) continue;
            
            var candidateState = world.getBlockState(candidate);
            
            // ignore air
            if (!isStateMineable(candidateState, candidate)) continue;
            
            // ignore back, down and up
            var offset = Vec3d.of(candidate.subtract(pos)).normalize();
            if (Vec3d.of(back.getVector()).distanceTo(offset) < 0.9f) continue;
            if (Vec3d.of(new Vec3i(0, 1, 0)).distanceTo(offset) < 1) continue;
            if (Vec3d.of(new Vec3i(0, -1, 0)).distanceTo(offset) < 1.25) continue;
            
            // got a hit, return nearby blocks
            var result = new ArrayDeque<BlockPos>();
            for (var innerCandidate : BlockPos.iterateOutwards(candidate, 4, 4, 4)) {
                var innerState = world.getBlockState(innerCandidate);
                if (isStateMineable(innerState, candidate)) {
                    result.add(innerCandidate.mutableCopy());
                    if (result.size() > 15) return result;
                }
            }
        }
        
        return new ArrayDeque<>();
        
    }
    
    private boolean isStateMineable(BlockState state, BlockPos pos) {
        return !state.isAir() && !state.isLiquid() && state.getHardness(world, pos) >= 0;
    }
    
    @Override
    public boolean initMultiblock(BlockState state) {
        
        var wasAssembled = isAssembled(state);
        
        var nowAssembled = MultiblockMachineController.super.initMultiblock(state);
        
        if (nowAssembled != wasAssembled) {
            movingUntil = world.getTime() + 80;
        }
        
        return nowAssembled;
    }
    
    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        
        Inventories.writeNbt(nbt, inventory.heldStacks, false, registryLookup);
        addMultiblockToNbt(nbt);
        writeAddonToNbt(nbt);
        nbt.putLong("energy", energyStorage.getAmount());
        
        nbt.putFloat("range", maxRange);
        
    }
    
    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        
        Inventories.readNbt(nbt, inventory.heldStacks, registryLookup);
        loadMultiblockNbtData(nbt);
        loadAddonNbtData(nbt);
        energyStorage.setAmount(nbt.getLong("energy"));
        
        maxRange = nbt.getFloat("range");
    }
    
    private PlayerEntity getDiggerPlayerEntity() {
        if (diggerPlayerEntity == null && world instanceof ServerWorld serverWorld) {
            diggerPlayerEntity = FakeMachinePlayer.create(serverWorld, new GameProfile(UUID.randomUUID(), "oritech_digger"), inventory);
        }
        
        return diggerPlayerEntity;
    }
    
    @Override
    public EnergyApi.EnergyStorage getEnergyStorage(Direction direction) {
        return energyStorage;
    }
    
    @Override
    public ItemApi.InventoryStorage getInventoryStorage(Direction direction) {
        return inventory;
    }
    
    @Override
    public List<Vec3i> getCorePositions() {
        return List.of(
          new Vec3i(-1, 1, -1),
          new Vec3i(0, 1, -1),
          new Vec3i(1, 1, -1),
          new Vec3i(-1, 1, 0),
          new Vec3i(0, 1, 0),
          new Vec3i(1, 1, 0),
          new Vec3i(-1, 1, 1),
          new Vec3i(0, 1, 1),
          new Vec3i(1, 1, 1),
          new Vec3i(-1, 0, -1),
          new Vec3i(-1, 0, 0),
          new Vec3i(-1, 0, 1),
          new Vec3i(0, 0, -1),
          new Vec3i(0, 0, 1)
        );
    }
    
    @Override
    public Direction getFacingForMultiblock() {
        return getCachedState().get(Properties.HORIZONTAL_FACING);
    }
    
    @Override
    public BlockPos getPosForMultiblock() {
        return pos;
    }
    
    @Override
    public World getWorldForMultiblock() {
        return world;
    }
    
    @Override
    public ArrayList<BlockPos> getConnectedCores() {
        return coreBlocksConnected;
    }
    
    @Override
    public void setCoreQuality(float quality) {
        coreQuality = quality;
    }
    
    @Override
    public List<BlockPos> getConnectedAddons() {
        return connectedAddons;
    }
    
    @Override
    public List<BlockPos> getOpenAddonSlots() {
        return openSlots;
    }
    
    @Override
    public BlockPos getPosForAddon() {
        return pos;
    }
    
    @Override
    public World getWorldForAddon() {
        return world;
    }
    
    @Override
    public Direction getFacingForAddon() {
        return getCachedState().get(Properties.HORIZONTAL_FACING);
    }
    
    @Override
    public DynamicEnergyStorage getStorageForAddon() {
        return energyStorage;
    }
    
    @Override
    public ItemApi.InventoryStorage getInventoryForAddon() {
        return inventory;
    }
    
    @Override
    public ScreenProvider getScreenProvider() {
        return this;
    }
    
    @Override
    public List<Vec3i> getAddonSlots() {
        return List.of(
          new Vec3i(1, 0, -1),
          new Vec3i(1, 0, 1)
        );
    }
    
    @Override
    public BaseAddonData getBaseAddonData() {
        return addonData;
    }
    
    @Override
    public void setBaseAddonData(BaseAddonData data) {
        this.addonData = data;
    }
    
    @Override
    public long getDefaultCapacity() {
        return 500_000;
    }
    
    @Override
    public long getDefaultInsertRate() {
        return 50_000;
    }
    
    @Override
    public float getCoreQuality() {
        return coreQuality;
    }
    
    @Override
    public ItemApi.InventoryStorage getInventoryForMultiblock() {
        return inventory;
    }
    
    @Override
    public EnergyApi.EnergyStorage getEnergyStorageForMultiblock(Direction direction) {
        return energyStorage;
    }
    
    @Override
    public void triggerSetupAnimation() {
        triggerAnim("machine", "setup");
    }
    
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "machine", state -> {
            if (state.getController().isPlayingTriggeredAnimation() && !state.getController().hasAnimationFinished()) {
                return PlayState.CONTINUE;
            } else if (this.isAssembled(this.getCachedState())) {
                if (isWorking()) return state.setAndContinue(WORKING);
                return state.setAndContinue(IDLE);
            } else {
                return state.setAndContinue(PACKAGED);
            }
        }).triggerableAnim("setup", SETUP).triggerableAnim("work", WORKING));
    }
    
    public boolean isAssembled(BlockState state) {
        return state.get(MultiblockMachine.ASSEMBLED);
    }
    
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return animatableInstanceCache;
    }
    
    @Override
    public List<GuiSlot> getGuiSlots() {
        return List.of(
          new GuiSlot(0, 50, 35),
          new GuiSlot(1, 120, 25, true),
          new GuiSlot(2, 120, 45, true),
          new GuiSlot(3, 120 + 20, 25, true),
          new GuiSlot(4, 120 + 20, 45, true)
        );
    }
    
    @Override
    public float getDisplayedEnergyUsage() {
        return (int) (DEFAULT_ENERGY_USAGE * (1f / addonData.speed()));
    }
    
    @Override
    public float getProgress() {
        return 0;
    }
    
    @Override
    public InventoryInputMode getInventoryInputMode() {
        return InventoryInputMode.FILL_LEFT_TO_RIGHT;
    }
    
    @Override
    public Inventory getDisplayedInventory() {
        return inventory;
    }
    
    @Override
    public ScreenHandlerType<?> getScreenHandlerType() {
        return ScreenContent.DIGGER_SCREEN;
    }
    
    @Override
    public void saveExtraData(PacketByteBuf buf) {
        this.sendUpdate(SyncType.GUI_OPEN);
        buf.writeBlockPos(pos);
    }
    
    @Override
    public Text getDisplayName() {
        return Text.literal("");
    }
    
    public boolean isWorking() {
        return world.getTime() - lastWorkTime < 20;
    }
    
    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new UpgradableMachineScreenHandler(syncId, playerInventory, this);
    }
}
