package rearth.excavations.fabric.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.RegistryWrapper;
import rearth.excavations.Excavation;
import rearth.excavations.init.BlockContent;
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
        
        // time here is the hardness used to calculate fracturizing depth
        addShattererRecipe(recipeExporter, 1, Ingredient.fromTag(TagContent.RANDOS), BlockContent.CRACKED_STONE, "randos");
        
        addShattererRecipe(recipeExporter, 1, Ingredient.ofItems(Items.STONE), BlockContent.CRACKED_STONE, "crackstone");
        addShattererRecipe(recipeExporter, 1, Ingredient.ofItems(BlockContent.CRACKED_STONE), BlockContent.SHATTERED_STONE, "shatteredstone");
        
        addShattererRecipe(recipeExporter, 2, Ingredient.ofItems(BlockContent.HARD_STONE), BlockContent.CRACKED_HARD_STONE, "crackhardstone");
        addShattererRecipe(recipeExporter, 2, Ingredient.ofItems(BlockContent.CRACKED_HARD_STONE), BlockContent.SHATTERED_HARD_STONE, "shatteredhardstone");
        
        addShattererRecipe(recipeExporter, 3, Ingredient.ofItems(BlockContent.HARDER_STONE), BlockContent.CRACKED_HARDER_STONE, "crackharderstone");
        addShattererRecipe(recipeExporter, 3, Ingredient.ofItems(BlockContent.CRACKED_HARDER_STONE), BlockContent.SHATTERED_HARDER_STONE, "shatteredharderstone");
        
        addShattererRecipe(recipeExporter, 4, Ingredient.ofItems(BlockContent.HARDERER_STONE), BlockContent.CRACKED_HARDERER_STONE, "crackhardererstone");
        addShattererRecipe(recipeExporter, 4, Ingredient.ofItems(BlockContent.CRACKED_HARDERER_STONE), BlockContent.SHATTERED_HARDERER_STONE, "shatteredhardererstone");
        
        addShattererRecipe(recipeExporter, 5, Ingredient.ofItems(Blocks.DEEPSLATE), BlockContent.CRACKED_DEEPSLATE, "crackdeepslate");
        addShattererRecipe(recipeExporter, 5, Ingredient.ofItems(BlockContent.CRACKED_DEEPSLATE), BlockContent.SHATTERED_DEEPSLATE, "shattereddeepslate");
        
        addShattererRecipe(recipeExporter, 6, Ingredient.ofItems(BlockContent.DEEPER_SLATE), BlockContent.CRACKED_DEEPER_SLATE, "crackdeeperslate");
        addShattererRecipe(recipeExporter, 6, Ingredient.ofItems(BlockContent.CRACKED_DEEPER_SLATE), BlockContent.SHATTERED_DEEPER_SLATE, "shattereddeeperslate");
        
        addShattererRecipe(recipeExporter, 8, Ingredient.ofItems(BlockContent.VERY_DEEP_SLATE), BlockContent.CRACKED_VERY_DEEP_SLATE, "crackverydeepslate");
        addShattererRecipe(recipeExporter, 8, Ingredient.ofItems(BlockContent.CRACKED_VERY_DEEP_SLATE), BlockContent.SHATTERED_VERY_DEEP_SLATE, "shatteredverydeepslate");
        
        addShattererRecipe(recipeExporter, 12, Ingredient.ofItems(BlockContent.DEEPEST_SLATE), BlockContent.CRACKED_DEEPEST_SLATE, "crackdeepestslate");
        addShattererRecipe(recipeExporter, 12, Ingredient.ofItems(BlockContent.CRACKED_DEEPEST_SLATE), BlockContent.SHATTERED_DEEPEST_SLATE, "shattereddeepestslate");
        
        addShattererRecipe(recipeExporter, 16, Ingredient.ofItems(Blocks.OBSIDIAN), BlockContent.CRACKED_OBSIDIAN, "crackobsidian");
        addShattererRecipe(recipeExporter, 16, Ingredient.ofItems(BlockContent.CRACKED_OBSIDIAN), BlockContent.SHATTERED_OBSIDIAN, "shatteredobsidian");
        
        addShattererRecipe(recipeExporter, 25, Ingredient.ofItems(BlockContent.REINFORCED_OBSIDIAN), BlockContent.CRACKED_REINFORCED_OBSIDIAN, "crackreinforcedobsidian");
        addShattererRecipe(recipeExporter, 25, Ingredient.ofItems(BlockContent.CRACKED_REINFORCED_OBSIDIAN), BlockContent.SHATTERED_REINFORCED_OBSIDIAN, "shatteredreinforcedobsidian");
        
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
