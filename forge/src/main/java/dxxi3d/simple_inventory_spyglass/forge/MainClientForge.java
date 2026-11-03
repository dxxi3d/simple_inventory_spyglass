package dxxi3d.simple_inventory_spyglass.forge;

import dxxi3d.simple_inventory_spyglass.ModLogger;
import dxxi3d.simple_inventory_spyglass.SharedConstants;
import dxxi3d.simple_inventory_spyglass.SpyglassSession;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(SharedConstants.MOD_ID)
public final class MainClientForge {
    public MainClientForge() {
        RegisterKeyMappingsEvent.BUS.addListener(this::registerKeyMappings);
        ModLogger.sendInitialized();
    }

    private void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(SpyglassSession.KEY);
    }
}
