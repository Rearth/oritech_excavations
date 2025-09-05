package rearth.excavations.init;

import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import rearth.excavations.Excavation;
import rearth.excavations.blocks.allay_creator.AllayCreatorBlockEntity;
import rearth.excavations.blocks.digger.DiggerBlockEntity;
import rearth.excavations.blocks.shatterer.ShattererBlockEntity;
import rearth.oritech.api.energy.EnergyApi;
import rearth.oritech.api.fluid.FluidApi;
import rearth.oritech.api.item.ItemApi;
import rearth.oritech.util.registry.ArchitecturyRegistryContainer;

import java.lang.reflect.Field;

public class BlockEntitiesContent implements ArchitecturyRegistryContainer<BlockEntityType<?>> {
    
    @rearth.oritech.init.BlockEntitiesContent.AssignSidedInventory
    @rearth.oritech.init.BlockEntitiesContent.AssignSidedEnergy
    public static final BlockEntityType<AllayCreatorBlockEntity> ALLAY_CREATOR_BLOCK_ENTITY = BlockEntityType.Builder.create(AllayCreatorBlockEntity::new, BlockContent.ALLAY_CREATOR_BLOCK).build(null);
    
    @rearth.oritech.init.BlockEntitiesContent.AssignSidedInventory
    @rearth.oritech.init.BlockEntitiesContent.AssignSidedEnergy
    public static final BlockEntityType<ShattererBlockEntity> SHATTERER_BLOCK_ENTITY = BlockEntityType.Builder.create(ShattererBlockEntity::new, BlockContent.SHATTERER_BLOCK).build(null);
    
    @rearth.oritech.init.BlockEntitiesContent.AssignSidedInventory
    @rearth.oritech.init.BlockEntitiesContent.AssignSidedEnergy
    public static final BlockEntityType<DiggerBlockEntity> DIGGER_BLOCK_ENTITY = BlockEntityType.Builder.create(DiggerBlockEntity::new, BlockContent.DIGGER).build(null);
    
    
    @Override
    public RegistryKey<Registry<BlockEntityType<?>>> getRegistryType() {
        return RegistryKeys.BLOCK_ENTITY_TYPE;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public Class<BlockEntityType<?>> getTargetFieldType() {
        return (Class<BlockEntityType<?>>) (Object) BlockEntityType.class;
    }
    
    @Override
    public void postProcessField(String namespace, BlockEntityType<?> value, String identifier, Field field, RegistrySupplier<BlockEntityType<?>> supplier) {
        
        if (EnergyApi.BLOCK == null) {
            Excavation.LOGGER.error("Oritech APIs not initialized!");
        }
        
        if (EnergyApi.BLOCK != null && field.isAnnotationPresent(rearth.oritech.init.BlockEntitiesContent.AssignSidedEnergy.class))
            EnergyApi.BLOCK.registerBlockEntity(() -> value);
        
        if (FluidApi.BLOCK != null && field.isAnnotationPresent(rearth.oritech.init.BlockEntitiesContent.AssignSidedFluid.class))
            FluidApi.BLOCK.registerBlockEntity(() -> value);
        
        if (ItemApi.BLOCK != null && field.isAnnotationPresent(rearth.oritech.init.BlockEntitiesContent.AssignSidedInventory.class))
            ItemApi.BLOCK.registerBlockEntity(() -> value);
        
    }
}
