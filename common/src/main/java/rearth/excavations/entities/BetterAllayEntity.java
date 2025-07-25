package rearth.excavations.entities;

import net.minecraft.block.Block;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.InventoryOwner;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.control.FlightMoveControl;
import net.minecraft.entity.ai.goal.FlyGoal;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MiningToolItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import rearth.excavations.entities.ai.ChestInteractionGoal;
import rearth.excavations.entities.ai.FlyToTagGoal;
import rearth.excavations.entities.ai.MineNearbyBlockGoal;
import rearth.excavations.init.TagContent;
import rearth.oritech.api.item.ItemApi;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Iterator;

public class BetterAllayEntity extends PathAwareEntity implements InventoryOwner, GeoEntity {
    
    public static float MAX_SPEED = 0.6f;
    
    public static final RawAnimation FLY_ANIM = RawAnimation.begin().thenLoop("fly");
    public static final RawAnimation WORK_ANIM = RawAnimation.begin().thenLoop("work");
    
    private static final TrackedData<ItemStack> SYNCED_TOOL = DataTracker.registerData(BetterAllayEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
    
    public boolean resetStoneCache = false;
    public BlockPos lastChest = BlockPos.ORIGIN;
    
    
    private final SimpleInventory inventory = new SimpleInventory(3);
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    
    public BetterAllayEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
        
        this.moveControl = new FlightMoveControl(this, 40, true);
        this.setNoGravity(true);
        this.setPersistent();
        
    }
    
    @Override
    protected EntityNavigation createNavigation(World world) {
        BirdNavigation birdNavigation = new BirdNavigation(this, world);
        birdNavigation.setCanPathThroughDoors(false);
        birdNavigation.setCanSwim(false);
        birdNavigation.setCanEnterOpenDoors(true);
        return birdNavigation;
    }
    
    @Override
    protected void initGoals() {
        super.initGoals();
        
        this.goalSelector.add(3, new FindPickaxeGoal(this));
        this.goalSelector.add(3, new DepositItemsGoal(this));
        this.goalSelector.add(3, new MineNearbyBlockGoal(this));
        this.goalSelector.add(5, new FlyToStone(this, TagContent.ALLAY_MINEABLE, 1f, 0.3f, 8f));
        this.goalSelector.add(8, new FlyToTagGoal(this, BlockTags.BEACON_BASE_BLOCKS, 7, 3, 40f));
        
        this.goalSelector.add(10, new FlyGoal(this, MAX_SPEED / 2f) {
            @Override
            public void start() {
                super.start();
                System.out.println("Starting wandering");
                startFlyAnimation();
            }
        });
        
    }
    
    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        Inventories.writeNbt(nbt, inventory.getHeldStacks(), true, this.getWorld().getRegistryManager());
        nbt.putLong("home", lastChest.asLong());
        
        if (!getSyncedTool().isEmpty()) {
            nbt.put("tool", getSyncedTool().encode(getWorld().getRegistryManager()));
        }
        
        return super.writeNbt(nbt);
    }
    
    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, inventory.getHeldStacks(), getWorld().getRegistryManager());
        lastChest = BlockPos.fromLong(nbt.getLong("home"));
        
        if (nbt.contains("tool")) {
            setSyncedTool(ItemStack.fromNbtOrEmpty(getWorld().getRegistryManager(), nbt.getCompound("tool")));
        }
        
    }
    
    @Override
    public void onDeath(DamageSource damageSource) {
        
        for (var stack : inventory.getHeldStacks()) {
            if (stack.isEmpty()) continue;
            getWorld().spawnEntity(new ItemEntity(getWorld(), getX(), getY(), getZ(), stack.copy()));
        }
        
        if (!getSyncedTool().isEmpty())
            getWorld().spawnEntity(new ItemEntity(getWorld(), getX(), getY(), getZ(), getSyncedTool().copy()));
        
        super.onDeath(damageSource);
    }
    
    public static DefaultAttributeContainer.Builder createMobAttributes() {
        return PathAwareEntity.createLivingAttributes()
                 .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 48)    // this is the max pathfinding range
                 .add(EntityAttributes.GENERIC_MAX_HEALTH, 8.0)
                 .add(EntityAttributes.GENERIC_FLYING_SPEED, MAX_SPEED);
    }
    
    @Override
    public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        return false;
    }
    
    public void onHappy() {
        if (this.getWorld() instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(ParticleTypes.HAPPY_VILLAGER,
              this.getX(), this.getEyeY(), this.getZ(),
              4, 0.5, 0.5, 0.5, 0.1);
        }
    }
    
    public void onSad() {
        if (this.getWorld() instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(ParticleTypes.ANGRY_VILLAGER,
              this.getX(), this.getEyeY(), this.getZ(),
              4, 0.5, 0.5, 0.5, 0.1);
        }
    }
    
    public boolean canStartMining() {
        return inventory.getStack(2).isEmpty() && !this.getSyncedTool().isEmpty();
    }
    
    @Override
    public boolean canImmediatelyDespawn(double distanceSquared) {
        return false;
    }
    
    @Override
    public boolean cannotDespawn() {
        return true;
    }
    
    @Override
    public SimpleInventory getInventory() {
        return inventory;
    }
    
    public BlockPos getSearchStartPos() {
        if (lastChest.equals(BlockPos.ORIGIN))
            return this.getBlockPos();
        return lastChest;
    }
    
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controller) {
        controller.add(new AnimationController<GeoAnimatable>(this, "allay", 1, state -> {
            if (state.getController().hasAnimationFinished()) {
                return state.setAndContinue(FLY_ANIM);
            }
            return PlayState.CONTINUE;
        }).triggerableAnim("fly", FLY_ANIM).triggerableAnim("work", WORK_ANIM));
    }
    
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }
    
    public void startFlyAnimation() {
        triggerAnim("allay", "fly");
    }
    
    public void startWorkAnimation() {
        triggerAnim("allay", "work");
    }
    
    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(SYNCED_TOOL, ItemStack.EMPTY);
    }
    
    public ItemStack getSyncedTool() {
        return this.dataTracker.get(SYNCED_TOOL);
    }
    
    public void setSyncedTool(ItemStack tool) {
        this.dataTracker.set(SYNCED_TOOL, tool);
    }
    
    private static class FlyToStone extends FlyToTagGoal {
        
        private Iterator<BlockPos> nextPosIterator;
        
        public FlyToStone(BetterAllayEntity entity, TagKey<Block> targetFilter, float minStartRange, float reachDist, float searchDist) {
            super(entity, targetFilter, minStartRange, reachDist, searchDist);
        }
        
        @Override
        public boolean canStart() {
            return this.entity.canStartMining() && super.canStart();
        }
        
        @Override
        public BlockPos findClosestTarget() {
            
            if (entity.resetStoneCache) {
                nextPosIterator = null;
                entity.resetStoneCache = false;
            }
            
            if (nextPosIterator != null && nextPosIterator.hasNext()) {
                var candidate = getClosestFromIterator();
                if (candidate != null) return candidate;
            }
            
            var closeResult = super.findClosestTarget();
            
            if (closeResult == null && entity.lastChest != BlockPos.ORIGIN) {
                System.out.println("getting target from dig controller");
                var digTarget = DigController.getNextPosition(entity.lastChest, entity.getWorld());
                nextPosIterator = BlockPos.iterateOutwards(digTarget, 4, 4, 4).iterator();
            }
            
            return null;
        }
        
        private BlockPos getClosestFromIterator() {
            while (nextPosIterator.hasNext()) {
                var candidate = nextPosIterator.next();
                var candidateState = entity.getWorld().getBlockState(candidate);
                if (candidateState.isAir() || !candidateState.isIn(TagContent.ALLAY_MINEABLE)) continue;
                return candidate;
            }
            
            return null;
        }
    }
    
    private static class FindPickaxeGoal extends ChestInteractionGoal {
        
        public FindPickaxeGoal(BetterAllayEntity entity) {
            super(entity);
        }
        
        @Override
        public boolean canStart() {
            if (!entity.getSyncedTool().isEmpty()) return false;
            return super.canStart();
        }
        
        @Override
        public boolean tryInventoryExchange() {
            if (entity.getWorld().getBlockEntity(targetChest) instanceof ChestBlockEntity chestEntity) {
                // search for item
                for (int i = 0; i < chestEntity.size(); i++) {
                    var candidateStack = chestEntity.getStack(i);
                    if (candidateStack.getItem() instanceof MiningToolItem pickaxeItem && candidateStack.isIn(ItemTags.PICKAXES)) {
                        entity.setSyncedTool(candidateStack.copyWithCount(1));
                        candidateStack.decrement(1);
                        System.out.println("found pickaxe");
                        return true;
                    }
                }
            }
            
            System.out.println("failed chest");
            return false;
        }
    }
    
    private static class DepositItemsGoal extends ChestInteractionGoal {
        
        public DepositItemsGoal(BetterAllayEntity entity) {
            super(entity);
        }
        
        @Override
        public boolean canStart() {
            if (entity.getInventory().getStack(2).isEmpty()) return false;  // only start if we have at least 1 stack, and slot 2 is started being used
            return super.canStart();
        }
        
        @Override
        public boolean tryInventoryExchange() {
            if (entity.getWorld().getBlockEntity(targetChest) instanceof ChestBlockEntity chestEntity) {
                var chestCandidate = ItemApi.BLOCK.find(entity.getWorld(), targetChest, null);
                if (chestCandidate != null) {
                    var toTransfer = entity.inventory.getHeldStacks();
                    for (var stack : toTransfer) {
                        var inserted = chestCandidate.insert(stack, false);
                        System.out.println("moved: " + stack + " | " + inserted);
                        stack.decrement(inserted);
                    }
                }
                
                if (entity.inventory.isEmpty())
                    return true;
                
            }
            
            System.out.println("failed chest");
            return false;
        }
    }
}
