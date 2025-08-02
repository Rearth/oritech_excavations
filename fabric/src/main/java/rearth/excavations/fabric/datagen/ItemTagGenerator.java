package rearth.excavations.fabric.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import rearth.excavations.init.TagContent;
import rearth.oritech.init.ItemContent;

import java.util.concurrent.CompletableFuture;

public class ItemTagGenerator extends FabricTagProvider<Item> {
    
    public ItemTagGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, RegistryKeys.ITEM, registriesFuture);
    }
    
    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(TagContent.ALLAY_BOOSTERS)
          .add(ItemContent.FLUXITE);
        
        getOrCreateTagBuilder(TagContent.RANDOS)
          .add(Items.GRANITE)
          .add(Items.ANDESITE)
          .add(Items.DIORITE)
          .add(Items.GRAVEL);
    }
}
