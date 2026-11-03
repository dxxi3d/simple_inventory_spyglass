package dxxi3d.simple_inventory_spyglass.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dxxi3d.simple_inventory_spyglass.SpyglassSession;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {

    @Unique
    private static final SpyglassSession SPYGLASS_SESSION = new SpyglassSession();

    @WrapOperation(
            method = "handleKeybinds",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;releaseUsingItem(Lnet/minecraft/world/entity/player/Player;)V"
            ),
            require = 0
    )
    private void simple_inventory_spyglass$suppressReleaseWhenActive(MultiPlayerGameMode gameMode, Player player, Operation<Void> original) {
        if (!SPYGLASS_SESSION.isActive()) {
            original.call(gameMode, player);
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void simple_inventory_spyglass$onTick(CallbackInfo ci) {
        SPYGLASS_SESSION.onTick((Minecraft) (Object) this);
    }

    @Inject(method = "disconnect(Lnet/minecraft/client/gui/screens/Screen;ZZ)V", at = @At("HEAD"))
    private void simple_inventory_spyglass$onDisconnect(Screen screen, boolean keepResourcePacks, boolean showWorldUnloadConfirmation, CallbackInfo ci) {
        SPYGLASS_SESSION.onDisconnect();
    }
}
