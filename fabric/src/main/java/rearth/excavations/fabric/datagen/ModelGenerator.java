package rearth.excavations.fabric.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.block.Blocks;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;
import net.minecraft.item.BlockItem;
import rearth.excavations.init.BlockContent;
import rearth.excavations.init.ItemGroups;

public class ModelGenerator extends FabricModelProvider {
    public ModelGenerator(FabricDataOutput output) {
        super(output);
    }
    
    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        blockStateModelGenerator.registerStateWithModelReference(BlockContent.ALLAY_CREATOR_BLOCK, Blocks.LIGHTNING_ROD);
        blockStateModelGenerator.registerStateWithModelReference(BlockContent.SHATTERER_BLOCK, Blocks.LIGHTNING_ROD);
        blockStateModelGenerator.registerStateWithModelReference(BlockContent.DIGGER, Blocks.LIGHTNING_ROD);
        blockStateModelGenerator.registerStateWithModelReference(BlockContent.PRIMITIVE_DEEP_DRILL, Blocks.LIGHTNING_ROD);
        
        blockStateModelGenerator.registerRod(BlockContent.WEAK_CHARGE_BLOCK);
        blockStateModelGenerator.registerRod(BlockContent.MEDIUM_CHARGE_BLOCK);
        blockStateModelGenerator.registerRod(BlockContent.STRONG_CHARGE_BLOCK);
        blockStateModelGenerator.registerRod(BlockContent.EXTREME_CHARGE_BLOCK);
        
        blockStateModelGenerator.registerSimpleCubeAll(BlockContent.HARD_STONE);
        blockStateModelGenerator.registerSimpleCubeAll(BlockContent.HARDER_STONE);
        blockStateModelGenerator.registerSimpleCubeAll(BlockContent.HARDERER_STONE);
        blockStateModelGenerator.registerSimpleCubeAll(BlockContent.DEEPER_SLATE);
        blockStateModelGenerator.registerSimpleCubeAll(BlockContent.VERY_DEEP_SLATE);
        blockStateModelGenerator.registerSimpleCubeAll(BlockContent.DEEPEST_SLATE);
        blockStateModelGenerator.registerSimpleCubeAll(BlockContent.REINFORCED_OBSIDIAN);
        
        blockStateModelGenerator.registerSimpleCubeAll(BlockContent.CRACKED_STONE);
        blockStateModelGenerator.registerSimpleCubeAll(BlockContent.SHATTERED_STONE);
        
        blockStateModelGenerator.registerSimpleCubeAll(BlockContent.CRACKED_HARD_STONE);
        blockStateModelGenerator.registerSimpleCubeAll(BlockContent.SHATTERED_HARD_STONE);
        
        blockStateModelGenerator.registerSimpleCubeAll(BlockContent.CRACKED_HARDER_STONE);
        blockStateModelGenerator.registerSimpleCubeAll(BlockContent.SHATTERED_HARDER_STONE);
        
        blockStateModelGenerator.registerSimpleCubeAll(BlockContent.CRACKED_HARDERER_STONE);
        blockStateModelGenerator.registerSimpleCubeAll(BlockContent.SHATTERED_HARDERER_STONE);
        
        blockStateModelGenerator.registerSimpleCubeAll(BlockContent.CRACKED_DEEPSLATE);
        blockStateModelGenerator.registerSimpleCubeAll(BlockContent.SHATTERED_DEEPSLATE);
        
        blockStateModelGenerator.registerSimpleCubeAll(BlockContent.CRACKED_DEEPER_SLATE);
        blockStateModelGenerator.registerSimpleCubeAll(BlockContent.SHATTERED_DEEPER_SLATE);
        
        blockStateModelGenerator.registerSimpleCubeAll(BlockContent.CRACKED_VERY_DEEP_SLATE);
        blockStateModelGenerator.registerSimpleCubeAll(BlockContent.SHATTERED_VERY_DEEP_SLATE);
        
        blockStateModelGenerator.registerSimpleCubeAll(BlockContent.CRACKED_DEEPEST_SLATE);
        blockStateModelGenerator.registerSimpleCubeAll(BlockContent.SHATTERED_DEEPEST_SLATE);
        
        blockStateModelGenerator.registerSimpleCubeAll(BlockContent.CRACKED_REINFORCED_OBSIDIAN);
        blockStateModelGenerator.registerSimpleCubeAll(BlockContent.SHATTERED_REINFORCED_OBSIDIAN);
        
        blockStateModelGenerator.registerSimpleCubeAll(BlockContent.CRACKED_OBSIDIAN);
        blockStateModelGenerator.registerSimpleCubeAll(BlockContent.SHATTERED_OBSIDIAN);
    }
    
    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
    
        for (var item : ItemGroups.registered) {
            if (item.get() instanceof BlockItem) continue;
            itemModelGenerator.register(item.get(), Models.GENERATED);
        }
        
    }
}
