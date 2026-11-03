package dxxi3d.simple_inventory_spyglass.neoforge;

import dxxi3d.simple_inventory_spyglass.ModLogger;
import dxxi3d.simple_inventory_spyglass.SharedConstants;
import dxxi3d.simple_inventory_spyglass.SpyglassSession;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

@Mod(SharedConstants.MOD_ID)
public final class MainClientNeoForge {
    public MainClientNeoForge(IEventBus modBus) {
        modBus.addListener(this::registerKeyMappings);
        ModLogger.sendInitialized();
    }

    private void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(SpyglassSession.KEY);
    }
}
