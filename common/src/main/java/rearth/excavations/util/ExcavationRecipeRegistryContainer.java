package rearth.excavations.util;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import rearth.excavations.Excavation;
import rearth.oritech.util.registry.ArchitecturyRegistryContainer;

import java.lang.reflect.Field;

public interface ExcavationRecipeRegistryContainer extends ArchitecturyRegistryContainer<RecipeType<?>> {
    
    DeferredRegister<RecipeSerializer<?>> SERIALIZER_REGISTRY = DeferredRegister.create(Excavation.MOD_ID, RegistryKeys.RECIPE_SERIALIZER);
    
    default RegistryKey<Registry<RecipeType<?>>> getRegistryType() {
        return RegistryKeys.RECIPE_TYPE;
    }
    
    @SuppressWarnings("unchecked")
    default Class<RecipeType<?>> getTargetFieldType() {
        return (Class<RecipeType<?>>) (Object)RecipeType.class;
    }
    
    default void postProcessField(String namespace, RecipeType<?> value, String identifier, Field field, RegistrySupplier<RecipeType<?>> supplier) {
        ArchitecturyRegistryContainer.super.postProcessField(namespace, value, identifier, field, supplier);
        SERIALIZER_REGISTRY.register(identifier, () -> (RecipeSerializer<?>) value);
    }
    
    static void finishSerializerRegister() {
        SERIALIZER_REGISTRY.register();
    }
}
