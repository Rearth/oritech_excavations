package rearth.excavations.blocks.assembler;

import net.minecraft.block.BlockState;
import net.minecraft.entity.SpawnReason;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import rearth.excavations.client.init.ScreenContent;
import rearth.excavations.init.BlockEntitiesContent;
import rearth.excavations.init.RecipeContent;
import rearth.oritech.api.networking.AdditionalNetworkingProvider;
import rearth.oritech.api.networking.SyncType;
import rearth.oritech.block.base.entity.MachineBlockEntity;
import rearth.oritech.block.base.entity.MultiblockMachineEntity;
import rearth.oritech.client.init.ParticleContent;
import rearth.oritech.init.recipes.OritechRecipe;
import rearth.oritech.init.recipes.OritechRecipeType;
import rearth.oritech.util.Geometry;
import rearth.oritech.util.InventorySlotAssignment;

import java.lang.reflect.Field;
import java.util.List;

public class AllayCreatorBlockEntity extends MultiblockMachineEntity implements AdditionalNetworkingProvider {
    
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
    protected void craftItem(OritechRecipe activeRecipe, List<ItemStack> outputInventory, List<ItemStack> inputInventory) {
        if (activeRecipe.getResults().size() == 1 && activeRecipe.getResults().getFirst().getItem() instanceof SpawnEggItem spawnEggItem) {
            // create corresponding entity
            var resultingStack = activeRecipe.getResults().getFirst();
            var resultingEntity = spawnEggItem.getEntityType(resultingStack);
            if (resultingEntity != null) {
                resultingEntity.spawn((ServerWorld) world, pos.up(2), SpawnReason.BREEDING);
            }
            
            var facing = this.getFacingForMultiblock();
            var offsetLocal = Geometry.rotatePosition(new Vec3d(0.5, 0.8, -0.5), facing);
            var emitPosition = Vec3d.ofCenter(this.pos).add(offsetLocal);
            ParticleContent.PARTICLE_COLLIDE.spawn(this.world, emitPosition);
            
        }
        super.craftItem(activeRecipe, outputInventory, inputInventory);
    }
    
    protected void useEnergy() {
        super.useEnergy();
        if (world.random.nextFloat() > 0.5f) {
            var facing = this.getFacingForMultiblock();
            var offsetLocal = Geometry.rotatePosition(new Vec3d(0.5, 0.8, -0.5), facing);
            var emitPosition = Vec3d.ofCenter(this.pos).add(offsetLocal);
            ParticleContent.ASSEMBLER_WORKING.spawn(this.world, emitPosition, 1);
        }
    }
    
    @Override
    public List<ItemStack> getCraftingResults(OritechRecipe activeRecipe) {
        if (activeRecipe.getResults().size() == 1 && activeRecipe.getResults().getFirst().getItem() instanceof SpawnEggItem) return List.of();
        return super.getCraftingResults(activeRecipe);
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
    
    @Override
    public List<Field> additionalSyncedFields(SyncType syncType) {
        if (syncType.equals(SyncType.TICK)) {
            try {
                return List.of(
                  MachineBlockEntity.class.getDeclaredField("progress"),
                  MachineBlockEntity.class.getDeclaredField("currentRecipe")
                );
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
        }
        return List.of();
    }
}
