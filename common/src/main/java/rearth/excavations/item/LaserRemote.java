package rearth.excavations.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import rearth.excavations.init.ComponentContent;
import rearth.oritech.block.blocks.processing.MachineCoreBlock;
import rearth.oritech.block.entity.interaction.LaserArmBlockEntity;
import rearth.oritech.init.BlockContent;

import java.util.ArrayList;
import java.util.List;

public class LaserRemote extends Item {
    
    public LaserRemote(Settings settings) {
        super(settings);
    }
    
    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        
        if (context.getWorld().isClient()) {
            return ActionResult.SUCCESS;
        }
        
        var stack = context.getStack();
        var targetPos = context.getBlockPos();
        var targetBlockState = context.getWorld().getBlockState(targetPos);
        
        // redirect from cores to centers
        if (targetBlockState.getBlock() instanceof MachineCoreBlock && targetBlockState.get(MachineCoreBlock.USED)) {
            // target the base instead (on laser arms)
            var machineEntity = MachineCoreBlock.getControllerEntity(context.getWorld(), context.getBlockPos());
            if (machineEntity instanceof LaserArmBlockEntity) {
                targetPos = context.getBlockPos().down();
                targetBlockState = context.getWorld().getBlockState(targetPos);
            }
        }
        
        
        // found laser, add to list
        if (targetBlockState.getBlock().equals(BlockContent.LASER_ARM_BLOCK) && context.getWorld().getBlockEntity(targetPos) instanceof LaserArmBlockEntity laserEntity) {
        
            var componentList = stack.getOrDefault(ComponentContent.TARGET_POSITIONS.get(), new ArrayList<BlockPos>());
            if (!componentList.contains(targetPos))
                componentList.add(targetPos);
            
            context.getPlayer().sendMessage(Text.translatable("message.oritech_excavations.source_added"));
            stack.set(ComponentContent.TARGET_POSITIONS.get(), componentList);
            context.getPlayer().setStackInHand(context.getHand(), stack);
            return ActionResult.SUCCESS;
        }
        
        // found other block, redirect selected lasers to this
        if (!targetBlockState.isAir()) {
            var componentList = stack.getOrDefault(ComponentContent.TARGET_POSITIONS.get(), new ArrayList<BlockPos>());
            if (componentList.isEmpty()) {
                context.getPlayer().sendMessage(Text.translatable("message.oritech_excavations.no_laser_sources"));
            } else {
                for (var pos : componentList) {
                    if (context.getWorld().getBlockEntity(pos) instanceof LaserArmBlockEntity laserEntity) {
                        laserEntity.setTargetFromDesignator(targetPos);
                    }
                }
                
                context.getPlayer().sendMessage(Text.translatable("message.oritech_excavations.laser_assigned", componentList.size()));
                return ActionResult.SUCCESS;
            }
        }
        
        return super.useOnBlock(context);
    }
    
    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        
        var storedPositions = stack.getOrDefault(ComponentContent.TARGET_POSITIONS.get(), new ArrayList<BlockPos>());
        
        if (storedPositions.isEmpty()) {
            tooltip.add(Text.translatable("tooltip.oritech_excavations.designator_empty"));
        } else {
            for (var storedPos : storedPositions) {
                tooltip.add(Text.literal(storedPos.toShortString()));
            }
        }
        
        super.appendTooltip(stack, context, tooltip, type);
    }
    
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (user.isSneaking() && !world.isClient) {
            var stack = user.getStackInHand(hand);
            if (stack.contains(ComponentContent.TARGET_POSITIONS.get())) {
                user.sendMessage(Text.translatable("message.oritech_excavations.designator_reset"));
                stack.remove(ComponentContent.TARGET_POSITIONS.get());
            }
            return TypedActionResult.success(user.getStackInHand(hand));
        }
        return super.use(world, user, hand);
    }
}
