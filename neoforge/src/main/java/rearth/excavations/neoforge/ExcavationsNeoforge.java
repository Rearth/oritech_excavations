package rearth.excavations.neoforge;

import net.neoforged.fml.common.Mod;

import rearth.excavations.Excavation;

@Mod(Excavation.MOD_ID)
public final class ExcavationsNeoforge {
    public ExcavationsNeoforge() {
        // Run our common setup.
        Excavation.init();
    }
}
