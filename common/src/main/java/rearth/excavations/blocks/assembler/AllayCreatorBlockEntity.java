package rearth.excavations.blocks.assembler;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import rearth.excavations.client.ScreenContent;
import rearth.excavations.init.BlockEntitiesContent;
import rearth.excavations.init.RecipeContent;
import rearth.oritech.block.base.entity.MultiblockMachineEntity;
import rearth.oritech.init.recipes.OritechRecipeType;
import rearth.oritech.util.InventorySlotAssignment;

import java.util.List;

public class AllayCreatorBlockEntity extends MultiblockMachineEntity {
    
    public AllayCreatorBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntitiesContent.ALLAY_CREATOR_BLOCK_ENTITY, pos, state, 256);
    }
    
    @Override
    protected OritechRecipeType getOwnRecipeType() {
        return RecipeContent.ALLAY_CREATOR;
    }
    
    @Override
    public InventorySlotAssignment getSlotAssignments() {
        return new InventorySlotAssignment(0, 3, 3, 1);
    }
    
    @Override
    public List<GuiSlot> getGuiSlots() {
        return List.of(
          new GuiSlot(0, 20, 10),
          new GuiSlot(1, 20, 30),
          new GuiSlot(2, 20, 50),
          new GuiSlot(3, 70, 30, true));
    }
    
    @Override
    public ScreenHandlerType<?> getScreenHandlerType() {
        return ScreenContent.ALLAY_CREATOR_SCREEN;
    }
    
    @Override
    public boolean inputOptionsEnabled() {
        return true;
    }
    
    @Override
    public int getInventorySize() {
        return 4;
    }
    
    @Override
    public List<Vec3i> getAddonSlots() {
        return List.of();
    }
    
    @Override
    public List<Vec3i> getCorePositions() {
        return List.of(
          new Vec3i(0, 1, 0),    // middle
          new Vec3i(0, 0, -1),    // left
          new Vec3i(0, 1, -1),
          new Vec3i(1, 0, -1),    // back left
          new Vec3i(1, 1, -1),
          new Vec3i(1, 0, 0),    // back middle
          new Vec3i(1, 1, 0)
        );
    }
}
