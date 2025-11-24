package rearth.excavations.blocks.excavation_controller;

import dev.architectury.registry.menu.ExtendedMenuProvider;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import rearth.excavations.client.ui.DigControllerScreenHandler;
import rearth.excavations.init.BlockEntitiesContent;
import rearth.oritech.api.networking.NetworkedBlockEntity;
import rearth.oritech.api.networking.SyncType;
import rearth.oritech.block.base.entity.MachineBlockEntity;
import rearth.oritech.block.blocks.storage.UnstableContainerBlock;
import rearth.oritech.client.init.ParticleContent;
import rearth.oritech.util.AutoPlayingSoundKeyframeHandler;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.*;

import static rearth.oritech.block.base.entity.MachineBlockEntity.IDLE;

public class DigControllerBlockEntity extends NetworkedBlockEntity implements GeoBlockEntity, ExtendedMenuProvider {
    
    protected final AnimatableInstanceCache animatableInstanceCache = GeckoLibUtil.createInstanceCache(this);
    
    private long age = 0;
    
    public DigControllerBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntitiesContent.DIG_CONTROLLER_ENTITY, pos, state);
    }
    
    @Override
    public void serverTick(World world, BlockPos pos, BlockState state, NetworkedBlockEntity blockEntity) {
        
        age++;
        if (age > 10 && !state.get(UnstableContainerBlock.SETUP_DONE)) {
            world.setBlockState(pos, state.with(UnstableContainerBlock.SETUP_DONE, true));
        }
        
    }
    
    // tries to find the deepest area (and center of it) by sampling random positions in an expanding area behind the machine
    private void findHole() {
        
        var backDir = getCachedState().get(Properties.HORIZONTAL_FACING).getOpposite();
        var backOffset = backDir.getVector();
        var rightOffset = backDir.rotateYClockwise().getVector();
        
        // start by locating deepest point
        var lowestPoint = this.pos;
        
        // total of up to 40 blocks back, in a 3-wide spacing
        for (int back = 0; back < 70; back += 3) {
            var sideDist = 30 - Math.abs(30 - back);
            sideDist = Math.clamp(sideDist, 1, 22);
            for (int side = -sideDist; side < sideDist; side += 2) {
                
                var offset = backOffset.multiply(back).add(rightOffset.multiply(side));
                var flatPos = this.pos.add(offset);
                var startFrom = flatPos.add(0, 80, 0);
                
                var find = sparseDowncast(startFrom, 2);
                if (find.isEmpty()) {
                    lowestPoint = this.pos.down(1000);
                } else if (find.get().getY() < lowestPoint.getY()) {
                    lowestPoint = find.get();
                }
                
            }
            
        }
        
        var horizontalDiff = this.getPos().getY() - lowestPoint.getY();
        
        var midPoints = Math.min(10, horizontalDiff / 3);
        var sum = lowestPoint.toBottomCenterPos();
        var tightestLayerCenter = lowestPoint;
        var tightestLayerCount = 10000;
        
        for (int i = 1; i <= midPoints; i++) {
            var midPoint = lowestPoint.add(0, (int) (i * horizontalDiff / (float) midPoints), 0);
            var layerPoints = horizontalAirFloodFill(midPoint);
            midPoint = getCenter(layerPoints);
            // ParticleContent.DEBUG_BLOCK.spawn(world, Vec3d.of(midPoint));
            sum = sum.add(Vec3d.of(midPoint));
            
            if (layerPoints.size() < tightestLayerCount) {
                tightestLayerCount = layerPoints.size();
                tightestLayerCenter = midPoint;
            }
        }
        
        sum = sum.add(Vec3d.of(tightestLayerCenter.multiply(10)));
        
        var finalCenterX = sum.getX() / (midPoints + 11);
        var finalCenterZ = sum.getZ() / (midPoints + 11);
        var finalBottom = new BlockPos((int) finalCenterX, lowestPoint.getY(), (int) finalCenterZ);
        
        for (int i = 0; i < horizontalDiff; i += 2) {
            var debugPos = finalBottom.add(0, i, 0);
            ParticleContent.DEBUG_BLOCK.spawn(world, Vec3d.of(debugPos));
        }
        
    }
    
    private Optional<BlockPos> sparseDowncast(BlockPos from, int spacing) {
        
        for (int i = 0; i < 500; i += spacing) {
            var checkPos = from.down(i);
            var checkState = world.getBlockState(checkPos);
            if (!checkState.isAir()) return Optional.of(checkPos.up(spacing));
        }
        
        return Optional.empty();
        
    }
    
    private List<BlockPos> horizontalAirFloodFill(BlockPos from) {
        
        var openPositions = new HashSet<BlockPos>();
        openPositions.add(from);
        
        var maxCount = 50 * 50;
        
        var results = new ArrayList<BlockPos>();
        
        while (!openPositions.isEmpty() && results.size() < maxCount) {
            
            var nextPositions = new ArrayList<BlockPos>();
            
            for (var openPos : openPositions) {
                var openState = world.getBlockState(openPos);
                if (openState.isAir()) {
                    // match
                    results.add(openPos);
                    
                    // add neighbors
                    nextPositions.add(openPos.north());
                    nextPositions.add(openPos.east());
                    nextPositions.add(openPos.south());
                    nextPositions.add(openPos.west());
                }
            }
            
            openPositions.clear();
            openPositions.addAll(nextPositions);
            
        }
        
        return results;
        
    }
    
    private BlockPos getCenter(Collection<BlockPos> list) {
        var sum = Vec3d.ZERO;
        
        for (var elem : list) {
            sum = Vec3d.of(elem).add(sum);
        }
        
        return BlockPos.ofFloored(sum.multiply(1 / (float) list.size()));
        
    }
    
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, 0, state -> {
            if (state.getController().getAnimationState().equals(AnimationController.State.STOPPED)) {
                if (this.getCachedState().get(UnstableContainerBlock.SETUP_DONE)) {
                    return state.setAndContinue(IDLE);
                } else {
                    return state.setAndContinue(MachineBlockEntity.SETUP);
                }
            }
            return PlayState.CONTINUE;
        }).setSoundKeyframeHandler(new AutoPlayingSoundKeyframeHandler<>()));
    }
    
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return animatableInstanceCache;
    }
    
    @Override
    public void saveExtraData(PacketByteBuf buf) {
        findHole();
        sendUpdate(SyncType.GUI_OPEN);
        buf.writeBlockPos(pos);
    }
    
    @Override
    public Text getDisplayName() {
        return Text.of("");
    }
    
    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new DigControllerScreenHandler(syncId, playerInventory, this);
    }
}
