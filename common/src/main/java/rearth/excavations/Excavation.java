package rearth.excavations;


import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rearth.excavations.init.ItemContent;
import rearth.excavations.init.ItemGroups;
import rearth.excavations.init.MobContent;
import rearth.oritech.Oritech;
import rearth.oritech.util.registry.ArchitecturyRegistryContainer;

public final class Excavation {
    public static final String MOD_ID = "oritech_excavations";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static void init() {
        // Write common init code here.
        LOGGER.info("Hello from oritech excavations");
        
        LOGGER.info("Oritech id: " + Oritech.MOD_ID);
        
        MobContent.init();
        
        ArchitecturyRegistryContainer.register(ItemContent.class, MOD_ID, false);
        ArchitecturyRegistryContainer.register(ItemGroups.class, MOD_ID, false);
    }
    
    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }
    
    
}
