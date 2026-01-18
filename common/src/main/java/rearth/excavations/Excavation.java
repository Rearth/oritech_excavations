package rearth.excavations;


import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rearth.excavations.client.init.ScreenContent;
import rearth.excavations.init.*;
import rearth.excavations.util.ExcavationRecipeRegistryContainer;
import rearth.oritech.Oritech;
import rearth.oritech.util.registry.ArchitecturyRegistryContainer;

public final class Excavation {
    public static final String MOD_ID = "oritech_excavations";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    
    
    public static final Multimap<Identifier, Runnable> EVENT_MAP = initEventMap();
    
    public static void init() {
        // Write common init code here.
        LOGGER.info("Hello from oritech excavations");
        
        LOGGER.info("Oritech id: " + Oritech.MOD_ID);
        
        MobContent.init();
        
        NetworkContent.init();
        
        FeatureContent.initialize();
    }
    
    // fabric only
    public static void runAllRegistries() {
        
        LOGGER.info("Running Excavation registrations...");
        
        // fluids need to be first
        LOGGER.debug("Registering early");
        EVENT_MAP.get(RegistryKeys.FLUID.getValue()).forEach(Runnable::run);
        EVENT_MAP.get(RegistryKeys.DATA_COMPONENT_TYPE.getValue()).forEach(Runnable::run);
        
        for (var type : EVENT_MAP.keySet()) {
            if (type.equals(RegistryKeys.FLUID.getValue()) || type.equals(RegistryKeys.ITEM_GROUP.getValue()) || type.equals(RegistryKeys.DATA_COMPONENT_TYPE.getValue())) continue;
            EVENT_MAP.get(type).forEach(Runnable::run);
        }
        
        LOGGER.debug("Registering late");
        EVENT_MAP.get(RegistryKeys.ITEM_GROUP.getValue()).forEach(Runnable::run);
        LOGGER.info("Oritech Excavation complete");
    }
    
    private static Multimap<Identifier, Runnable> initEventMap() {
        
        Multimap<Identifier, Runnable> res = ArrayListMultimap.create();
        
        res.put(RegistryKeys.ITEM.getValue(), () -> ArchitecturyRegistryContainer.register(ItemContent.class, MOD_ID, false));
        res.put(RegistryKeys.BLOCK.getValue(), () -> ArchitecturyRegistryContainer.register(BlockContent.class, MOD_ID, false));
        res.put(RegistryKeys.BLOCK_ENTITY_TYPE.getValue(), () -> ArchitecturyRegistryContainer.register(BlockEntitiesContent.class, MOD_ID, false));
        res.put(RegistryKeys.RECIPE_TYPE.getValue(), () -> ArchitecturyRegistryContainer.register(RecipeContent.class, MOD_ID, false));
        res.put(RegistryKeys.SCREEN_HANDLER.getValue(), () -> ArchitecturyRegistryContainer.register(ScreenContent.class, MOD_ID, false));
        res.put(RegistryKeys.ITEM_GROUP.getValue(), () -> ArchitecturyRegistryContainer.register(ItemGroups.class, MOD_ID, false));
        res.put(RegistryKeys.SOUND_EVENT.getValue(), () -> ArchitecturyRegistryContainer.register(SoundContent.class, MOD_ID, false));
        res.put(RegistryKeys.RECIPE_SERIALIZER.getValue(), ExcavationRecipeRegistryContainer::finishSerializerRegister);
        res.put(RegistryKeys.DATA_COMPONENT_TYPE.getValue(), ComponentContent.COMPONENTS::register);
        res.put(RegistryKeys.FEATURE.getValue(), () -> ArchitecturyRegistryContainer.register(FeatureContent.class, MOD_ID, false));
        
        return res;
        
    }
    
    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }
    
    
}
