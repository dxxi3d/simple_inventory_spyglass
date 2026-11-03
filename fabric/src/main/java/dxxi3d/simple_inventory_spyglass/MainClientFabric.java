package dxxi3d.simple_inventory_spyglass;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;

public final class MainClientFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        KeyMappingHelper.registerKeyMapping(SpyglassSession.KEY);
        ModLogger.sendInitialized();
    }
}
