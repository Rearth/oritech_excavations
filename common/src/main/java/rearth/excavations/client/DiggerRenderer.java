package rearth.excavations.client;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.Box;
import rearth.excavations.blocks.digger.DiggerBlockEntity;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class DiggerRenderer extends GeoBlockRenderer<DiggerBlockEntity> {
    
    public DiggerRenderer(String modelPath) {
        super(new DiggerModel(modelPath));
    }
    
    // this overrides a method from IBlockEntityRendererExtension on NF. Since this extension mixin is not available in common, we just declare the methode without\
    // the override annotation
    public Box getRenderBoundingBox(BlockEntity blockEntity) {
        return Box.of(blockEntity.getPos().toCenterPos(), 40, 40, 40);
    }
}
