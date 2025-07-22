package rearth.excavations.init;

import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Colors;
import rearth.oritech.util.registry.ArchitecturyRegistryContainer;

import java.lang.reflect.Field;

public class ItemContent implements ArchitecturyRegistryContainer<Item> {
    
    public static final Item BETTER_ALLAY_EGG = new SpawnEggItem(MobContent.BETTER_ALLAY.get(), Colors.GREEN, Colors.LIGHT_GRAY, new Item.Settings());
    
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
