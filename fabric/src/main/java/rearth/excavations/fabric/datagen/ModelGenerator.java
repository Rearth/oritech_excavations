package rearth.excavations.fabric.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.block.Blocks;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;
import rearth.excavations.init.BlockContent;
import rearth.excavations.init.ItemGroups;

public class ModelGenerator extends FabricModelProvider {
    public ModelGenerator(FabricDataOutput output) {
        super(output);
    }
    
    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        blockStateModelGenerator.registerStateWithModelReference(BlockContent.ALLAY_CREATOR_BLOCK, Blocks.LIGHTNING_ROD);
    }
    
    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
    
        for (var item : ItemGroups.registered) {
            itemModelGenerator.register(item.get(), Models.GENERATED);
        }
        
    }
}
