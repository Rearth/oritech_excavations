package rearth.excavations.fabric.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.registry.RegistryWrapper;
import rearth.excavations.init.BlockContent;

import java.util.concurrent.CompletableFuture;

public class BlockLootGenerator extends FabricBlockLootTableProvider {
    
    public BlockLootGenerator(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }
    
    @Override
    public void generate() {
        for (var block : BlockContent.autoRegisteredDrops) {
            addDrop(block);
        }
    }
}
