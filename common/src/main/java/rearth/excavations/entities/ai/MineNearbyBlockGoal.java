package rearth.excavations.entities.ai;

import net.minecraft.block.Block;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import rearth.excavations.entities.BetterAllayEntity;
import rearth.excavations.init.TagContent;

import java.util.EnumSet;

public class MineNearbyBlockGoal extends Goal {
    
    private final BetterAllayEntity entity;
    private BlockPos targetBlock;
    
    private int timer = 0;
    private int workTime = 20;
    
    public MineNearbyBlockGoal(BetterAllayEntity entity) {
        this.entity = entity;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }
    
    @Override
    public boolean canStart() {
        if (!entity.canStartMining()) return false;
        
        this.targetBlock = findClosestStone();
        return this.targetBlock != null && this.entity.getPos().isInRange(this.targetBlock.toCenterPos(), 2.0);
    }
    
    private BlockPos findClosestStone() {
        return BlockPos.findClosest(this.entity.getBlockPos(), 3, 3, (pos) ->
                                                                       this.entity.getWorld().getBlockState(pos).isIn(TagContent.ALLAY_MINEABLE))
                 .orElse(null);
    }
    
    @Override
    public boolean shouldContinue() {
        return this.targetBlock != null && timer <= workTime && this.entity.getPos().isInRange(this.targetBlock.toCenterPos(), 2.5);
    }
    
    @Override
    public void start() {
        this.timer = 0;
        this.entity.getNavigation().stop();
        this.entity.stopMovement();
        this.entity.setVelocity(Vec3d.ZERO);
        
        this.entity.startWorkAnimation();
        
        if (targetBlock != null) {
            var lookTarget = targetBlock.toCenterPos();
            this.entity.getLookControl().lookAt(lookTarget.x, lookTarget.y, lookTarget.z);
            
            // calculate break time
            var targetState = entity.getWorld().getBlockState(targetBlock);
            var tool = entity.getSyncedTool();
            var speed = tool.getItem().getMiningSpeed(tool, targetState);
            System.out.println("speed: " + speed);
            var hardness = targetState.getHardness(entity.getWorld(), targetBlock);
            var totalTime = hardness / speed * 20;
            
            if (entity.getWorld() instanceof ServerWorld serverWorld && entity.tryUseBoost()) {
                totalTime = (float) Math.sqrt(totalTime);
                var spawnAt = entity.getEyePos().addRandom(entity.getRandom(), 0.4f);
                serverWorld.spawnParticles(ParticleTypes.TRIAL_SPAWNER_DETECTION, spawnAt.x, spawnAt.y, spawnAt.z, 2, 0, 0.3f, 0, 0);
            }
            
            workTime = (int) totalTime + 1;
        }
        System.out.println("starting mine goal");
    }
    
    @Override
    public void tick() {
        // Increment the timer each tick.
        this.timer++;
        
        if (this.entity.getWorld() instanceof ServerWorld serverWorld) {
            var progress = (int) (this.timer / (double) workTime * 10.0) - 1;    // range 0-9
            progress = Math.max(0, progress); // Clamp to a minimum of 0
            serverWorld.setBlockBreakingInfo(entity.getId(), targetBlock, progress);
        }
        
        if (this.timer >= workTime) {
            breakTargetBlock();
        }
    }
    
    private void breakTargetBlock() {
        var targetState = this.entity.getWorld().getBlockState(this.targetBlock);
        if (targetState.isIn(TagContent.ALLAY_MINEABLE)) {
            this.entity.getWorld().breakBlock(this.targetBlock, false, this.entity);
            var tool = this.entity.getSyncedTool();
            tool.getItem().postMine(tool, entity.getWorld(), targetState, targetBlock, this.entity);
            tool.damage(5, this.entity, EquipmentSlot.MAINHAND);
            
            this.entity.setSyncedTool(tool);
            
            var dropped = Block.getDroppedStacks(targetState, (ServerWorld) entity.getWorld(), targetBlock, null, entity, tool);
            for (var drop : dropped) {
                var remains = this.entity.getInventory().addStack(drop);
                entity.getWorld().spawnEntity(new ItemEntity(entity.getWorld(), entity.prevX, entity.prevY, entity.prevZ, remains));
            }
            
            System.out.println(entity.getInventory().getHeldStacks());
            
            if (this.entity.getWorld() instanceof ServerWorld serverWorld) {
                serverWorld.setBlockBreakingInfo(entity.getId(), targetBlock, 0);
                serverWorld.spawnParticles(new BlockStateParticleEffect(ParticleTypes.BLOCK, targetState),
                  this.targetBlock.getX() + 0.5, this.targetBlock.getY() + 0.5, this.targetBlock.getZ() + 0.5,
                  3, 0.3, 0.3, 0.3, 0.1);
                serverWorld.playSound(null, this.targetBlock, targetState.getSoundGroup().getBreakSound(), SoundCategory.BLOCKS, 1.0f, 1.0f);
            }
        }
    }
    
    @Override
    public void stop() {
        
        // reset state
        this.timer = 0;
        this.targetBlock = null;
        
        System.out.println("stopped mine goal");
    }
    
}
