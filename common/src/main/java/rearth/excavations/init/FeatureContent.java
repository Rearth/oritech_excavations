package rearth.excavations.init;

import dev.architectury.platform.Platform;
import dev.architectury.registry.level.biome.BiomeModifications;
import io.wispforest.owo.serialization.CodecUtils;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.Feature;
import rearth.excavations.Excavation;
import rearth.oritech.init.world.features.uranium.UraniumPatchFeature;
import rearth.oritech.init.world.features.uranium.UraniumPatchFeatureConfig;
import rearth.oritech.util.registry.ArchitecturyRegistryContainer;

public class FeatureContent implements ArchitecturyRegistryContainer<Feature<?>> {
    
    public static final Feature<UraniumPatchFeatureConfig> COPPER_PATCH = new UraniumPatchFeature(CodecUtils.toCodec(UraniumPatchFeatureConfig.URANIUM_FEATURE_ENDEC));
    public static final Feature<UraniumPatchFeatureConfig> GOLD_PATCH = new UraniumPatchFeature(CodecUtils.toCodec(UraniumPatchFeatureConfig.URANIUM_FEATURE_ENDEC));
    public static final Feature<UraniumPatchFeatureConfig> IRON_PATCH = new UraniumPatchFeature(CodecUtils.toCodec(UraniumPatchFeatureConfig.URANIUM_FEATURE_ENDEC));
    public static final Feature<UraniumPatchFeatureConfig> NICKEL_PATCH = new UraniumPatchFeature(CodecUtils.toCodec(UraniumPatchFeatureConfig.URANIUM_FEATURE_ENDEC));
    public static final Feature<UraniumPatchFeatureConfig> REDSTONE_PATCH = new UraniumPatchFeature(CodecUtils.toCodec(UraniumPatchFeatureConfig.URANIUM_FEATURE_ENDEC));
    
    public static void initialize() {
        
        if (!Platform.isModLoaded("spectrum")) return;
        
        BiomeModifications.addProperties((context, mutable) -> {
            if (context.hasTag(BiomeTags.IS_OVERWORLD)) {
                mutable.getGenerationProperties().addFeature(GenerationStep.Feature.UNDERGROUND_ORES, RegistryKey.of(RegistryKeys.PLACED_FEATURE, Excavation.id("copper_patch")));
                mutable.getGenerationProperties().addFeature(GenerationStep.Feature.UNDERGROUND_ORES, RegistryKey.of(RegistryKeys.PLACED_FEATURE, Excavation.id("iron_patch")));
                mutable.getGenerationProperties().addFeature(GenerationStep.Feature.UNDERGROUND_ORES, RegistryKey.of(RegistryKeys.PLACED_FEATURE, Excavation.id("gold_patch")));
                mutable.getGenerationProperties().addFeature(GenerationStep.Feature.UNDERGROUND_ORES, RegistryKey.of(RegistryKeys.PLACED_FEATURE, Excavation.id("nickel_patch")));
                mutable.getGenerationProperties().addFeature(GenerationStep.Feature.UNDERGROUND_ORES, RegistryKey.of(RegistryKeys.PLACED_FEATURE, Excavation.id("redstone_patch")));
                Excavation.LOGGER.info("Registered ore patches");
                
            }
        });
        
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public Class<Feature<?>> getTargetFieldType() {
        return (Class<Feature<?>>) (Object) Feature.class;
    }
    
    @Override
    public RegistryKey<Registry<Feature<?>>> getRegistryType() {
        return RegistryKeys.FEATURE;
    }
}
