package rearth.excavations.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import rearth.excavations.blocks.allay_creator.AllayCreatorBlockEntity;
import rearth.oritech.client.renderers.MachineRenderer;
import rearth.oritech.util.Geometry;
import software.bernie.geckolib.cache.object.BakedGeoModel;

public class AllayCreatorRenderer extends MachineRenderer<AllayCreatorBlockEntity> {
    
    public AllayCreatorRenderer(String modelPath) {
        super(modelPath);
    }
    
    @Override
    public void postRender(MatrixStack matrices, AllayCreatorBlockEntity entity, BakedGeoModel model, VertexConsumerProvider vertexConsumers, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        super.postRender(matrices, entity, model, vertexConsumers, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
        
        var recipe = entity.getCurrentRecipe();
        if (recipe.getResults().size() != 1) return;
        
        var resultItem = recipe.getResults().getFirst();
        if (!(resultItem.getItem() instanceof SpawnEggItem spawnEggItem)) return;
        
        var resultingEntity = spawnEggItem.getEntityType(resultItem);
        if (resultingEntity == null) return;
        
        var progress = entity.progress;
        var maxProgress = recipe.getTime();
        if (progress <= 0 || progress >= maxProgress) return;
        
        var scaledProgress = progress / (float) maxProgress;
        
        var renderedEntity = resultingEntity.create(entity.getWorld());
        var dispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();
        var renderer = dispatcher.getRenderer(renderedEntity);
        
        var yaw = Math.sin((entity.getWorld().getTime() + partialTick) / 20f) * 90;
        
        var blockFacing = entity.getFacingForMultiblock();
        var offset = Geometry.rotatePosition(new Vec3d(0.5, 0.8, -0.5), blockFacing);
        
        matrices.push();
        matrices.translate(offset.getX(), offset.y, offset.getZ());
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float) yaw));
        matrices.scale(scaledProgress, scaledProgress, scaledProgress);
        
        renderer.render(renderedEntity, 0, 0, matrices, vertexConsumers, packedLight);
        
        matrices.pop();
        
    }
}
