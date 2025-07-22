package rearth.excavations.init;

import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import rearth.oritech.util.registry.ArchitecturyRegistryContainer;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ItemGroups implements ArchitecturyRegistryContainer<ItemGroup> {
    
    public static List<Supplier<Item>> registered = new ArrayList<>();
    
    public static final ItemGroup MAIN = CreativeTabRegistry.create(
      Text.translatable("itemgroup.oritech_excavations.mainb"),
      () -> new ItemStack(ItemContent.BETTER_ALLAY_EGG));
    
    @Override
    public RegistryKey<Registry<ItemGroup>> getRegistryType() {
        return RegistryKeys.ITEM_GROUP;
    }
    
    @Override
    public Class<ItemGroup> getTargetFieldType() {
        return ItemGroup.class;
    }
    
    @SuppressWarnings("UnstableApiUsage")
    @Override
    public void postProcessField(String namespace, ItemGroup value, String identifier, Field field, RegistrySupplier<ItemGroup> supplier) {
        ArchitecturyRegistryContainer.super.postProcessField(namespace, value, identifier, field, supplier);
        
        registered.forEach(item -> CreativeTabRegistry.appendStack(supplier, new ItemStack(item.get())));
        
    }
}
