package rearth.excavations.init.world.resourcenode;

import io.wispforest.endec.Endec;
import io.wispforest.endec.impl.StructEndecBuilder;
import io.wispforest.owo.serialization.endec.MinecraftEndecs;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.FeatureConfig;

import java.util.List;

public record ExcavationNodeFeatureConfig(
  int nodeSize,
  int boulderRadius,
  List<Identifier> nodeOres,
  float nodeOreChance,
  List<Identifier> boulderOres,
  Identifier overlayBlock,
  int overlayHeight,
  int depth) implements FeatureConfig {
    
    public static final Endec<ExcavationNodeFeatureConfig> NODE_FEATURE_ENDEC = StructEndecBuilder.of(
      Endec.INT.fieldOf("nodeSize", ExcavationNodeFeatureConfig::nodeSize),
      Endec.INT.fieldOf("boulderRadius", ExcavationNodeFeatureConfig::boulderRadius),
      MinecraftEndecs.IDENTIFIER.listOf().fieldOf("nodeOres", ExcavationNodeFeatureConfig::nodeOres),
      Endec.FLOAT.fieldOf("nodeOreChance", ExcavationNodeFeatureConfig::nodeOreChance),
      MinecraftEndecs.IDENTIFIER.listOf().fieldOf("boulderOres", ExcavationNodeFeatureConfig::boulderOres),
      MinecraftEndecs.IDENTIFIER.fieldOf("overlayBlock", ExcavationNodeFeatureConfig::overlayBlock),
      Endec.INT.fieldOf("overlayHeight", ExcavationNodeFeatureConfig::overlayHeight),
      Endec.INT.fieldOf("depth", ExcavationNodeFeatureConfig::depth),
      ExcavationNodeFeatureConfig::new
    );
}
