package rearth.excavations.blocks.assembler;

import net.minecraft.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import rearth.oritech.block.base.block.MultiblockMachine;

public class AllayCreatorBlock extends MultiblockMachine {
    
    public AllayCreatorBlock(Settings settings) {
        super(settings);
    }
    
    @Override
    public @NotNull Class<? extends BlockEntity> getBlockEntityType() {
        return AllayCreatorBlockEntity.class;
    }
}
