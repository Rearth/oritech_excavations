package rearth.excavations.blocks.excavation_controller;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.state.StateManager;
import org.jetbrains.annotations.NotNull;
import rearth.oritech.block.base.block.MachineBlock;

import static rearth.oritech.block.blocks.storage.UnstableContainerBlock.SETUP_DONE;

public class DigControllerBlock extends MachineBlock {
    
    public DigControllerBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(SETUP_DONE, false));
    }
    
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(SETUP_DONE);
    }
    
    @Override
    public @NotNull Class<? extends BlockEntity> getBlockEntityType() {
        return DigControllerBlockEntity.class;
    }
}
