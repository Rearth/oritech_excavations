package rearth.excavations.fabric;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import rearth.excavations.fabric.datagen.ModelGenerator;

public class ExcavationsDataGeneratorFabric implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        
        System.out.println("Running excavations datagen");
        
        var pack = fabricDataGenerator.createPack();
        
        pack.addProvider(ModelGenerator::new);
        
    }
}
