package rearth.excavations.blocks.shatterer;

import net.minecraft.block.BlockState;
import net.minecraft.inventory.Inventories;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import rearth.excavations.init.BlockEntitiesContent;
import rearth.oritech.api.energy.EnergyApi;
import rearth.oritech.api.energy.containers.SimpleEnergyStorage;
import rearth.oritech.api.item.ItemApi;
import rearth.oritech.api.item.containers.SimpleInventoryStorage;
import rearth.oritech.api.networking.NetworkedBlockEntity;
import rearth.oritech.api.networking.SyncField;
import rearth.oritech.api.networking.SyncType;
import rearth.oritech.block.base.block.MultiblockMachine;
import rearth.oritech.util.MultiblockMachineController;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.List;

import static rearth.oritech.block.base.entity.MachineBlockEntity.*;

// renders energy amount and charge item on top. Has no UI.
// right click with charge item loads it in (only 1 can be kept inside at a time). Charges are unstackable.
// right click with anything else (or empty) just displays the status.
// when player is near the machine, the control panel hologram shows up
public class ShattererBlockEntity extends NetworkedBlockEntity implements EnergyApi.BlockProvider, ItemApi.BlockProvider, MultiblockMachineController, GeoBlockEntity {
    
    // storage
    @SyncField({SyncType.INITIAL, SyncType.TICK})
    public final SimpleEnergyStorage energyStorage = new SimpleEnergyStorage(1_000_000, 0, 5_000_000, this::markDirty);
    public final SimpleInventoryStorage inventory = new SimpleInventoryStorage(1, this::markDirty);
    
    // multiblock
    private final ArrayList<BlockPos> coreBlocksConnected = new ArrayList<>();
    
    protected final AnimatableInstanceCache animatableInstanceCache = GeckoLibUtil.createInstanceCache(this);
    
    public ShattererBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntitiesContent.SHATTERER_BLOCK_ENTITY, pos, state);
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }
    
    @Override
    public void serverTick(World world, BlockPos blockPos, BlockState blockState, NetworkedBlockEntity networkedBlockEntity) {
    
    }
    
    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        Inventories.writeNbt(nbt, inventory.heldStacks, false, registryLookup);
        addMultiblockToNbt(nbt);
        nbt.putLong("energy", energyStorage.getAmount());
    }
    
    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        Inventories.readNbt(nbt, inventory.heldStacks, registryLookup);
        loadMultiblockNbtData(nbt);
        energyStorage.setAmount(nbt.getLong("energy"));
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
          new Vec3i(1, 0, 1),
          new Vec3i(1, 0, 0),
          new Vec3i(1, 0, -1),
          new Vec3i(0, 0, 1),
          new Vec3i(0, 0, -1),
          new Vec3i(-1, 0, 1),
          new Vec3i(-1, 0, 0),
          new Vec3i(-1, 0, -1),
          new Vec3i(1, -1, 1),
          new Vec3i(1, -1, 0),
          new Vec3i(1, -1, -1),
          new Vec3i(0, -1, 1),
          new Vec3i(0, -1, 0),
          new Vec3i(0, -1, -1),
          new Vec3i(-1, -1, 1),
          new Vec3i(-1, -1, 0),
          new Vec3i(-1, -1, -1),
          new Vec3i(1, -2, 1),
          new Vec3i(1, -2, 0),
          new Vec3i(1, -2, -1),
          new Vec3i(0, -2, 1),
          new Vec3i(0, -2, 0),
          new Vec3i(0, -2, -1),
          new Vec3i(-1, -2, 1),
          new Vec3i(-1, -2, 0),
          new Vec3i(-1, -2, -1)
        );
    }
    
    @Override
    public Direction getFacingForMultiblock() {
        var state = getCachedState();
        return state.get(Properties.HORIZONTAL_FACING).getOpposite();
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
    public void setCoreQuality(float v) {
        // not needed/used
    }
    
    @Override
    public float getCoreQuality() {
        return 1;
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
            if (state.getController().isPlayingTriggeredAnimation()) {
                return PlayState.CONTINUE;
            } else if (this.isAssembled(this.getCachedState())) {
                return state.setAndContinue(IDLE);
            } else {
                return state.setAndContinue(PACKAGED);
            }
        }).triggerableAnim("setup", SETUP));
    }
    
    public boolean isAssembled(BlockState state) {
        return state.get(MultiblockMachine.ASSEMBLED);
    }
    
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return animatableInstanceCache;
    }
}
