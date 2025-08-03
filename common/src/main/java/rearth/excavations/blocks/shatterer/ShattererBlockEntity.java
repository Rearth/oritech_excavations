package rearth.excavations.blocks.shatterer;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import org.joml.Vector2i;
import rearth.excavations.blocks.ExplosiveChargeBlock;
import rearth.excavations.init.BlockEntitiesContent;
import rearth.excavations.init.RecipeContent;
import rearth.oritech.api.energy.EnergyApi;
import rearth.oritech.api.energy.containers.SimpleEnergyStorage;
import rearth.oritech.api.item.ItemApi;
import rearth.oritech.api.item.containers.SimpleInventoryStorage;
import rearth.oritech.api.networking.NetworkedBlockEntity;
import rearth.oritech.api.networking.SyncField;
import rearth.oritech.api.networking.SyncType;
import rearth.oritech.block.base.block.MachineBlock;
import rearth.oritech.block.base.block.MultiblockMachine;
import rearth.oritech.block.blocks.processing.MachineCoreBlock;
import rearth.oritech.client.init.ParticleContent;
import rearth.oritech.init.recipes.OritechRecipe;
import rearth.oritech.util.MultiblockMachineController;
import rearth.oritech.util.SimpleCraftingInventory;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static rearth.oritech.block.base.entity.MachineBlockEntity.*;

// renders energy amount and charge item on top. Has no UI.
// right click with charge item loads it in (only 1 can be kept inside at a time). Charges are unstackable.
// right click with anything else (or empty) just displays the status.
// when player is near the machine, the control panel hologram shows up
public class ShattererBlockEntity extends NetworkedBlockEntity implements EnergyApi.BlockProvider, ItemApi.BlockProvider, MultiblockMachineController, GeoBlockEntity {
    
    public static final RawAnimation SHOOT = RawAnimation.begin().thenPlay("fire");
    
    // storage
    @SyncField({SyncType.INITIAL, SyncType.TICK})
    public final SimpleEnergyStorage energyStorage = new SimpleEnergyStorage(250_000, 0, 10_000_000, this::markDirty);
    @SyncField({SyncType.INITIAL, SyncType.TICK})
    public final SimpleInventoryStorage inventory = new SimpleInventoryStorage(1, this::markDirty) {
        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }
        
        @Override
        public int insertToSlot(ItemStack addedStack, int slot, boolean simulate) {
            if (!(addedStack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof ExplosiveChargeBlock))
                return 0;
            return super.insertToSlot(addedStack, slot, simulate);
        }
    };
    
    private boolean hasSupports = false;
    private long lastFiredAt;
    
    // multiblock
    private final ArrayList<BlockPos> coreBlocksConnected = new ArrayList<>();
    
    protected final AnimatableInstanceCache animatableInstanceCache = GeckoLibUtil.createInstanceCache(this);
    
    public ShattererBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntitiesContent.SHATTERER_BLOCK_ENTITY, pos, state);
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }
    
    @Override
    public void serverTick(World world, BlockPos blockPos, BlockState blockState, NetworkedBlockEntity networkedBlockEntity) {
        
        if (!blockState.get(MultiblockMachine.ASSEMBLED)) return;
        
        if (!hasSupports && world.getTime() % 124 == 0)
            initSupports();
        
        if (hasSupports && energyStorage.getAmount() >= energyStorage.getCapacity() && !inventory.isEmpty()) {
            shoot();
        }
        
    }
    
    private void shoot() {
        
        var targetPos = findShotTarget();
        
        if (targetPos == null) return;
        
        var takenStack = inventory.getStack(0);
        var explosionPower = 1;
        inventory.setStack(0, ItemStack.EMPTY);
        if (takenStack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof ExplosiveChargeBlock explosiveChargeBlock) {
            explosionPower = explosiveChargeBlock.power;
        }
        
        energyStorage.extractIgnoringLimit(explosionPower * 1_000_000L, false);
        energyStorage.update();
        lastFiredAt = world.getTime();
        
        triggerAnim("machine", "shoot");
        
        if (world instanceof ServerWorld serverWorld) {
            var spawnAt = this.pos.toCenterPos();
            serverWorld.spawnParticles(ParticleTypes.LARGE_SMOKE, spawnAt.x, spawnAt.y, spawnAt.z, 3, 0, 0, 0, 0);
            serverWorld.spawnParticles(ParticleTypes.SONIC_BOOM, spawnAt.x, spawnAt.y - 3.5, spawnAt.z, 3, 0, 0, 0, 0);
            for (int i = 0; i < 5; i++) {
                spawnAt = this.pos.toCenterPos().addRandom(world.random, 5);
                serverWorld.spawnParticles(ParticleTypes.POOF, spawnAt.x, spawnAt.y, spawnAt.z, 3, 0, 0, 0, 0);
            }
            serverWorld.playSound(null, pos, SoundEvents.ENTITY_BREEZE_CHARGE, SoundCategory.BLOCKS, 2.5f, 0.2f);
            serverWorld.playSound(null, pos, SoundEvents.BLOCK_BAMBOO_WOOD_TRAPDOOR_OPEN, SoundCategory.BLOCKS, 1f, 1f);
        }
        
        ParticleContent.LASER_BOOM.spawn(world, pos.down(3).toCenterPos(), targetPos.toCenterPos());
        
        createShatteredArea(world, targetPos, explosionPower);
        
    }
    
    public static void createShatteredArea(World world, BlockPos targetPos, int explosionPower) {
        world.setBlockState(targetPos, Blocks.AIR.getDefaultState());
        world.createExplosion(null, targetPos.getX(), targetPos.getY(), targetPos.getZ(), explosionPower * 0.5f, false, World.ExplosionSourceType.BLOCK);
        
        var maxShatteredBlocks = explosionPower * explosionPower * explosionPower;
        var shatteredBlocks = 0;
        
        // create shattered area
        for (var candidatePos : BlockPos.iterateOutwards(targetPos, explosionPower * 2, (int) (explosionPower * 1.5f), explosionPower * 2)) {
            var dist = candidatePos.getSquaredDistance(targetPos);
            var candidateState = world.getBlockState(candidatePos);
            if (candidateState.isAir() || candidateState.isLiquid()) continue;
            if (world.getRandom().nextFloat() < dist / (explosionPower * explosionPower * 4f)) continue;
            var recipeCandidate = getRecipeForBlock(candidateState, world);
            if (recipeCandidate == null) continue;
            
            var resultItem = recipeCandidate.getResults().getFirst().getItem();
            var block = Blocks.AIR.getDefaultState();
            if (resultItem instanceof BlockItem blockItem)
                block = blockItem.getBlock().getDefaultState();
            
            world.setBlockState(candidatePos, block, Block.NOTIFY_LISTENERS, 0);
            shatteredBlocks += recipeCandidate.getTime();
            if (shatteredBlocks > maxShatteredBlocks) {
                break;
            }
        }
    }
    
    private BlockPos findShotTarget() {
        
        var start = this.getPos().down(3);
        
        var blockedPositions = new HashSet<Vector2i>();
        
        for (int i = 0; i < 1024; i++) {
            
            // this is set to false if we encounter air anywhere
            var layerBlocked = true;
            var center = start.down(i);
            
            var width = Math.clamp(i / 8, 1, 16);
            
            for (int x = -width; x < width; x++) {
                for (int z = -width; z < width; z++) {
                    
                    var offset = new Vector2i(x, z);
                    if (blockedPositions.contains(offset)) continue;
                    
                    var checkPos = center.add(x, 0, z);
                    var checkState = world.getBlockState(checkPos);
                    
                    if (checkState.isAir()) {
                        layerBlocked = false;
                        continue;
                    }
                    
                    var recipeCandidate = getRecipeForBlock(checkState, world);
                    if (recipeCandidate != null) {
                        return checkPos;
                    } else {
                        blockedPositions.add(offset);
                    }
                    
                }
            }
            
            if (layerBlocked) return null;
            
        }
        
        return null;
        
    }
    
    private void initSupports() {
        
        hasSupports = true;
        
        var startOffsets = List.of(
          new Vec3i(2, -1, 1),
          new Vec3i(2, -1, -1),
          new Vec3i(-2, -1, 1),
          new Vec3i(-2, -1, -1),
          new Vec3i(1, -1, 2),
          new Vec3i(-1, -1, 2),
          new Vec3i(1, -1, -2),
          new Vec3i(-1, -1, -2)
        );
        
        for (var offset : startOffsets) {
            var worldPos = pos.add(offset);
            
            // start floodfill, ignore core blocks and going till a specific hardness is found
            
            var openPositions = new HashSet<BlockPos>();
            var checkedPositions = new HashSet<BlockPos>();
            var maxIterations = 90;
            var foundHardness = 0f;
            
            openPositions.add(worldPos);
            
            for (int i = 0; i < maxIterations; i++) {
                
                var checked = new HashSet<BlockPos>();
                var toAdd = new HashSet<BlockPos>();
                for (var openPos : openPositions) {
                    checkedPositions.add(openPos);
                    checked.add(openPos);
                    var openState = world.getBlockState(openPos);
                    if (openState.isAir() || openState.getBlock() instanceof MachineCoreBlock) continue;
                    if (openState.getHardness(world, openPos) > 0.1f) {
                        foundHardness += openState.getHardness(world, openPos);
                        
                        // add neighboring positions to queue
                        Arrays.stream(getNeighbors(openPos)).filter(elem -> !checkedPositions.contains(elem)).forEach(toAdd::add);
                        
                    }
                }
                openPositions.removeAll(checked);
                openPositions.addAll(toAdd);
                
                if (openPositions.isEmpty()) break;
                
                if (foundHardness > 300) break;
                
            }
            
            if (foundHardness < 300) {
                hasSupports = false;
                ParticleContent.HIGHLIGHT_BLOCK.spawn(world, Vec3d.of(worldPos));
                break;
            }
        }
        
    }
    
    private static BlockPos[] getNeighbors(BlockPos from) {
        return new BlockPos[]{
          from.add(0, 1, 0),
          from.add(0, -1, 0),
          from.add(1, 0, 0),
          from.add(-1, 0, 0),
          from.add(0, 0, 1),
          from.add(0, 0, -1)
        };
    }
    
    public static OritechRecipe getRecipeForBlock(BlockState state, World world) {
        
        var input = state.getBlock().asItem();
        if (input == null) return null;
        
        var recipeInv = new SimpleCraftingInventory(new ItemStack(input));
        var candidate = world.getRecipeManager().getFirstMatch(RecipeContent.SHATTERER, recipeInv, world);
        return candidate.map(RecipeEntry::value).orElse(null);
        
    }
    
    public void getStatus(PlayerEntity player) {
        
        var status = "no_target";
        
        if (!hasSupports) {
            status = "no_support";
        } else if (inventory.isEmpty()) {
            status = "no_ammo";
        } else if (energyStorage.getAmount() <= 0) {
            status = "no_energy";
        } else if (world.getTime() - lastFiredAt < 100) {
            status = "working";
        }
        
        player.sendMessage(Text.translatable("hint.oritech_excavations." + status));
    }
    
    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        Inventories.writeNbt(nbt, inventory.heldStacks, false, registryLookup);
        addMultiblockToNbt(nbt);
        nbt.putLong("energy", energyStorage.getAmount());
        nbt.putBoolean("support", hasSupports);
    }
    
    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        Inventories.readNbt(nbt, inventory.heldStacks, registryLookup);
        loadMultiblockNbtData(nbt);
        energyStorage.setAmount(nbt.getLong("energy"));
        hasSupports = nbt.getBoolean("support");
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
            if (state.getController().isPlayingTriggeredAnimation() && !state.getController().hasAnimationFinished()) {
                return PlayState.CONTINUE;
            } else if (this.isAssembled(this.getCachedState())) {
                return state.setAndContinue(IDLE);
            } else {
                return state.setAndContinue(PACKAGED);
            }
        }).triggerableAnim("setup", SETUP).triggerableAnim("shoot", SHOOT));
    }
    
    public boolean isAssembled(BlockState state) {
        return state.get(MultiblockMachine.ASSEMBLED);
    }
    
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return animatableInstanceCache;
    }
}
