package rearth.excavations.init;

import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.item.Item;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Colors;
import net.minecraft.util.Rarity;
import rearth.excavations.item.DiggerShovelItem;
import rearth.excavations.item.LaserRemote;
import rearth.oritech.util.registry.ArchitecturyRegistryContainer;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class ItemContent implements ArchitecturyRegistryContainer<Item> {
    
    public static final Item BETTER_ALLAY_EGG = new SpawnEggItem(MobContent.BETTER_ALLAY.get(), Colors.GREEN, Colors.LIGHT_GRAY, new Item.Settings());
    public static final Item LASER_REDIRECTOR = new LaserRemote(new Item.Settings().maxCount(1).component(ComponentContent.TARGET_POSITIONS.get(), new ArrayList<>()));
    
    public static final Item PRIMITIVE_DIG_SHOVEL = new DiggerShovelItem(new Item.Settings(), 16, 0.3f);
    public static final Item ADVANCED_DIG_SHOVEL = new DiggerShovelItem(new Item.Settings(), 80, 0.5f);
    public static final Item GOLD_DIG_SHOVEL = new DiggerShovelItem(new Item.Settings(), 60, 2f);
    public static final Item ELITE_DIG_SHOVEL = new DiggerShovelItem(new Item.Settings().rarity(Rarity.UNCOMMON), 140, 1f);
    
    @Override
    public RegistryKey<Registry<Item>> getRegistryType() {
        return RegistryKeys.ITEM;
    }
    
    @Override
    public Class<Item> getTargetFieldType() {
        return Item.class;
    }
    
    @Override
    public void postProcessField(String namespace, Item value, String identifier, Field field, RegistrySupplier<Item> supplier) {
        ArchitecturyRegistryContainer.super.postProcessField(namespace, value, identifier, field, supplier);
        
        ItemGroups.registered.add(supplier);
        
    }
}
