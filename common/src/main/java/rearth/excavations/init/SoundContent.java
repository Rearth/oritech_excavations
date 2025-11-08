package rearth.excavations.init;

import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.SoundEvent;
import rearth.excavations.Excavation;
import rearth.oritech.Oritech;
import rearth.oritech.util.registry.ArchitecturyRegistryContainer;

public class SoundContent implements ArchitecturyRegistryContainer<SoundEvent> {
    
    public static final SoundEvent INTRO_LINE = SoundEvent.of(Excavation.id("intro_line"));
    public static final SoundEvent STONE_BREAKING = SoundEvent.of(Excavation.id("stone_breaking"));
    
    @Override
    public Class<SoundEvent> getTargetFieldType() {
        return SoundEvent.class;
    }
    
    @Override
    public RegistryKey<Registry<SoundEvent>> getRegistryType() {
        return RegistryKeys.SOUND_EVENT;
    }
}
