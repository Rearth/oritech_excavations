package rearth.excavations.fabric.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import rearth.excavations.init.TagContent;

import java.util.concurrent.CompletableFuture;

public class BlockTagGenerator extends FabricTagProvider<Block> {
    
    public BlockTagGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, RegistryKeys.BLOCK, registriesFuture);
    }
    
    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(TagContent.ALLAY_MINEABLE)
          .addOptionalTag(ConventionalBlockTags.ORES)
          .addOptionalTag(ConventionalBlockTags.STONES)
          .addOptionalTag(ConventionalBlockTags.DEEPSLATE_COBBLESTONES)
          .addOptionalTag(ConventionalBlockTags.GRAVELS)
          .addOptionalTag(ConventionalBlockTags.SANDS)
          .addOptionalTag(ConventionalBlockTags.SANDSTONE_BLOCKS)
          .addOptionalTag(BlockTags.DIRT)
          .add(Blocks.GRASS_BLOCK)
          .add(Blocks.CLAY)
          .add(Blocks.MUD)
          .addOptionalTag(BlockTags.OVERWORLD_CARVER_REPLACEABLES);
    }
}
