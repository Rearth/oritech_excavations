package rearth.excavations.fabric.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryWrapper;
import rearth.excavations.init.BlockContent;
import rearth.oritech.init.ItemContent;

import java.util.concurrent.CompletableFuture;

public class BlockLootGenerator extends FabricBlockLootTableProvider {
    
    public BlockLootGenerator(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }
    
    @Override
    public void generate() {
        for (var block : BlockContent.autoRegisteredDrops) {
            addDrop(block);
        }
        
        addDrop(BlockContent.NICKEL_CRYSTAL_BLOCK, oreDrops(BlockContent.NICKEL_CRYSTAL_BLOCK, ItemContent.RAW_NICKEL));
        
        addDrop(BlockContent.HARDENED_CRYSTAL_BLOCK, Items.AMETHYST_SHARD);
        addDrop(BlockContent.REINFORCED_CRYSTAL_BLOCK, Items.AMETHYST_SHARD);
        addDrop(BlockContent.CRACKED_STONE, Items.COBBLESTONE);
        addDrop(BlockContent.SHATTERED_STONE, Items.COBBLESTONE);
        addDrop(BlockContent.HARD_STONE, Items.COBBLESTONE);
        addDrop(BlockContent.CRACKED_HARD_STONE, Items.COBBLESTONE);
        addDrop(BlockContent.SHATTERED_HARD_STONE, Items.COBBLESTONE);
        addDrop(BlockContent.HARDER_STONE, Items.COBBLESTONE);
        addDrop(BlockContent.CRACKED_HARDER_STONE, Items.COBBLESTONE);
        addDrop(BlockContent.SHATTERED_HARDER_STONE, Items.COBBLESTONE);
        addDrop(BlockContent.HARDERER_STONE, Items.COBBLED_DEEPSLATE);
        addDrop(BlockContent.CRACKED_HARDERER_STONE, Items.COBBLED_DEEPSLATE);
        addDrop(BlockContent.SHATTERED_HARDERER_STONE, Items.COBBLED_DEEPSLATE);
        addDrop(BlockContent.CRACKED_DEEPSLATE, Items.COBBLED_DEEPSLATE);
        addDrop(BlockContent.SHATTERED_DEEPSLATE, Items.COBBLED_DEEPSLATE);
        addDrop(BlockContent.DEEPER_SLATE, Items.COBBLED_DEEPSLATE);
        addDrop(BlockContent.CRACKED_DEEPER_SLATE, Items.COBBLED_DEEPSLATE);
        addDrop(BlockContent.SHATTERED_DEEPER_SLATE, Items.COBBLED_DEEPSLATE);
        addDrop(BlockContent.VERY_DEEP_SLATE, Items.COBBLED_DEEPSLATE);
        addDrop(BlockContent.CRACKED_VERY_DEEP_SLATE, Items.COBBLED_DEEPSLATE);
        addDrop(BlockContent.SHATTERED_VERY_DEEP_SLATE, Items.COBBLED_DEEPSLATE);
        addDrop(BlockContent.DEEPEST_SLATE, Items.OBSIDIAN);
        addDrop(BlockContent.CRACKED_DEEPEST_SLATE, Items.OBSIDIAN);
        addDrop(BlockContent.SHATTERED_DEEPEST_SLATE, Items.OBSIDIAN);
        addDrop(BlockContent.CRACKED_OBSIDIAN, Items.OBSIDIAN);
        addDrop(BlockContent.SHATTERED_OBSIDIAN, Items.OBSIDIAN);
        addDrop(BlockContent.REINFORCED_OBSIDIAN, Items.OBSIDIAN);
        addDrop(BlockContent.CRACKED_REINFORCED_OBSIDIAN, Items.OBSIDIAN);
        addDrop(BlockContent.SHATTERED_REINFORCED_OBSIDIAN, Items.OBSIDIAN);
    }
}
