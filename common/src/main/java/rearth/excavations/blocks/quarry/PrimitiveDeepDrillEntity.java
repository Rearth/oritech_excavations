package rearth.excavations.blocks.quarry;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import rearth.excavations.init.BlockEntitiesContent;
import rearth.oritech.api.energy.EnergyApi;
import rearth.oritech.api.networking.NetworkedBlockEntity;
import rearth.oritech.block.entity.interaction.DeepDrillEntity;
import rearth.oritech.block.entity.processing.PulverizerBlockEntity;
import rearth.oritech.client.init.ParticleContent;
import rearth.oritech.init.TagContent;

import java.util.List;

public class PrimitiveDeepDrillEntity extends DeepDrillEntity {
    
    public PrimitiveDeepDrillEntity(BlockPos pos, BlockState state) {
        super(BlockEntitiesContent.PRIMITIVE_DEEP_DRILL_ENTITY, pos, state);
    }
    
    @Override
    public void loadOreBlocks(boolean manual) {
        
        var target = this.pos.down();
        var targetState = world.getBlockState(target);
        if (targetState.isIn(TagContent.RESOURCE_NODES)) {
            if (manual) ParticleContent.DEBUG_BLOCK.spawn(world, Vec3d.of(target));
            targetedOre.add(targetState.getBlock());
        }
        
    }
    
    @Override
    public List<Vec3i> getCorePositions() {
        return List.of(new Vec3i(0, 1, 0));
    }
    
    @Override
    public EnergyApi.EnergyStorage getEnergyStorageForMultiblock(Direction direction) {
        return energyStorage;
    }
    
    @Override
    public int getRfPerStep() {
        return 256;
    }
    
    @Override
    public int getMaxRfInput() {
        return 64;
    }
}
