package rearth.excavations.neoforge;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;

import net.neoforged.neoforge.registries.RegisterEvent;
import rearth.excavations.Excavation;
import rearth.oritech.Oritech;

@Mod(Excavation.MOD_ID)
public final class ExcavationsNeoforge {
    public ExcavationsNeoforge(IEventBus eventBus) {
        
        eventBus.register(new EventHandler());
        
        // Run our common setup.
        Excavation.init();
    }
    
    class EventHandler {
        
        @SubscribeEvent
        public void register(RegisterEvent event) {
            
            var id = event.getRegistryKey().getValue();
            
            if (Excavation.EVENT_MAP.containsKey(id)) {
                Excavation.LOGGER.debug(event.getRegistryKey().toString());
                Excavation.EVENT_MAP.get(id).forEach(Runnable::run);
            }
            
        }
    
    }
}
