package rearth.excavations.blocks.shatterer;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import rearth.excavations.blocks.ExplosiveChargeBlock;
import rearth.excavations.init.BlockEntitiesContent;
import rearth.oritech.block.base.block.MultiblockMachine;
import rearth.oritech.block.blocks.processing.MachineCoreBlock;
import rearth.oritech.init.ItemContent;

import static rearth.oritech.block.blocks.processing.MachineCoreBlock.getControllerPos;

public class ShattererBlock extends MultiblockMachine {
    
    public ShattererBlock(Settings settings) {
        super(settings);
    }
    
    @Override
    public @NotNull Class<? extends BlockEntity> getBlockEntityType() {
        return ShattererBlockEntity.class;
    }
    
    @Override
    public ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        
        if (!world.isClient && stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof ExplosiveChargeBlock) {
            
            if (state.getBlock() instanceof MachineCoreBlock coreBlock) {
                pos = getControllerPos(world, pos);
            }
            
            var shatterer = world.getBlockEntity(pos, BlockEntitiesContent.SHATTERER_BLOCK_ENTITY);
            if (shatterer.isPresent()) {
                var shattererInv = shatterer.get().inventory;
                if (shattererInv.isEmpty()) {
                    shattererInv.heldStacks.set(0, stack.copyWithCount(1));
                    shattererInv.markDirty();
                    stack.decrement(1);
                    player.setStackInHand(hand, stack);
                }
            }
            
        }
        
        return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
        
    }
    
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        
        var shatterer = world.getBlockEntity(pos, BlockEntitiesContent.SHATTERER_BLOCK_ENTITY);
        if (!world.isClient && state.get(ASSEMBLED) && shatterer.isPresent()) {
            shatterer.get().getStatus(player);
            return ActionResult.SUCCESS;
        }
        
        return super.onUse(state, world, pos, player, hit);
    }
}
