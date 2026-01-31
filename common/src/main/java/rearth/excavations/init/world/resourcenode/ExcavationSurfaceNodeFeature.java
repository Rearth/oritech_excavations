package rearth.excavations.init.world.resourcenode;

import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.noise.PerlinNoiseSampler;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;
import org.jetbrains.annotations.Nullable;
import rearth.excavations.Excavation;
import rearth.oritech.Oritech;

import java.util.List;

public class ExcavationSurfaceNodeFeature extends Feature<ExcavationNodeFeatureConfig> {
    
    // uses just nodeOres.First and surfaceOres.first, but I was too lazy to create another config
    public ExcavationSurfaceNodeFeature(Codec<ExcavationNodeFeatureConfig> configCodec) {
        super(configCodec);
    }
    
    @Override
    public boolean generate(FeatureContext<ExcavationNodeFeatureConfig> context) {
        
        var world = context.getWorld();
        var origin = context.getOrigin();
        
        if (world.isClient()) return false;
        
        // place ore node in middle
        var orePos = origin.up(2);
        orePos = getSolidSurface(orePos, world);
        if (orePos == null) return false;
        
        Excavation.LOGGER.debug("Placing deep node at: " + orePos);
        
        world.setBlockState(orePos, Registries.BLOCK.get(context.getConfig().nodeOres().getFirst()).getDefaultState(), Block.NO_REDRAW);
        
        var random = world.getRandom();
        
        // random pillars around the pos
        for (int i = 0; i < 4; i++) {
            var offset = orePos.add(random.nextBetween(-3, 3), 0, random.nextBetween(-3, 3));
            var start = getSolidSurface(offset.up(2), world);
            if (start == null) continue;
            for (int j = 0; j < random.nextBetween(1, 4); j++) {
                world.setBlockState(start.up(j + 1), Registries.BLOCK.get(context.getConfig().boulderOres().getFirst()).getDefaultState(), Block.NO_REDRAW);
            }
        }
        
        return true;
    }
    
    // returns the first solid block, not the empty block above
    private @Nullable BlockPos getSolidSurface(BlockPos from, WorldAccess world) {
        for (int i = 0; i < 10; i++) {
            var testPos = from.down(i);
            var testState = world.getBlockState(testPos);
            if (testState.isSolidBlock(world, testPos)) return testPos;
        }
        
        return null;
    }
}
