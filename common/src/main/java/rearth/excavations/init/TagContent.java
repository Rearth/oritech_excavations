package rearth.excavations.init;

import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import rearth.excavations.Excavation;

public class TagContent {
    
    public static final TagKey<Block> ALLAY_MINEABLE = TagKey.of(RegistryKeys.BLOCK, Excavation.id("allay_mineable"));
    
}
