package rearth.excavations.fabric.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import rearth.excavations.init.BlockContent;
import rearth.excavations.init.TagContent;

import java.util.concurrent.CompletableFuture;

public class BlockTagGenerator extends FabricTagProvider<Block> {
    
    public BlockTagGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, RegistryKeys.BLOCK, registriesFuture);
    }
    
    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        
        getOrCreateTagBuilder(TagContent.HARDER_STONES)
          .add(BlockContent.SHATTERED_STONE)
          .add(BlockContent.HARD_STONE)
          .add(BlockContent.HARDER_STONE)
          .add(BlockContent.HARDERER_STONE)
          .add(BlockContent.DEEPER_SLATE)
          .add(BlockContent.VERY_DEEP_SLATE)
          .add(BlockContent.DEEPEST_SLATE)
          .add(BlockContent.REINFORCED_OBSIDIAN)
          .add(BlockContent.CRACKED_STONE)
          .add(BlockContent.SHATTERED_HARD_STONE)
          .add(BlockContent.CRACKED_HARD_STONE)
          .add(BlockContent.SHATTERED_HARDER_STONE)
          .add(BlockContent.CRACKED_HARDER_STONE)
          .add(BlockContent.SHATTERED_HARDERER_STONE)
          .add(BlockContent.CRACKED_HARDERER_STONE)
          .add(BlockContent.CRACKED_DEEPSLATE)
          .add(BlockContent.SHATTERED_DEEPSLATE)
          .add(BlockContent.CRACKED_DEEPER_SLATE)
          .add(BlockContent.SHATTERED_DEEPER_SLATE)
          .add(BlockContent.CRACKED_VERY_DEEP_SLATE)
          .add(BlockContent.SHATTERED_VERY_DEEP_SLATE)
          .add(BlockContent.CRACKED_DEEPEST_SLATE)
          .add(BlockContent.SHATTERED_DEEPEST_SLATE)
          .add(BlockContent.CRACKED_OBSIDIAN)
          .add(BlockContent.SHATTERED_OBSIDIAN)
          .add(BlockContent.CRACKED_REINFORCED_OBSIDIAN)
          .add(BlockContent.HARDENED_CRYSTAL_BLOCK)
          .add(BlockContent.REINFORCED_CRYSTAL_BLOCK)
          .add(BlockContent.SHATTERED_REINFORCED_OBSIDIAN);
        
        getOrCreateTagBuilder(rearth.oritech.init.TagContent.LASER_FAST_BREAKING)
          .add(BlockContent.HARDENED_CRYSTAL_BLOCK)
            .add(BlockContent.REINFORCED_CRYSTAL_BLOCK);
        
        getOrCreateTagBuilder(TagContent.ALLAY_MINEABLE)
          .addOptionalTag(ConventionalBlockTags.ORES)
          .addOptionalTag(ConventionalBlockTags.STONES)
          .addOptionalTag(ConventionalBlockTags.DEEPSLATE_COBBLESTONES)
          .addOptionalTag(ConventionalBlockTags.GRAVELS)
          .addOptionalTag(ConventionalBlockTags.SANDS)
          .addOptionalTag(ConventionalBlockTags.SANDSTONE_BLOCKS)
          .addOptionalTag(ConventionalBlockTags.BUDDING_BLOCKS)
          .addOptionalTag(BlockTags.DIRT)
          .addOptionalTag(TagContent.HARDER_STONES)
          .add(Blocks.GRASS_BLOCK)
          .add(Blocks.CLAY)
          .add(Blocks.MUD)
          .add(Blocks.AMETHYST_BLOCK)
          .add(Blocks.BUDDING_AMETHYST)
          .addOptionalTag(BlockTags.OVERWORLD_CARVER_REPLACEABLES);
        
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE)
          .add(BlockContent.ALLAY_CREATOR_BLOCK)
          .add(BlockContent.SHATTERER_BLOCK)
          .add(BlockContent.DIGGER)
          .add(BlockContent.PRIMITIVE_DEEP_DRILL)
          .add(BlockContent.WEAK_CHARGE_BLOCK)
          .add(BlockContent.MEDIUM_CHARGE_BLOCK)
          .add(BlockContent.STRONG_CHARGE_BLOCK)
          .add(BlockContent.EXTREME_CHARGE_BLOCK)
          .add(BlockContent.NICKEL_CRYSTAL_BLOCK)
          .addOptionalTag(TagContent.HARDER_STONES);
    }
}
