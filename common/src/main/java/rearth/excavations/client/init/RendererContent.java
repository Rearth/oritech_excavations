package rearth.excavations.client.init;

import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import rearth.excavations.client.AllayCreatorRenderer;
import rearth.excavations.init.BlockEntitiesContent;
import rearth.oritech.client.renderers.MachineRenderer;

public class RendererContent {
    
    public static void registerRenderers() {
        BlockEntityRendererFactories.register(BlockEntitiesContent.ALLAY_CREATOR_BLOCK_ENTITY, ctx -> new AllayCreatorRenderer("models/allay_creator_block"));
        BlockEntityRendererFactories.register(BlockEntitiesContent.SHATTERER_BLOCK_ENTITY, ctx -> new MachineRenderer<>("models/shatterer_block"));
    }
    
}
