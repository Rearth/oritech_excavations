package rearth.excavations.client.init;

import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import rearth.excavations.client.AllayCreatorRenderer;
import rearth.excavations.client.DiggerRenderer;
import rearth.excavations.client.ShattererRenderer;
import rearth.excavations.init.BlockEntitiesContent;
import rearth.oritech.client.renderers.MachineRenderer;

public class RendererContent {
    
    public static void registerRenderers() {
        BlockEntityRendererFactories.register(BlockEntitiesContent.ALLAY_CREATOR_BLOCK_ENTITY, ctx -> new AllayCreatorRenderer("models/allay_creator_block"));
        BlockEntityRendererFactories.register(BlockEntitiesContent.SHATTERER_BLOCK_ENTITY, ctx -> new ShattererRenderer("models/shatterer_block"));
        BlockEntityRendererFactories.register(BlockEntitiesContent.DIGGER_BLOCK_ENTITY, ctx -> new DiggerRenderer("models/digger"));
        BlockEntityRendererFactories.register(BlockEntitiesContent.PRIMITIVE_DEEP_DRILL_ENTITY, ctx -> new MachineRenderer<>("models/primitive_deep_drill"));
    }
    
}
