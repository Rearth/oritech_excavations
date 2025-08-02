package rearth.excavations.fabric.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.RegistryWrapper;
import rearth.excavations.Excavation;
import rearth.excavations.init.RecipeContent;
import rearth.excavations.init.TagContent;
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
    public void generate(RecipeExporter recipeExporter) {
        
        addAllayCreatorRecipe(recipeExporter, 200, List.of(Ingredient.ofItems(ItemContent.UNHOLY_INTELLIGENCE), Ingredient.ofItems(Items.IRON_INGOT), Ingredient.ofItems(Items.IRON_INGOT)), new ItemStack(rearth.excavations.init.ItemContent.BETTER_ALLAY_EGG), "mecha_allay");
        
        addShattererRecipe(recipeExporter, 1, Ingredient.ofItems(Items.STONE), Blocks.SANDSTONE, "stone");
        addShattererRecipe(recipeExporter, 2, Ingredient.ofItems(Items.DEEPSLATE), Blocks.COBBLESTONE, "deepslate");
        addShattererRecipe(recipeExporter, 1, Ingredient.ofItems(Items.COBBLESTONE), Blocks.SANDSTONE, "cobble");
        addShattererRecipe(recipeExporter, 1, Ingredient.ofItems(Items.SANDSTONE), Blocks.AIR, "sandstone");
        addShattererRecipe(recipeExporter, 4, Ingredient.ofItems(Items.OBSIDIAN), Blocks.DEEPSLATE, "obsidian");
        addShattererRecipe(recipeExporter, 4, Ingredient.ofItems(Items.DIRT), Blocks.AIR, "dirt");
        addShattererRecipe(recipeExporter, 2, Ingredient.fromTag(TagContent.RANDOS), Blocks.DIRT, "randos");
        addShattererRecipe(recipeExporter, 2, Ingredient.fromTag(ConventionalItemTags.ORES), Blocks.STONE, "ores");
        
    }
    
    private static void addAllayCreatorRecipe(RecipeExporter exporter, int time, List<Ingredient> inputs, ItemStack result, String id) {
        exporter.accept(Excavation.id(id),
          new OritechRecipe(time, inputs, List.of(result), RecipeContent.ALLAY_CREATOR, FluidIngredient.EMPTY, List.of()), null);
    }
    
    private static void addShattererRecipe(RecipeExporter exporter, int time, Ingredient input, Block result, String id) {
        exporter.accept(Excavation.id(id),
          new OritechRecipe(time, List.of(input), List.of(new ItemStack(result)), RecipeContent.SHATTERER, FluidIngredient.EMPTY, List.of()), null);
    }
}
