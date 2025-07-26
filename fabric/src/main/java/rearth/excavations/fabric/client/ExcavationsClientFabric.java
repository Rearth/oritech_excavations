package rearth.excavations.fabric.client;

import net.fabricmc.api.ClientModInitializer;
import rearth.excavations.ExcavationClient;
import rearth.excavations.client.init.RendererContent;

public final class ExcavationsClientFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // This entrypoint is suitable for setting up client-specific logic, such as rendering.
        ExcavationClient.init();
        RendererContent.registerRenderers();
    }
}
