package rearth.excavations;


import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rearth.excavations.init.MobContent;
import rearth.oritech.Oritech;

public final class Excavation {
    public static final String MOD_ID = "oritech_excavations";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static void init() {
        // Write common init code here.
        LOGGER.info("Hello from oritech excavations");
        
        LOGGER.info("Oritech id: " + Oritech.MOD_ID);
        
        MobContent.init();
    }
    
    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }
}
