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
import net.minecraft.util.Rarity;
import org.jetbrains.annotations.Nullable;
import rearth.excavations.blocks.ExplosiveChargeBlock;
import rearth.excavations.blocks.allay_creator.AllayCreatorBlock;
import rearth.excavations.blocks.shatterer.ShattererBlock;
import rearth.oritech.item.OritechGeoItem;
import rearth.oritech.util.registry.ArchitecturyBlockRegistryContainer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

public class BlockContent implements ArchitecturyBlockRegistryContainer {
    
    public static Set<Block> autoRegisteredDrops = new HashSet<>();
    
    @rearth.oritech.init.BlockContent.UseGeoBlockItem(scale = 0.7f)
    @Rarity(net.minecraft.util.Rarity.UNCOMMON)
    public static final Block ALLAY_CREATOR_BLOCK = new AllayCreatorBlock(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK).nonOpaque());
    
    @rearth.oritech.init.BlockContent.UseGeoBlockItem(scale = 0.4f)
    @Rarity(net.minecraft.util.Rarity.RARE)
    public static final Block SHATTERER_BLOCK = new ShattererBlock(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK).nonOpaque());
    
    public static final Block WEAK_CHARGE_BLOCK = new ExplosiveChargeBlock(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK).nonOpaque(), 2, 250_000, 4);
    public static final Block MEDIUM_CHARGE_BLOCK = new ExplosiveChargeBlock(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK).nonOpaque(), 4, 500_000, 8);
    @Rarity(net.minecraft.util.Rarity.RARE)
    public static final Block STRONG_CHARGE_BLOCK = new ExplosiveChargeBlock(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK).nonOpaque(), 8, 1_000_000, 16);
    @Rarity(net.minecraft.util.Rarity.EPIC)
    public static final Block EXTREME_CHARGE_BLOCK = new ExplosiveChargeBlock(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK).nonOpaque(), 16, 4_000_000, 50);
    
    @Override
    public void postProcessField(String namespace, Block value, String identifier, Field field, RegistrySupplier<Block> supplier) {
        
        if (field.isAnnotationPresent(BlockRegistryContainer.NoBlockItem.class)) return;
        
        net.minecraft.util.Rarity rarity = null;
        
        if (field.isAnnotationPresent(Rarity.class))
            rarity = field.getAnnotation(Rarity.class).value();
        
        if (field.isAnnotationPresent(rearth.oritech.init.BlockContent.UseGeoBlockItem.class)) {
            Registry.register(Registries.ITEM, Identifier.of(namespace, identifier), getGeoBlockItem(value, identifier, field.getAnnotation(rearth.oritech.init.BlockContent.UseGeoBlockItem.class).scale(), rarity));
        } else {
            Registry.register(Registries.ITEM, Identifier.of(namespace, identifier), createBlockItem(value, identifier, rarity));
        }
        
        if (!field.isAnnotationPresent(rearth.oritech.init.BlockContent.NoAutoDrop.class)) {
            autoRegisteredDrops.add(value);
        }
        
        ItemGroups.registered.add(value::asItem);
    }
    
    private BlockItem getGeoBlockItem(Block block, String identifier, float scale, @Nullable net.minecraft.util.Rarity rarity) {
        
        var settings = new Item.Settings();
        if (rarity != null)
            settings.rarity(rarity);
        
        return new OritechGeoItem(block, settings, scale, identifier);
    }
    
    public BlockItem createBlockItem(Block block, String identifier, @Nullable net.minecraft.util.Rarity rarity) {
        var settings = new Item.Settings();
        if (rarity != null)
            settings.rarity(rarity);
        return new BlockItem(block, settings);
    }
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface Rarity {
        net.minecraft.util.Rarity value();
    }
    
}
