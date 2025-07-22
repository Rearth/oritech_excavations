package rearth.excavations.neoforge.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import rearth.excavations.Excavation;
import rearth.excavations.ExcavationClient;

@Mod(value = Excavation.MOD_ID, dist = Dist.CLIENT)
public class ExcavationsClientNeoforge {
    
    public ExcavationsClientNeoforge(IEventBus eventBus) {
        ExcavationClient.init();
    }
    
}
