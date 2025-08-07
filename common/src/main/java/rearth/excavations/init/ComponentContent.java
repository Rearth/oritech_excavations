package rearth.excavations.init;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.component.ComponentType;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.math.BlockPos;
import rearth.excavations.Excavation;
import rearth.oritech.Oritech;

import java.util.List;

public class ComponentContent {
    
    public static final DeferredRegister<ComponentType<?>> COMPONENTS = DeferredRegister.create(Excavation.MOD_ID, RegistryKeys.DATA_COMPONENT_TYPE);
    
    public static final RegistrySupplier<ComponentType<List<BlockPos>>> TARGET_POSITIONS = COMPONENTS.register(
      "target_positions", () -> ComponentType.<List<BlockPos>>builder()
                                 .codec(BlockPos.CODEC.listOf())
                                 .packetCodec(BlockPos.PACKET_CODEC.collect(PacketCodecs.toList())).build());
    
}
