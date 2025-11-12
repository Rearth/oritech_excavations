package rearth.excavations.init;

import dev.architectury.registry.registries.RegistrySupplier;
import io.wispforest.owo.registration.reflect.BlockRegistryContainer;
import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import rearth.excavations.blocks.ExplosiveChargeBlock;
import rearth.excavations.blocks.allay_creator.AllayCreatorBlock;
import rearth.excavations.blocks.digger.DiggerBlock;
import rearth.excavations.blocks.quarry.PrimitiveDeepDrillBlock;
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
    
    @rearth.oritech.init.BlockContent.UseGeoBlockItem(scale = 0.3f)
    @Rarity(net.minecraft.util.Rarity.UNCOMMON)
    public static final Block DIGGER = new DiggerBlock(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK).nonOpaque());
    
    @rearth.oritech.init.BlockContent.UseGeoBlockItem(scale = 0.7f)
    public static final Block PRIMITIVE_DEEP_DRILL = new PrimitiveDeepDrillBlock(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK).nonOpaque());
    
    public static final Block WEAK_CHARGE_BLOCK = new ExplosiveChargeBlock(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK).nonOpaque(), 2, 250_000, 4);
    public static final Block MEDIUM_CHARGE_BLOCK = new ExplosiveChargeBlock(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK).nonOpaque(), 4, 500_000, 8);
    @Rarity(net.minecraft.util.Rarity.RARE)
    public static final Block STRONG_CHARGE_BLOCK = new ExplosiveChargeBlock(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK).nonOpaque(), 8, 1_000_000, 16);
    @Rarity(net.minecraft.util.Rarity.EPIC)
    public static final Block EXTREME_CHARGE_BLOCK = new ExplosiveChargeBlock(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK).nonOpaque(), 16, 4_000_000, 50);
    
    @rearth.oritech.init.BlockContent.NoAutoDrop
    public static final Block NICKEL_CRYSTAL_BLOCK = new AmethystClusterBlock(7, 3, AbstractBlock.Settings.copy(Blocks.AMETHYST_CLUSTER));
    
    // vanilla
    // stone is .strength(1.5F, 6.0F)
    // deepslate is .strength(3F, 6.0F)
    // obsidian is .strength(50F, 1200F)
    // cracked is quarter strength, shattered is sqrt(x) / 2
    
    @rearth.oritech.init.BlockContent.NoAutoDrop
    public static final Block HARDENED_CRYSTAL_BLOCK = new Block(AbstractBlock.Settings.copy(Blocks.STONE).strength(100F, 15F).mapColor(MapColor.STONE_GRAY));
    @rearth.oritech.init.BlockContent.NoAutoDrop
    public static final Block REINFORCED_CRYSTAL_BLOCK = new Block(AbstractBlock.Settings.copy(Blocks.STONE).strength(250F, 30F).mapColor(MapColor.STONE_GRAY));
    
    @rearth.oritech.init.BlockContent.NoAutoDrop
    public static final Block HARD_STONE = new Block(AbstractBlock.Settings.copy(Blocks.STONE).strength(2.5F, 9F).mapColor(MapColor.STONE_GRAY));
    @rearth.oritech.init.BlockContent.NoAutoDrop
    public static final Block HARDER_STONE = new Block(AbstractBlock.Settings.copy(Blocks.STONE).strength(4F, 12F).mapColor(MapColor.STONE_GRAY));
    @rearth.oritech.init.BlockContent.NoAutoDrop
    public static final Block HARDERER_STONE = new Block(AbstractBlock.Settings.copy(Blocks.STONE).strength(7F, 15F).mapColor(MapColor.DEEPSLATE_GRAY));
    // deepslate would be here (target 9)
    @rearth.oritech.init.BlockContent.NoAutoDrop
    public static final Block DEEPER_SLATE = new Block(AbstractBlock.Settings.copy(Blocks.DEEPSLATE).strength(15F, 50F).mapColor(MapColor.DEEPSLATE_GRAY));
    @rearth.oritech.init.BlockContent.NoAutoDrop
    public static final Block VERY_DEEP_SLATE = new Block(AbstractBlock.Settings.copy(Blocks.DEEPSLATE).strength(25F, 100F).mapColor(MapColor.DEEPSLATE_GRAY));
    @rearth.oritech.init.BlockContent.NoAutoDrop
    public static final Block DEEPEST_SLATE = new Block(AbstractBlock.Settings.copy(Blocks.DEEPSLATE).strength(40F, 200F).mapColor(MapColor.BLACK));
    // obsidian is here (50)
    @rearth.oritech.init.BlockContent.NoAutoDrop
    public static final Block REINFORCED_OBSIDIAN = new Block(AbstractBlock.Settings.copy(Blocks.OBSIDIAN).strength(100F, 3000F).mapColor(MapColor.BLACK));
    
    @rearth.oritech.init.BlockContent.NoAutoDrop
    public static final Block CRACKED_STONE = new Block(AbstractBlock.Settings.copy(Blocks.STONE).strength(1.5F / 4, 2F).mapColor(MapColor.STONE_GRAY));
    @rearth.oritech.init.BlockContent.NoAutoDrop
    public static final Block SHATTERED_STONE = new Block(AbstractBlock.Settings.copy(Blocks.STONE).strength((float) (Math.sqrt(1.5F) / 2f), 2F).mapColor(MapColor.STONE_GRAY));
    @rearth.oritech.init.BlockContent.NoAutoDrop
    public static final Block CRACKED_HARD_STONE = new Block(AbstractBlock.Settings.copy(Blocks.STONE).strength(2.5F / 4, 3F).mapColor(MapColor.STONE_GRAY));
    @rearth.oritech.init.BlockContent.NoAutoDrop
    public static final Block SHATTERED_HARD_STONE = new Block(AbstractBlock.Settings.copy(Blocks.STONE).strength((float) (Math.sqrt(2.5F) / 2f), 1F).mapColor(MapColor.STONE_GRAY));
    @rearth.oritech.init.BlockContent.NoAutoDrop
    public static final Block CRACKED_HARDER_STONE = new Block(AbstractBlock.Settings.copy(Blocks.STONE).strength(3.5F / 4, 4F).mapColor(MapColor.STONE_GRAY));
    @rearth.oritech.init.BlockContent.NoAutoDrop
    public static final Block SHATTERED_HARDER_STONE = new Block(AbstractBlock.Settings.copy(Blocks.STONE).strength((float) (Math.sqrt(3.5F) / 2f), 2F).mapColor(MapColor.STONE_GRAY));
    @rearth.oritech.init.BlockContent.NoAutoDrop
    public static final Block CRACKED_HARDERER_STONE = new Block(AbstractBlock.Settings.copy(Blocks.STONE).strength(5F / 4, 6F).mapColor(MapColor.DEEPSLATE_GRAY));
    @rearth.oritech.init.BlockContent.NoAutoDrop
    public static final Block SHATTERED_HARDERER_STONE = new Block(AbstractBlock.Settings.copy(Blocks.STONE).strength((float) (Math.sqrt(5F) / 2f), 3F).mapColor(MapColor.DEEPSLATE_GRAY));
    @rearth.oritech.init.BlockContent.NoAutoDrop
    public static final Block CRACKED_DEEPSLATE = new Block(AbstractBlock.Settings.copy(Blocks.DEEPSLATE).strength(3F / 4, 7F).mapColor(MapColor.DEEPSLATE_GRAY));
    @rearth.oritech.init.BlockContent.NoAutoDrop
    public static final Block SHATTERED_DEEPSLATE = new Block(AbstractBlock.Settings.copy(Blocks.DEEPSLATE).strength((float) (Math.sqrt(3F) / 2f), 2F).mapColor(MapColor.DEEPSLATE_GRAY));
    @rearth.oritech.init.BlockContent.NoAutoDrop
    public static final Block CRACKED_DEEPER_SLATE = new Block(AbstractBlock.Settings.copy(Blocks.DEEPSLATE).strength(10F / 4, 6F).mapColor(MapColor.DEEPSLATE_GRAY));
    @rearth.oritech.init.BlockContent.NoAutoDrop
    public static final Block SHATTERED_DEEPER_SLATE = new Block(AbstractBlock.Settings.copy(Blocks.DEEPSLATE).strength((float) (Math.sqrt(10F) / 2f), 2F).mapColor(MapColor.DEEPSLATE_GRAY));
    @rearth.oritech.init.BlockContent.NoAutoDrop
    public static final Block CRACKED_VERY_DEEP_SLATE = new Block(AbstractBlock.Settings.copy(Blocks.DEEPSLATE).strength(15F / 4, 7F).mapColor(MapColor.DEEPSLATE_GRAY));
    @rearth.oritech.init.BlockContent.NoAutoDrop
    public static final Block SHATTERED_VERY_DEEP_SLATE = new Block(AbstractBlock.Settings.copy(Blocks.DEEPSLATE).strength((float) (Math.sqrt(15F) / 2f), 3F).mapColor(MapColor.DEEPSLATE_GRAY));
    @rearth.oritech.init.BlockContent.NoAutoDrop
    public static final Block CRACKED_DEEPEST_SLATE = new Block(AbstractBlock.Settings.copy(Blocks.DEEPSLATE).strength(35F / 4, 8F).mapColor(MapColor.BLACK));
    @rearth.oritech.init.BlockContent.NoAutoDrop
    public static final Block SHATTERED_DEEPEST_SLATE = new Block(AbstractBlock.Settings.copy(Blocks.DEEPSLATE).strength((float) (Math.sqrt(35F) / 2f), 4F).mapColor(MapColor.BLACK));
    @rearth.oritech.init.BlockContent.NoAutoDrop
    public static final Block CRACKED_OBSIDIAN = new Block(AbstractBlock.Settings.copy(Blocks.OBSIDIAN).strength(50F / 4, 9F).mapColor(MapColor.BLACK));
    @rearth.oritech.init.BlockContent.NoAutoDrop
    public static final Block SHATTERED_OBSIDIAN = new Block(AbstractBlock.Settings.copy(Blocks.OBSIDIAN).strength((float) (Math.sqrt(50F) / 2f), 5F).mapColor(MapColor.BLACK));
    @rearth.oritech.init.BlockContent.NoAutoDrop
    public static final Block CRACKED_REINFORCED_OBSIDIAN = new Block(AbstractBlock.Settings.copy(Blocks.OBSIDIAN).strength(100F / 4, 20F).mapColor(MapColor.BLACK));
    @rearth.oritech.init.BlockContent.NoAutoDrop
    public static final Block SHATTERED_REINFORCED_OBSIDIAN = new Block(AbstractBlock.Settings.copy(Blocks.OBSIDIAN).strength((float) (Math.sqrt(100F) / 2f), 10F).mapColor(MapColor.BLACK));
    
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
        
        var settings = new Item.Settings();
        
        return new OritechGeoItem(block, settings, scale, identifier);
    }
    
    public BlockItem createBlockItem(Block block, String identifier) {
        var settings = new Item.Settings();
        return new BlockItem(block, settings);
    }
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface Rarity {
        net.minecraft.util.Rarity value();
    }
    
}
