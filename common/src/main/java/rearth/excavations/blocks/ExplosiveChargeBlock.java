package rearth.excavations.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FacingBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.state.StateManager;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.Nullable;
import rearth.excavations.blocks.shatterer.ShattererBlockEntity;

import java.util.List;
import java.util.function.BiConsumer;

public class ExplosiveChargeBlock extends FacingBlock {
    
    public final int explosionPower;
    public final int rfNeeded;
    public final int shatterPower;
    
    public ExplosiveChargeBlock(Settings settings, int explosionPower, int rfNeeded, int shatterPower) {
        super(settings);
        this.explosionPower = explosionPower;
        this.rfNeeded = rfNeeded;
        this.shatterPower = shatterPower;
        setDefaultState(getDefaultState().with(FACING, Direction.UP));
    }
    
    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType options) {
        tooltip.add(Text.translatable("tooltip.oritech_excavations.seismic_charge").formatted(Formatting.ITALIC, Formatting.GRAY));
        super.appendTooltip(stack, context, tooltip, options);
    }
    
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(FACING);
    }
    
    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        return super.getPlacementState(ctx).with(FACING, ctx.getSide());
    }
    
    @Override
    protected MapCodec<? extends FacingBlock> getCodec() {
        return null;
    }
    
    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        
        if (stack.isOf(Items.FLINT_AND_STEEL)) {
            explode(world, pos);
            return ItemActionResult.SUCCESS;
        }
        
        return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
    }
    
    // allow redstone to connect
    @Override
    protected boolean hasComparatorOutput(BlockState state) {
        return true;
    }
    
    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (world.isReceivingRedstonePower(pos)) {
            explode(world, pos);
        }
        super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);
    }
    
    @Override
    protected void onExploded(BlockState state, World world, BlockPos pos, Explosion explosion, BiConsumer<ItemStack, BlockPos> stackMerger) {
        explode(world, pos);
        super.onExploded(state, world, pos, explosion, stackMerger);
    }
    
    private void explode(World world, BlockPos pos) {
        ShattererBlockEntity.createShatteredArea(world, pos, explosionPower);
    }
    
}
