package rearth.excavations.neoforge.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import rearth.excavations.Excavation;
import rearth.excavations.ExcavationClient;
import rearth.excavations.client.init.RendererContent;

@Mod(value = Excavation.MOD_ID, dist = Dist.CLIENT)
public class ExcavationsClientNeoforge {
    
    public ExcavationsClientNeoforge(IEventBus eventBus) {
        
        eventBus.register(new EventHandler());
        
        ExcavationClient.init();
    }
    
    static class EventHandler {
        @SubscribeEvent
        public void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
            RendererContent.registerRenderers();
        }
    }
    
}
