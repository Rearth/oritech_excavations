package rearth.excavations.fabric.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import rearth.excavations.ExcavationClient;
import rearth.excavations.client.init.RendererContent;
import rearth.oritech.client.init.ModRenderers;

public final class ExcavationsClientFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // This entrypoint is suitable for setting up client-specific logic, such as rendering.
        ExcavationClient.init();
        RendererContent.registerRenderers();
        
        for (var entry : RendererContent.RENDER_LAYERS.entrySet()) {
            BlockRenderLayerMap.INSTANCE.putBlock(entry.getKey(), entry.getValue());
        }
        
    }
}
