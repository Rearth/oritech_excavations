package rearth.excavations.fabric;

import dev.architectury.fluid.FluidStack;
import net.fabricmc.api.ModInitializer;

import rearth.excavations.Excavation;
import rearth.oritech.Oritech;
import rearth.oritech.api.networking.NetworkManager;
import rearth.oritech.init.recipes.RecipeContent;

public final class ExcavationsFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        // Run our common setup.
        Excavation.init();
        
        // avoid possible oritech not initialized issues
        if (NetworkManager.FLUID_STACK_STREAM_CODEC == null) {
            NetworkManager.FLUID_STACK_CODEC = FluidStack.CODEC;
            NetworkManager.FLUID_STACK_STREAM_CODEC = FluidStack.STREAM_CODEC;
        }
        
        Excavation.runAllRegistries();
    }
}
