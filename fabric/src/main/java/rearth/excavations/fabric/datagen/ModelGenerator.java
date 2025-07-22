package rearth.excavations.fabric.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;
import rearth.excavations.init.ItemGroups;

public class ModelGenerator extends FabricModelProvider {
    public ModelGenerator(FabricDataOutput output) {
        super(output);
    }
    
    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
    
    }
    
    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
    
        for (var item : ItemGroups.registered) {
            itemModelGenerator.register(item.get(), Models.GENERATED);
        }
        
    }
}
