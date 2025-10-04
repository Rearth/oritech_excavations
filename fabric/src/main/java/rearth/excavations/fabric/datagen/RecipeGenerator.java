package rearth.excavations.fabric.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import rearth.excavations.Excavation;
import rearth.excavations.init.BlockContent;
import rearth.excavations.init.RecipeContent;
import rearth.excavations.init.TagContent;
import rearth.oritech.Oritech;
import rearth.oritech.init.ItemContent;
import rearth.oritech.init.recipes.OritechRecipe;
import rearth.oritech.util.FluidIngredient;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class RecipeGenerator extends FabricRecipeProvider {
    
    public RecipeGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }
    
    @Override
    public void generate(RecipeExporter exporter) {
        addAllayCreatorRecipe(exporter, 200, List.of(Ingredient.ofItems(ItemContent.UNHOLY_INTELLIGENCE), Ingredient.ofItems(Items.IRON_INGOT), Ingredient.ofItems(Items.IRON_INGOT)), new ItemStack(rearth.excavations.init.ItemContent.BETTER_ALLAY_EGG), "mecha_allay");
        
        // time here is the hardness used to calculate fracturizing depth
        addShattererRecipe(exporter, 1, Ingredient.fromTag(TagContent.RANDOS), BlockContent.CRACKED_STONE, "randos");
        
        addShattererRecipe(exporter, 1, Ingredient.ofItems(Items.STONE), BlockContent.CRACKED_STONE, "crackstone");
        addShattererRecipe(exporter, 1, Ingredient.ofItems(BlockContent.CRACKED_STONE), BlockContent.SHATTERED_STONE, "shatteredstone");
        
        addShattererRecipe(exporter, 2, Ingredient.ofItems(BlockContent.HARD_STONE), BlockContent.CRACKED_HARD_STONE, "crackhardstone");
        addShattererRecipe(exporter, 2, Ingredient.ofItems(BlockContent.CRACKED_HARD_STONE), BlockContent.SHATTERED_HARD_STONE, "shatteredhardstone");
        
        addShattererRecipe(exporter, 3, Ingredient.ofItems(BlockContent.HARDER_STONE), BlockContent.CRACKED_HARDER_STONE, "crackharderstone");
        addShattererRecipe(exporter, 3, Ingredient.ofItems(BlockContent.CRACKED_HARDER_STONE), BlockContent.SHATTERED_HARDER_STONE, "shatteredharderstone");
        
        addShattererRecipe(exporter, 4, Ingredient.ofItems(BlockContent.HARDERER_STONE), BlockContent.CRACKED_HARDERER_STONE, "crackhardererstone");
        addShattererRecipe(exporter, 4, Ingredient.ofItems(BlockContent.CRACKED_HARDERER_STONE), BlockContent.SHATTERED_HARDERER_STONE, "shatteredhardererstone");
        
        addShattererRecipe(exporter, 5, Ingredient.ofItems(Blocks.DEEPSLATE), BlockContent.CRACKED_DEEPSLATE, "crackdeepslate");
        addShattererRecipe(exporter, 5, Ingredient.ofItems(BlockContent.CRACKED_DEEPSLATE), BlockContent.SHATTERED_DEEPSLATE, "shattereddeepslate");
        
        addShattererRecipe(exporter, 6, Ingredient.ofItems(BlockContent.DEEPER_SLATE), BlockContent.CRACKED_DEEPER_SLATE, "crackdeeperslate");
        addShattererRecipe(exporter, 6, Ingredient.ofItems(BlockContent.CRACKED_DEEPER_SLATE), BlockContent.SHATTERED_DEEPER_SLATE, "shattereddeeperslate");
        
        addShattererRecipe(exporter, 8, Ingredient.ofItems(BlockContent.VERY_DEEP_SLATE), BlockContent.CRACKED_VERY_DEEP_SLATE, "crackverydeepslate");
        addShattererRecipe(exporter, 8, Ingredient.ofItems(BlockContent.CRACKED_VERY_DEEP_SLATE), BlockContent.SHATTERED_VERY_DEEP_SLATE, "shatteredverydeepslate");
        
        addShattererRecipe(exporter, 12, Ingredient.ofItems(BlockContent.DEEPEST_SLATE), BlockContent.CRACKED_DEEPEST_SLATE, "crackdeepestslate");
        addShattererRecipe(exporter, 12, Ingredient.ofItems(BlockContent.CRACKED_DEEPEST_SLATE), BlockContent.SHATTERED_DEEPEST_SLATE, "shattereddeepestslate");
        
        addShattererRecipe(exporter, 16, Ingredient.ofItems(Blocks.OBSIDIAN), BlockContent.CRACKED_OBSIDIAN, "crackobsidian");
        addShattererRecipe(exporter, 16, Ingredient.ofItems(BlockContent.CRACKED_OBSIDIAN), BlockContent.SHATTERED_OBSIDIAN, "shatteredobsidian");
        
        addShattererRecipe(exporter, 25, Ingredient.ofItems(BlockContent.REINFORCED_OBSIDIAN), BlockContent.CRACKED_REINFORCED_OBSIDIAN, "crackreinforcedobsidian");
        addShattererRecipe(exporter, 25, Ingredient.ofItems(BlockContent.CRACKED_REINFORCED_OBSIDIAN), BlockContent.SHATTERED_REINFORCED_OBSIDIAN, "shatteredreinforcedobsidian");
        
        // allay creator
        offerComplex(exporter, BlockContent.ALLAY_CREATOR_BLOCK.asItem(), of(rearth.oritech.init.TagContent.NICKEL_INGOTS), of(rearth.oritech.init.BlockContent.ENERGY_PIPE.asItem()), of(ItemContent.DUBIOS_CONTAINER), of(rearth.oritech.init.BlockContent.ASSEMBLER_BLOCK.asItem()), of(Items.COPPER_INGOT), "_allay_creator");
        
        // shatterer
        offerComplex(exporter, BlockContent.SHATTERER_BLOCK.asItem(), of(BlockContent.WEAK_CHARGE_BLOCK.asItem()), of(rearth.oritech.init.BlockContent.METAL_GIRDER_BLOCK), of(rearth.oritech.init.BlockContent.METAL_GIRDER_BLOCK.asItem()), of(ItemContent.OVERCHARGED_CRYSTAL), of(ItemContent.PLASTIC_SHEET), "_shatterer");
        
        // digger
        offerComplex(exporter, BlockContent.DIGGER.asItem(), of(ItemContent.PLASTIC_SHEET), of(rearth.excavations.init.ItemContent.PRIMITIVE_DIG_SHOVEL), of(rearth.excavations.init.ItemContent.PRIMITIVE_DIG_SHOVEL), of(ItemContent.PLASTIC_SHEET), of(rearth.oritech.init.TagContent.ELECTRUM_INGOTS), "_digger");
        
        // primitive drill
        offerComplex(exporter, BlockContent.PRIMITIVE_DEEP_DRILL.asItem(), of(ItemContent.STEEL_INGOT), of(ItemTags.PLANKS), of(rearth.excavations.init.ItemContent.PRIMITIVE_DIG_SHOVEL), of(ItemContent.PLASTIC_SHEET), of(Items.SMOOTH_STONE), "_primitive_quarry");
        
        // charges
        offerCharge(exporter, BlockContent.WEAK_CHARGE_BLOCK.asItem(), of(Items.REDSTONE), of(Items.GUNPOWDER), of(Items.SAND), 4, "_weakcharge");
        offerCharge(exporter, BlockContent.MEDIUM_CHARGE_BLOCK.asItem(), of(Items.REDSTONE), of(Items.GUNPOWDER), of(ItemContent.ENERGITE_INGOT), 4, "_mediumcharge");
        offerCharge(exporter, BlockContent.STRONG_CHARGE_BLOCK.asItem(), of(Items.REDSTONE), of(Items.GUNPOWDER), of(ItemContent.ADAMANT_INGOT), 4, "_strongcharge");
        offerCharge(exporter, BlockContent.EXTREME_CHARGE_BLOCK.asItem(), of(Items.REDSTONE), of(Items.GUNPOWDER), of(ItemContent.DURATIUM_INGOT), 4, "_extremecharge");
        
        // laser redirector
        offerCharge(exporter, rearth.excavations.init.ItemContent.LASER_REDIRECTOR, of(ItemContent.TARGET_DESIGNATOR), of(ItemContent.ADVANCED_COMPUTING_ENGINE), of(ItemContent.ELECTRUM_INGOT), 1, "_lasertool");
        
        // shovels
        offerShovel(exporter, rearth.excavations.init.ItemContent.PRIMITIVE_DIG_SHOVEL, of(ItemTags.LOGS), of(Items.IRON_INGOT), 1, "_primitive_shovel");
        offerShovel(exporter, rearth.excavations.init.ItemContent.ADVANCED_DIG_SHOVEL, of(rearth.oritech.init.TagContent.STEEL_INGOTS), of(Items.IRON_INGOT), 1, "_adv_shovel");
        offerShovel(exporter, rearth.excavations.init.ItemContent.GOLD_DIG_SHOVEL, of(Items.GOLD_INGOT), of(Items.IRON_INGOT), 1, "_gold_shovel");
        offerShovel(exporter, rearth.excavations.init.ItemContent.ELITE_DIG_SHOVEL, of(ItemContent.ADAMANT_INGOT), of(Items.IRON_INGOT), 1, "_elite_shovel");
        
    }
    
    private static void addAllayCreatorRecipe(RecipeExporter exporter, int time, List<Ingredient> inputs, ItemStack result, String id) {
        exporter.accept(Excavation.id(id),
          new OritechRecipe(time, inputs, List.of(result), RecipeContent.ALLAY_CREATOR, FluidIngredient.EMPTY, List.of()), null);
    }
    
    private static void addShattererRecipe(RecipeExporter exporter, int time, Ingredient input, Block result, String id) {
        exporter.accept(Excavation.id(id),
          new OritechRecipe(time, List.of(input), List.of(new ItemStack(result)), RecipeContent.SHATTERER, FluidIngredient.EMPTY, List.of()), null);
    }
    
    public void offerComplex(RecipeExporter exporter, Item output, Ingredient bottom, Ingredient botSides, Ingredient middleSides, Ingredient core, Ingredient top, String suffix) {
        var builder = ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, output, 1).input('s', botSides).input('c', core).input('t', top).input('b', bottom).input('m', middleSides)
                        .pattern("ttt")
                        .pattern("mcm")
                        .pattern("sbs");
        builder.criterion(hasItem(output), conditionsFromItem(output)).offerTo(exporter, Excavation.id("crafting/" + suffix));
    }
    
    public void offerCharge(RecipeExporter exporter, Item output, Ingredient redstone, Ingredient b, Ingredient o, int count, String suffix) {
        var builder = ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, output, count).input('r', redstone).input('o', o).input('b', b)
                        .pattern("brb")
                        .pattern("obo")
                        .pattern("bob");
        builder.criterion(hasItem(output), conditionsFromItem(output)).offerTo(exporter, Excavation.id("crafting/" + suffix));
    }
    
    public void offerShovel(RecipeExporter exporter, Item output, Ingredient plating, Ingredient core, int count, String suffix) {
        var builder = ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, output, count).input('c', core).input('p', plating)
                        .pattern(" pp")
                        .pattern("pcp")
                        .pattern("pcp");
        builder.criterion(hasItem(output), conditionsFromItem(output)).offerTo(exporter, Excavation.id("crafting/" + suffix));
    }
    
    public static Ingredient of(ItemConvertible item) {
        return Ingredient.ofItems(item);
    }
    
    public static Ingredient of(TagKey<Item> item) {
        return Ingredient.fromTag(item);
    }
}
