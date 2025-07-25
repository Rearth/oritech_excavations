package rearth.excavations.init;

import rearth.excavations.Excavation;
import rearth.excavations.util.ExcavationRecipeRegistryContainer;
import rearth.oritech.init.recipes.OritechRecipeType;

public class RecipeContent implements ExcavationRecipeRegistryContainer {
    
    public static final OritechRecipeType ALLAY_CREATOR = new OritechRecipeType(Excavation.id("allay_creator"));
    
}
