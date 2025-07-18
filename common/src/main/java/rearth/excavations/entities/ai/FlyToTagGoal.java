package rearth.excavations.entities.ai;

import net.minecraft.block.Block;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import rearth.excavations.entities.BetterAllayEntity;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public class FlyToTagGoal extends Goal {
    
    private final Map<BlockPos, Long> skippedTargets = new HashMap<>();
    
    public final BetterAllayEntity entity;
    private final TagKey<Block> targetFilter;
    private final float minStartRange;
    private final float stopDist;
    private final float searchDist;
    
    private BlockPos targetStone;
    private int searchIntervalTicks = 0;
    
    // stuck / abort detection
    private int timeWithoutProgress;
    private Vec3d lastPos;
    
    public FlyToTagGoal(BetterAllayEntity entity, TagKey<Block> targetFilter, float minStartRange, float reachDist, float searchDist) {
        this.entity = entity;
        this.targetFilter = targetFilter;
        this.minStartRange = minStartRange;
        this.stopDist = reachDist;
        this.searchDist = searchDist;
        
        // This prevents other movement goals from running at the same time.
        this.setControls(EnumSet.of(Control.MOVE));
    }
    
    @Override
    public boolean canStart() {
        this.targetStone = findClosestTarget();
        return this.targetStone != null && !isCloseToTarget(minStartRange);
    }
    
    @Override
    public boolean shouldContinue() {
        return timeWithoutProgress < getMaxStuckTime() && this.targetStone != null && !isCloseToTarget(stopDist);
    }
    
    @Override
    public void start() {
        if (this.targetStone != null) {
            this.entity.getNavigation().startMovingTo(this.targetStone.getX(), this.targetStone.getY(), this.targetStone.getZ(), BetterAllayEntity.MAX_SPEED);
            
            this.timeWithoutProgress = 0;
            this.lastPos = entity.getPos();
            
            System.out.println("Starting fly to tag: " + targetFilter.toString());
            
            skippedTargets.put(targetStone, entity.getWorld().getTime());
            this.entity.startFlyAnimation();
        } else {
            System.out.println("Fly error");
        }
    }
    
    @Override
    public void tick() {
        super.tick();
        
        if (this.entity.getNavigation().isIdle()) {
            this.timeWithoutProgress++;
            return;
        }
        
        // Check if the entity has moved significantly.
        if (this.entity.getPos().distanceTo(this.lastPos) < 0.01) {
            this.timeWithoutProgress++;
        } else {
            this.timeWithoutProgress = 0;
        }
        
        this.lastPos = this.entity.getPos();
    }
    
    @Override
    public void stop() {
        if (targetStone != null)
            skippedTargets.put(targetStone, entity.getWorld().getTime());
        this.targetStone = null;
        this.entity.getNavigation().stop();
        searchIntervalTicks = getSearchInterval() - 1;
        System.out.println("stopped flying to tag: " + targetFilter.toString());
    }
    
    public BlockPos findClosestTarget() {
        
        searchIntervalTicks++;
        if (searchIntervalTicks < getSearchInterval())
            return null;
        
        searchIntervalTicks = 0;
        
        var candidate = BlockPos.findClosest(this.entity.getBlockPos(), (int) searchDist, (int) (searchDist / 2), pos -> {
            var skipTime = skippedTargets.getOrDefault(pos, 0L);
            if (entity.getWorld().getTime() < skipTime + getSkipTime()) return false;
            return this.entity.getWorld().getBlockState(pos).isIn(targetFilter);
        }).orElse(null);
        
        if (candidate == null && !entity.getSearchStartPos().isWithinDistance(entity.getBlockPos(), 20)) {
            candidate = BlockPos.findClosest(this.entity.getSearchStartPos(), (int) searchDist, (int) (searchDist / 2), pos -> {
                var skipTime = skippedTargets.getOrDefault(pos, 0L);
                if (entity.getWorld().getTime() < skipTime + getSkipTime()) return false;
                return this.entity.getWorld().getBlockState(pos).isIn(targetFilter);
            }).orElse(null);
        }
        
        return candidate;
    }
    
    private int getSkipTime() {
        return 1200;
    }
    
    private int getMaxStuckTime() {
        return 60;
    }
    
    private int getSearchInterval() {
        return 24;
    }
    
    private boolean isCloseToTarget(float range) {
        return this.targetStone != null && this.entity.getBlockPos().isWithinDistance(this.targetStone, range);
    }
}
