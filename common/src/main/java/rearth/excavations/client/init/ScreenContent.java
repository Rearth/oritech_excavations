package rearth.excavations.client.init;

import dev.architectury.registry.menu.MenuRegistry;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.screen.ScreenHandlerType;
import rearth.excavations.client.ui.DigControllerScreen;
import rearth.excavations.client.ui.DigControllerScreenHandler;
import rearth.oritech.client.ui.BasicMachineScreen;
import rearth.oritech.client.ui.BasicMachineScreenHandler;
import rearth.oritech.client.ui.UpgradableMachineScreen;
import rearth.oritech.client.ui.UpgradableMachineScreenHandler;
import rearth.oritech.util.registry.ArchitecturyRegistryContainer;

public class ScreenContent implements ArchitecturyRegistryContainer<ScreenHandlerType<?>> {
    
    public static final ScreenHandlerType<BasicMachineScreenHandler> ALLAY_CREATOR_SCREEN = MenuRegistry.ofExtended(BasicMachineScreenHandler::new);
    public static final ScreenHandlerType<UpgradableMachineScreenHandler> DIGGER_SCREEN = MenuRegistry.ofExtended(UpgradableMachineScreenHandler::new);
    public static final ScreenHandlerType<DigControllerScreenHandler> DIG_CONTROLLER_SCREEN = MenuRegistry.ofExtended(DigControllerScreenHandler::new);
    
    public static void registerScreens() {
        
        MenuRegistry.registerScreenFactory(ALLAY_CREATOR_SCREEN, BasicMachineScreen<BasicMachineScreenHandler>::new);
        MenuRegistry.registerScreenFactory(DIGGER_SCREEN, UpgradableMachineScreen<UpgradableMachineScreenHandler>::new);
        MenuRegistry.registerScreenFactory(DIG_CONTROLLER_SCREEN, DigControllerScreen::new);
    
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
