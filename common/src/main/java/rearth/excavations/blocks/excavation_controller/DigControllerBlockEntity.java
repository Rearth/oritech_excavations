package rearth.excavations.blocks.excavation_controller;

import dev.architectury.registry.menu.ExtendedMenuProvider;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import rearth.excavations.Excavation;
import rearth.excavations.client.ui.DigControllerScreenHandler;
import rearth.excavations.init.BlockEntitiesContent;
import rearth.oritech.api.networking.NetworkedBlockEntity;
import rearth.oritech.api.networking.SyncField;
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
    private Map<Integer, LayerScan> scanResults = new HashMap<>();
    
    @SyncField
    private BlockPos holeCenter = BlockPos.ORIGIN;
    @SyncField
    private Map<Integer, SegmentScan> groupedAreas = new HashMap<>();
    
    public DigControllerBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntitiesContent.DIG_CONTROLLER_ENTITY, pos, state);
    }
    
    @Override
    public void serverTick(World world, BlockPos pos, BlockState state, NetworkedBlockEntity blockEntity) {
        
        age++;
        if (age > 10 && !state.get(UnstableContainerBlock.SETUP_DONE)) {
            world.setBlockState(pos, state.with(UnstableContainerBlock.SETUP_DONE, true));
        }
        
        if (holeCenter.equals(BlockPos.ORIGIN)) return;
        
        var scanAt = age % 200 - 64;
        scanLayer((int) scanAt);
        
        if (scanAt % 16 == 15) {
            groupResults((int) scanAt);
            this.markDirty();
        }
        
    }
    
    private void scanLayer(int y) {
        
        var from = new BlockPos(holeCenter.getX(), y, holeCenter.getZ());
        
        var maxSearchDist = GetMaxSearchDistAtY(y);
        
        var airBlocks = horizontalAirFloodFill(from, maxSearchDist);
        
        var closestSolid = getClosestNonAirInLayer(from, maxSearchDist / 2);
        var dist = (int) Math.sqrt(closestSolid.getSquaredDistance(from));
        
        var result = new LayerScan(airBlocks.size(), dist);
        
        scanResults.put(y, result);
    }
    
    private void groupResults(int lastScanned) {
        
        var startPoint = lastScanned - (lastScanned % 16);  // rounds down to nearest multiple of 16
        
        var lowestRadius = 100000;
        var lowestSize = 100000;
        var largestRadius = 0;
        var largestSize = 0;
        
        for (int i = 0; i < 16; i++) {
            var layer = startPoint + i;
            var data = scanResults.getOrDefault(layer, LayerScan.NOT_SCANNED);
            if (data == LayerScan.NOT_SCANNED) {
                groupedAreas.put(startPoint, SegmentScan.NOT_SCANNED);
                return;
            }
            
            var radius = data.foundMinRadius;
            var size = data.foundSize;
            
            if (radius < lowestRadius)
                lowestRadius = radius;
            if (radius > largestRadius)
                largestRadius = radius;
            
            if (size < lowestSize)
                lowestSize = size;
            if (size > largestSize)
                largestSize = size;
            
        }
        
        var targetMinRadius = GetMinRadiusAtY(startPoint);
        var targetRadius = GetDesiredRadiusAtY(startPoint);
        
        var targetArea = Math.PI * targetRadius * targetRadius;
        
        var effectiveRadius = (lowestRadius + largestRadius) / 2;
        var effectiveSize = (lowestSize + largestSize) / 2;
        
        var radiusProgress = effectiveRadius / targetRadius;
        var sizeProgress = effectiveSize / targetArea;
        var progress = (radiusProgress + sizeProgress) / 2f;
        progress = Math.clamp(progress, 0, 2);
        
        if (lowestRadius < targetMinRadius) {
            progress /= 4;
        }
        
        
        var result = new SegmentScan(lowestRadius, largestRadius, lowestSize, largestSize, effectiveRadius, effectiveSize, progress);
        
        System.out.println("segment scan at Y " + startPoint + ": " + result);
        
        groupedAreas.put(startPoint, result);
        
    }
    
    public static int GetDesiredRadiusAtY(int y) {
        
        var bottom = -500;
        var top = 200;
        var minRadius = 10;
        var maxRadius = 35;
        
        var totalHeight = top - bottom;
        var totalExtraRadius = maxRadius - minRadius;
        
        var heightOffset = y - bottom;
        var heightPercentage = heightOffset / (float) totalHeight;
        
        return (int) (minRadius + totalExtraRadius * heightPercentage);
        
    }
    
    public static int GetMinRadiusAtY(int y) {
        var factor = 0.6f;
        return (int) (DigControllerBlockEntity.GetDesiredRadiusAtY(y) * factor);
    }
    
    public static int GetMaxSearchDistAtY(int y) {
        var factor = 2.5f;
        return (int) (DigControllerBlockEntity.GetDesiredRadiusAtY(y) * factor + 5);
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
            var layerPoints = horizontalAirFloodFill(midPoint, GetMaxSearchDistAtY(midPoint.getY()));
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
        
        holeCenter = finalBottom;
        
    }
    
    private Optional<BlockPos> sparseDowncast(BlockPos from, int spacing) {
        
        for (int i = 0; i < 500; i += spacing) {
            var checkPos = from.down(i);
            var checkState = world.getBlockState(checkPos);
            if (!checkState.isAir()) return Optional.of(checkPos.up(spacing));
        }
        
        return Optional.empty();
        
    }
    
    private List<BlockPos> horizontalAirFloodFill(BlockPos from, int maxDist) {
        
        var openPositions = new HashSet<BlockPos>();
        openPositions.add(from);
        
        var maxCount = 50 * 50;
        var dist = 0;
        
        var results = new ArrayList<BlockPos>();
        
        while (!openPositions.isEmpty() && results.size() < maxCount && dist < maxDist) {
            
            var nextPositions = new ArrayList<BlockPos>();
            dist++;
            
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
    
    private BlockPos getClosestNonAirInLayer(BlockPos from, int range) {
        
        for (int x = 0; x < range; x++) {
            for (int z = 0; z < range; z++) {
                for (int sign = -1; sign <= 1; sign += 2) {
                    
                    var checkPos = from.add(x * sign, 0, z * sign);
                    var checkState = world.getBlockState(checkPos);
                    
                    if (!checkState.isAir()) return checkPos;
                    
                }
            }
        }
        
        return from.add(0, 100, 0);
        
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
    
    public static void handleReloadTriggered(DigTriggerReloadPacket packet, PlayerEntity player, DynamicRegistryManager dynamicRegistryManager) {
        var blockEntity = player.getWorld().getBlockEntity(packet.pos(), BlockEntitiesContent.DIG_CONTROLLER_ENTITY);
        if (blockEntity.isPresent()) {
            blockEntity.get().findHole();
        }
    }
    
    public BlockPos getHoleCenter() {
        return holeCenter;
    }
    
    public Map<Integer, SegmentScan> getScanResults() {
        return groupedAreas;
    }
    
    public record LayerScan(int foundSize, int foundMinRadius) {
        public static LayerScan NOT_SCANNED = new LayerScan(-1, -1);
    }
    
    public record SegmentScan(int foundMinRadius, int foundMaxRadius, int foundMinSize, int foundMaxSize, int effectiveRadius, int effectiveSize, double progress) {
        public static SegmentScan NOT_SCANNED = new SegmentScan(-1, -1, -1, -1, -1, -1, 0);
    }
    
    
    public record DigTriggerReloadPacket(BlockPos pos) implements CustomPayload {
        @Override
        public Id<? extends CustomPayload> getId() {
            return PACKET_ID;
        }
        
        public static final CustomPayload.Id<DigTriggerReloadPacket> PACKET_ID = new CustomPayload.Id<>(Excavation.id("hole_reload"));
        
        public static final PacketCodec<RegistryByteBuf, DigTriggerReloadPacket> PACKET_CODEC = PacketCodec.tuple(
          BlockPos.PACKET_CODEC, DigTriggerReloadPacket::pos,
          DigTriggerReloadPacket::new
        );
        
    }
}
