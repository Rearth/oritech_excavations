package rearth.excavations;

import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.registry.ReloadListenerRegistry;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.resource.ResourceType;
import org.lwjgl.glfw.GLFW;
import rearth.excavations.announcement.AnnouncementManager;
import rearth.excavations.client.BetterAllayRenderer;
import rearth.excavations.client.init.ScreenContent;
import rearth.excavations.client.ui.AdvancementAnnouncer;
import rearth.excavations.init.MobContent;

public class ExcavationClient {
    
    public static final AnnouncementManager ANNOUNCEMENT_MANAGER = new AnnouncementManager();
    
    public static final KeyBinding DEBUG_TEST = new KeyBinding("key.excavations.debug", GLFW.GLFW_KEY_X, "key.categories.excavations");
    
    public static void init() {
        Excavation.LOGGER.info("Hello from excavation client!");
        
        KeyMappingRegistry.register(DEBUG_TEST);
        
        EntityRendererRegistry.register(MobContent.BETTER_ALLAY, BetterAllayRenderer::new);
        
        ScreenContent.registerScreens();
        
        AdvancementAnnouncer.init();
        
        ReloadListenerRegistry.register(ResourceType.CLIENT_RESOURCES, ANNOUNCEMENT_MANAGER, Excavation.id("announcements"));
        
        ClientTickEvent.CLIENT_POST.register(client -> {
            
            AdvancementAnnouncer.tick();
            
            if (DEBUG_TEST.wasPressed()) {
                Excavation.LOGGER.info("Excavations debug test!");
                var data = AnnouncementManager.getAnnouncement(Excavation.id("test"));
                System.out.println(data);
                
                AdvancementAnnouncer.announce(data);
            }
        });
        
        
    }
    
}
