package rearth.excavations.init;

import rearth.excavations.blocks.excavation_controller.DigControllerBlockEntity;
import rearth.oritech.api.networking.NetworkManager;

public class NetworkContent {
    
    public static void init() {
        
        NetworkManager.registerToServer(DigControllerBlockEntity.DigTriggerReloadPacket.PACKET_ID, DigControllerBlockEntity.DigTriggerReloadPacket.PACKET_CODEC, DigControllerBlockEntity::handleReloadTriggered);
        
    }
    
}
