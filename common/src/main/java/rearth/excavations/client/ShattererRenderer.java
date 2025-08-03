package rearth.excavations.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import rearth.excavations.blocks.shatterer.ShattererBlockEntity;
import rearth.oritech.client.renderers.MachineRenderer;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.util.Color;

public class ShattererRenderer extends MachineRenderer<ShattererBlockEntity> {
    
    public ShattererRenderer(String modelPath) {
        super(modelPath, true);
    }
    
    @Override
    public void renderCubesOfBone(MatrixStack poseStack, GeoBone bone, VertexConsumer buffer, int packedLight, int packedOverlay, int colour) {
        
        if (bone.getName().equals("statusLed") && this.animatable != null) {
            
            var fillAmount = this.animatable.energyStorage.getAmount() / (float) this.animatable.energyStorage.getCapacity();
            
            fillAmount = (float) Math.pow(fillAmount, 2);
            
            var color = Color.ofRGB(Math.min(1, 2 - (fillAmount * 2)), Math.min(1, fillAmount * 2), 0.1f);
            
            super.renderCubesOfBone(poseStack, bone, buffer, packedLight, packedOverlay, color.argbInt());
            return;
        }
        
        super.renderCubesOfBone(poseStack, bone, buffer, packedLight, packedOverlay, colour);
    }
    
    @Override
    public void render(ShattererBlockEntity entity, float partialTick, MatrixStack matrices, VertexConsumerProvider bufferSource, int packedLight, int packedOverlay) {
        super.render(entity, partialTick, matrices, bufferSource, packedLight, packedOverlay);
        
        var inputStack = entity.inventory.getStack(0);
        if (!inputStack.isEmpty()) {
            matrices.push();
            matrices.translate(0.5F, 0.9F, 0.5F);
            
            var scale = 1.2f;
            matrices.scale(scale, scale, scale);
            
            var rotation = (entity.getWorld().getTime() + partialTick) * 6;
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotation % 360));
            
            MinecraftClient.getInstance().getItemRenderer().renderItem(inputStack, ModelTransformationMode.FIXED, 15728640, packedOverlay, matrices, bufferSource, entity.getWorld(), 0);
            matrices.pop();
        }
    }
}
