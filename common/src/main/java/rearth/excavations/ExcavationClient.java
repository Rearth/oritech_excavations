package rearth.excavations;

import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import rearth.excavations.client.BetterAllayRenderer;
import rearth.excavations.client.init.ScreenContent;
import rearth.excavations.init.MobContent;

public class ExcavationClient {
    
    public static void init() {
        Excavation.LOGGER.info("Hello from excavation client!");
        
        EntityRendererRegistry.register(MobContent.BETTER_ALLAY, BetterAllayRenderer::new);
        
        ScreenContent.registerScreens();
        
    }
    
}
