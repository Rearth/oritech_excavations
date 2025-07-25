package rearth.excavations.fabric.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.RegistryWrapper;
import rearth.excavations.Excavation;
import rearth.excavations.init.RecipeContent;
import rearth.oritech.init.ItemContent;
import rearth.oritech.init.recipes.OritechRecipe;
import rearth.oritech.util.FluidIngredient;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class RecipeGenerator extends FabricRecipeProvider {
    
    private final FabricDataOutput output;
    private final CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture;
    
    public RecipeGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
        this.output = output;
        this.registriesFuture = registriesFuture;
    }
    
    @Override
    public void generate(RecipeExporter recipeExporter) {
        
        addAllayCreatorRecipe(recipeExporter, 200, List.of(Ingredient.ofItems(ItemContent.UNHOLY_INTELLIGENCE), Ingredient.ofItems(Items.IRON_INGOT), Ingredient.ofItems(Items.IRON_INGOT)), new ItemStack(rearth.excavations.init.ItemContent.BETTER_ALLAY_EGG), "mecha_allay");
        
    }
    
    private static void addAllayCreatorRecipe(RecipeExporter exporter, int time, List<Ingredient> inputs, ItemStack result, String id) {
        exporter.accept(Excavation.id(id),
          new OritechRecipe(time, inputs, List.of(result), RecipeContent.ALLAY_CREATOR, FluidIngredient.EMPTY, List.of()), null);
    }
}
