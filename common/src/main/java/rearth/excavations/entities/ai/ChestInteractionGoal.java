package rearth.excavations.entities.ai;

import net.minecraft.block.Blocks;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import rearth.excavations.entities.BetterAllayEntity;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public abstract class ChestInteractionGoal extends Goal {
    
    public final BetterAllayEntity entity;
    
    private static final Map<BlockPos, Long> emptyChestTimings = new HashMap<>();
    
    public BlockPos targetChest = BlockPos.ORIGIN;
    private long searchWaitTicks = 50;
    private long chestInteractionEnd = 0;
    private int timeStuck = 0;
    
    public ChestInteractionGoal(BetterAllayEntity entity) {
        this.entity = entity;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }
    
    @Override
    public boolean canStart() {
        
        var chestCandidate = findClosestChest();
        if (chestCandidate != null) {
            System.out.println("target chest: " + chestCandidate);
            targetChest = chestCandidate;
            return true;
        }
        
        return false;
    }
    
    @Override
    public void start() {
        System.out.println("Starting chest goal");
        var targetPos = targetChest.toCenterPos();
        this.entity.getNavigation().startMovingTo(targetPos.x, targetPos.y, targetPos.z, BetterAllayEntity.MAX_SPEED);
        timeStuck = 0;
        this.entity.startFlyAnimation();
        this.entity.resetStoneCache = true;
        super.start();
    }
    
    @Override
    public void stop() {
        System.out.println("Stopping chest goal");
        if (entity.getWorld().getBlockEntity(targetChest) instanceof ChestBlockEntity chestEntity) {
            entity.getWorld().addSyncedBlockEvent(targetChest, Blocks.CHEST, 1, 0);
        }
        targetChest = BlockPos.ORIGIN;
        searchWaitTicks = (long) (60 * 0.8);
        super.stop();
    }
    
    @Override
    public boolean shouldContinue() {
        return !this.targetChest.equals(BlockPos.ORIGIN)
                 && entity.getWorld().getBlockEntity(this.targetChest) instanceof ChestBlockEntity;
    }
    
    @Override
    public void tick() {
        super.tick();
        
        if (entity.getWorld().isClient) return;
        
        if (chestInteractionEnd > 0) {
            if (entity.getWorld().getTime() <= chestInteractionEnd) return;
            
            if (entity.getWorld().getTime() > chestInteractionEnd) {
                chestInteractionEnd = 0;
                setChestOpenState(targetChest, false);
                stop();
            }
        }
        
        if ((targetChest == null || targetChest == BlockPos.ORIGIN)) return;
        
        if (targetChest.toCenterPos().squaredDistanceTo(entity.getPos()) < 3) {
            interactWithChest();
        } else {
            if (this.entity.getNavigation().isIdle()) {
                this.timeStuck++;
                
                if (timeStuck > 40) {
                    System.out.println("stuck!");
                    emptyChestTimings.put(targetChest, entity.getWorld().getTime());
                    entity.onSad();
                    stop();
                }
                
            }
        }
        
    }
    
    private void interactWithChest() {
        chestInteractionEnd = entity.getWorld().getTime() + 20;
        setChestOpenState(targetChest, true);
        
        entity.lastChest = targetChest;
        
        this.entity.getNavigation().stop();
        this.entity.stopMovement();
        this.entity.setVelocity(Vec3d.ZERO);
        
        var lookTarget = targetChest.toCenterPos();
        this.entity.getLookControl().lookAt(lookTarget.x, lookTarget.y, lookTarget.z);
        
        if (tryInventoryExchange()) {
            entity.onHappy();
        } else {
            emptyChestTimings.put(targetChest, entity.getWorld().getTime());
            entity.onSad();
        }
    }
    
    public abstract boolean tryInventoryExchange();
    
    private BlockPos findClosestChest() {
        
        searchWaitTicks++;
        if (searchWaitTicks < 60)
            return null;
        
        searchWaitTicks = 0;
        
        var time = entity.getWorld().getTime();
        
        var pos = BlockPos.findClosest(entity.getBlockPos(), 12, 12, candidate -> {
            if (time - emptyChestTimings.getOrDefault(candidate, 0L) < 400)
                return false;
            
            var candidateEntity = entity.getWorld().getBlockEntity(candidate);
            return candidateEntity instanceof ChestBlockEntity;
        }).orElse(null);
        
        if (pos == null && !entity.getSearchStartPos().isWithinDistance(entity.getBlockPos(), 8)) {
            pos = BlockPos.findClosest(entity.getSearchStartPos(), 8, 8, candidate -> {
                if (time - emptyChestTimings.getOrDefault(candidate, 0L) < 400)
                    return false;
                
                var candidateEntity = entity.getWorld().getBlockEntity(candidate);
                return candidateEntity instanceof ChestBlockEntity;
            }).orElse(null);
        }
        
        return pos;
        
    }
    
    private void setChestOpenState(BlockPos chestPos, boolean open) {
        
        var soundEvent = SoundEvents.BLOCK_CHEST_CLOSE;
        var soundPos = chestPos.toCenterPos();
        if (open)
            soundEvent = SoundEvents.BLOCK_CHEST_OPEN;
        entity.getWorld().playSound(null, soundPos.x, soundPos.y, soundPos.z, soundEvent, SoundCategory.BLOCKS, 0.5F, entity.getWorld().random.nextFloat() * 0.1F + 0.9F);
        
        if (entity.getWorld().getBlockEntity(chestPos) instanceof ChestBlockEntity chestEntity) {
            entity.getWorld().addSyncedBlockEvent(chestPos, Blocks.CHEST, 1, open ? 1 : 0);
        }
    }
}
