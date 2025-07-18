package rearth.excavations.client;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;
import org.jetbrains.annotations.Nullable;
import rearth.excavations.Excavation;
import rearth.excavations.entities.BetterAllayEntity;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.BlockAndItemGeoLayer;

public class BetterAllayRenderer extends GeoEntityRenderer<BetterAllayEntity> {
    
    public BetterAllayRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new DefaultedEntityGeoModel<>(Excavation.id("better_allay"), true));
        
        addRenderLayer(new BlockAndItemGeoLayer<>(this) {
            @Override
            protected @Nullable ItemStack getStackForBone(GeoBone bone, BetterAllayEntity animatable) {
                
                if (!animatable.getSyncedTool().isEmpty() && bone.getName().equals("right_arm")) {
                    return animatable.getSyncedTool();
                }
                
                return super.getStackForBone(bone, animatable);
            }
            
            @Override
            protected ModelTransformationMode getTransformTypeForStack(GeoBone bone, ItemStack stack, BetterAllayEntity animatable) {
                return ModelTransformationMode.THIRD_PERSON_RIGHT_HAND;
            }
            
            @Override
            protected void renderStackForBone(MatrixStack poseStack, GeoBone bone, ItemStack stack, BetterAllayEntity animatable, VertexConsumerProvider bufferSource, float partialTick, int packedLight, int packedOverlay) {
                
                // there's only one stack here
                if (!stack.isEmpty()) {
                    var scale = 0.6f;
                    poseStack.translate(-0.1, -0.2, 0.2);
                    poseStack.scale(scale, scale, scale);
                    poseStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(60 + 90));
                }
                
                super.renderStackForBone(poseStack, bone, stack, animatable, bufferSource, partialTick, packedLight, packedOverlay);
            }
        });
        
    }
}
