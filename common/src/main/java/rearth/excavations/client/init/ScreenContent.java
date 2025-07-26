package rearth.excavations.client.init;

import dev.architectury.registry.menu.MenuRegistry;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.screen.ScreenHandlerType;
import rearth.oritech.client.ui.BasicMachineScreen;
import rearth.oritech.client.ui.BasicMachineScreenHandler;
import rearth.oritech.util.registry.ArchitecturyRegistryContainer;

public class ScreenContent implements ArchitecturyRegistryContainer<ScreenHandlerType<?>> {
    
    public static final ScreenHandlerType<BasicMachineScreenHandler> ALLAY_CREATOR_SCREEN = MenuRegistry.ofExtended(BasicMachineScreenHandler::new);
    
    public static void registerScreens() {
        
        MenuRegistry.registerScreenFactory(ALLAY_CREATOR_SCREEN, BasicMachineScreen<BasicMachineScreenHandler>::new);
    
    }
    
    @Override
    public RegistryKey<Registry<ScreenHandlerType<?>>> getRegistryType() {
        return RegistryKeys.SCREEN_HANDLER;
    }
    
    @Override
    public Class<ScreenHandlerType<?>> getTargetFieldType() {
        //noinspection unchecked
        return (Class<ScreenHandlerType<?>>) (Object) ScreenHandlerType.class;
    }
}
