package rearth.excavations.fabric;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import rearth.excavations.fabric.datagen.BlockTagGenerator;
import rearth.excavations.fabric.datagen.ItemTagGenerator;
import rearth.excavations.fabric.datagen.ModelGenerator;
import rearth.excavations.fabric.datagen.RecipeGenerator;

public class ExcavationsDataGeneratorFabric implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        
        System.out.println("Running excavations datagen");
        
        var pack = fabricDataGenerator.createPack();
        
        pack.addProvider(ModelGenerator::new);
        pack.addProvider(RecipeGenerator::new);
        pack.addProvider(BlockTagGenerator::new);
        pack.addProvider(ItemTagGenerator::new);
        
    }
}
