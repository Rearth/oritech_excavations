package rearth.excavations.init;

import dev.architectury.registry.registries.RegistrySupplier;
import io.wispforest.owo.registration.reflect.BlockRegistryContainer;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import rearth.excavations.blocks.ExplosiveChargeBlock;
import rearth.excavations.blocks.allay_creator.AllayCreatorBlock;
import rearth.excavations.blocks.shatterer.ShattererBlock;
import rearth.oritech.item.OritechGeoItem;
import rearth.oritech.util.registry.ArchitecturyBlockRegistryContainer;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

public class BlockContent implements ArchitecturyBlockRegistryContainer {
    
    public static Set<Block> autoRegisteredDrops = new HashSet<>();
    
    @rearth.oritech.init.BlockContent.UseGeoBlockItem(scale = 0.7f)
    public static final Block ALLAY_CREATOR_BLOCK = new AllayCreatorBlock(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK).nonOpaque());
    
    @rearth.oritech.init.BlockContent.UseGeoBlockItem(scale = 0.4f)
    public static final Block SHATTERER_BLOCK = new ShattererBlock(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK).nonOpaque());
    
    public static final Block WEAK_CHARGE_BLOCK = new ExplosiveChargeBlock(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK).nonOpaque(), 3);
    public static final Block MEDIUM_CHARGE_BLOCK = new ExplosiveChargeBlock(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK).nonOpaque(), 5);
    public static final Block STRONG_CHARGE_BLOCK = new ExplosiveChargeBlock(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK).nonOpaque(), 7);
    public static final Block EXTREME_CHARGE_BLOCK = new ExplosiveChargeBlock(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK).nonOpaque(), 9);
    
    @Override
    public void postProcessField(String namespace, Block value, String identifier, Field field, RegistrySupplier<Block> supplier) {
        
        if (field.isAnnotationPresent(BlockRegistryContainer.NoBlockItem.class)) return;
        
        if (field.isAnnotationPresent(rearth.oritech.init.BlockContent.UseGeoBlockItem.class)) {
            Registry.register(Registries.ITEM, Identifier.of(namespace, identifier), getGeoBlockItem(value, identifier, field.getAnnotation(rearth.oritech.init.BlockContent.UseGeoBlockItem.class).scale()));
        } else {
            Registry.register(Registries.ITEM, Identifier.of(namespace, identifier), createBlockItem(value, identifier));
        }
        
        if (!field.isAnnotationPresent(rearth.oritech.init.BlockContent.NoAutoDrop.class)) {
            autoRegisteredDrops.add(value);
        }
        
        ItemGroups.registered.add(value::asItem);
    }
    
    private BlockItem getGeoBlockItem(Block block, String identifier, float scale) {
        return new OritechGeoItem(block, new Item.Settings(), scale, identifier);
    }
    
}
