package rearth.excavations.init;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import rearth.excavations.Excavation;

public class TagContent {
    
    public static final TagKey<Block> ALLAY_MINEABLE = TagKey.of(RegistryKeys.BLOCK, Excavation.id("allay_mineable"));
    public static final TagKey<Block> HARDER_STONES = TagKey.of(RegistryKeys.BLOCK, Excavation.id("harder_stones"));
    public static final TagKey<Item> ALLAY_BOOSTERS = TagKey.of(RegistryKeys.ITEM, Excavation.id("allay_booster"));
    
    public static final TagKey<Item> RANDOS = TagKey.of(RegistryKeys.ITEM, Excavation.id("randos"));
    
}
