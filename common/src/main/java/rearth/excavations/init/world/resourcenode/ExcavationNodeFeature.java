package rearth.excavations.init.world.resourcenode;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.noise.PerlinNoiseSampler;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;
import rearth.excavations.Excavation;
import rearth.oritech.Oritech;

import java.util.List;

public class ExcavationNodeFeature extends Feature<ExcavationNodeFeatureConfig> {
    
    public ExcavationNodeFeature(Codec<ExcavationNodeFeatureConfig> configCodec) {
        super(configCodec);
    }
    
    @Override
    public boolean generate(FeatureContext<ExcavationNodeFeatureConfig> context) {
        
        var world = context.getWorld();
        var origin = context.getOrigin();
        
        if (world.isClient()) return false;
        
        var solidBlockFound = false;
        var testPos = new BlockPos(origin);
        var deepNodePos = testPos;
        var boulderPos = testPos;
        
        for (int i = 0; i < context.getConfig().depth() + 15; i++) {
            var downPos = testPos.down(i);
            var testState = world.getBlockState(downPos);
            if (i > context.getConfig().depth() && testState.isSolidBlock(world, downPos)) {
                deepNodePos = downPos;
                break;
            } else if (testState.isSolidBlock(world, downPos) && !solidBlockFound) {
                boulderPos = downPos;
                solidBlockFound = true;
            }
        }
        
        // edge case: if no solid block was found, or the boulder is too close to the deep node, don't generate
        if (!solidBlockFound || boulderPos.getY() < (deepNodePos.getY() + 4))
            return false;
        
        if (Oritech.CONFIG.easyFindFeatures())
            placeSurfaceBoulder(boulderPos, context);
        placeBedrockNode(deepNodePos, context);
        Excavation.LOGGER.debug("Placing deep node at: " + boulderPos);
        return true;
        
    }
    
    private BlockState getRandomBlockFromList(List<Identifier> list, Random random) {
        return Registries.BLOCK.get(getRandomFromList(list, random)).getDefaultState();
    }
    
    private Identifier getRandomFromList(List<Identifier> list, Random random) {
        return list.get(random.nextInt(list.size()));
    }
    
    private void placeBedrockNode(BlockPos startPos, FeatureContext<ExcavationNodeFeatureConfig> context) {
        
        var world = context.getWorld();
        var random = context.getRandom();
        var ores = context.getConfig().nodeOres();
        
        var radius = context.getConfig().nodeSize();
        var overlayBlock = Registries.BLOCK.get(context.getConfig().overlayBlock()).getDefaultState();
        var overlayHeight = context.getConfig().overlayHeight();
        
        var noise = new PerlinNoiseSampler(random);
        
        for (BlockPos pos : BlockPos.iterateOutwards(startPos, radius, radius, radius)) {
            // skip anything outside the radius, or outside the vertical cutoff
            if (Math.sqrt(pos.getSquaredDistance(startPos)) + noise.sample(pos.getX(), pos.getY(), pos.getZ()) > radius
                  || pos.getY() >= startPos.getY() + overlayHeight + 3 + noise.sample(pos.getX(), pos.getY() + 2, pos.getZ())) continue;
            // randomly replace some blocks below bedrock level with resource nodes
            if (pos.getY() <= startPos.getY() + 1 && random.nextDouble() <= context.getConfig().nodeOreChance()) {
                world.setBlockState(pos, getRandomBlockFromList(ores, random), 0x10);
                // set blocks between bedrock and bedrock + overlayHeight to overlayBlock
            } else if (pos.getY() > startPos.getY() + 1 && pos.getY() <= startPos.getY() + overlayHeight + 1) {
                world.setBlockState(pos, overlayBlock, 0x10);
                // set anything between overlay and vertical cutoff to air
            } else if (pos.getY() > startPos.getY() + 1) {
                world.setBlockState(pos, Blocks.AIR.getDefaultState(), 0x10);
            }
        }
    }
    
    private void placeSurfaceBoulder(BlockPos startPos, FeatureContext<ExcavationNodeFeatureConfig> context) {
        
        var world = context.getWorld();
        var random = context.getRandom();
        var radius = context.getConfig().boulderRadius();
        var movedCenter = startPos.offset(Axis.pickRandomAxis(random), random.nextBetween(0, radius-1));
        var ores = context.getConfig().boulderOres();
        var noise = new PerlinNoiseSampler(random);
        
        for (BlockPos pos : BlockPos.iterateOutwards(movedCenter, radius, radius, radius)) {
            if (Math.sqrt(pos.getSquaredDistance(movedCenter)) > radius + noise.sample(pos.getX(), pos.getY(), pos.getZ())) continue;
            world.setBlockState(pos, getRandomBlockFromList(ores, random), 0x10);
        }
    }
}
