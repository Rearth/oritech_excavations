package rearth.excavations.init;

import dev.architectury.registry.level.entity.EntityAttributeRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.RegistryKeys;
import rearth.excavations.Excavation;
import rearth.excavations.entities.BetterAllayEntity;

public class MobContent {
    
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Excavation.MOD_ID, RegistryKeys.ENTITY_TYPE);
    
    public static RegistrySupplier<EntityType<BetterAllayEntity>> BETTER_ALLAY = ENTITIES.register(Excavation.id("better_allay"),
      () -> EntityType.Builder.create(BetterAllayEntity::new, SpawnGroup.CREATURE)
              .dimensions(0.3f, 0.3f)
              .build("better_allay"));
    
    public static void init() {
        
        ENTITIES.register();
        
        EntityAttributeRegistry.register(BETTER_ALLAY, BetterAllayEntity::createMobAttributes);
    }
    
}
