package dxxi3d.simple_inventory_spyglass;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.Items;

public final class SpyglassSession {
    public static final KeyMapping KEY = new KeyMapping(
            "key.simple_inventory_spyglass",
            InputConstants.KEY_Z,
            KeyMapping.Category.GAMEPLAY
    );

    private SpyglassTarget target;
    private int originalHotbarSlot = -1;
    private int sourceSlot = -1;
    private boolean wasKeyDown = false;

    public boolean isActive() {
        return this.target != null;
    }

    public void onTick(Minecraft mc) {
        if (mc.player == null || mc.gameMode == null) {
            abort(mc.player, mc.gameMode);
            this.wasKeyDown = false;
            return;
        }

        if (mc.gui != null && (mc.gui.screen() != null || mc.gui.overlay() != null)) {
            abort(mc.player, mc.gameMode);
            this.wasKeyDown = false;
            return;
        }

        if (mc.player.isDeadOrDying() || mc.player.isSpectator()) {
            abort(mc.player, mc.gameMode);
            this.wasKeyDown = false;
            return;
        }

        boolean isKeyDown = KEY.isDown();

        if (isKeyDown && !this.wasKeyDown) {
            start(mc.player, mc.gameMode);
        } else if (isKeyDown) {
            maintain(mc.player, mc.gameMode);
        } else if (this.wasKeyDown) {
            abort(mc.player, mc.gameMode);
        }

        this.wasKeyDown = isKeyDown;
    }

    public void onDisconnect() {
        abort(null, null);
        this.wasKeyDown = false;
    }

    public void abort(Player player, MultiPlayerGameMode gameMode) {
        if (!isActive()) {
            return;
        }

        if (player != null && gameMode != null) {
            if (player.isUsingItem()) {
                gameMode.releaseUsingItem(player);
            }
            revertInventory(player, gameMode);
        }

        reset();
    }

    private void start(Player player, MultiPlayerGameMode gameMode) {
        if (isActive() || !player.containerMenu.getCarried().isEmpty()) {
            return;
        }

        if (player.isUsingItem() && !player.getUseItem().is(Items.SPYGLASS)) {
            gameMode.releaseUsingItem(player);
        }

        if (player.getMainHandItem().is(Items.SPYGLASS)) {
            this.target = SpyglassTarget.MAIN_HAND;
            gameMode.useItem(player, InteractionHand.MAIN_HAND);
            return;
        }

        if (player.getOffhandItem().is(Items.SPYGLASS)) {
            this.target = SpyglassTarget.OFFHAND;
            gameMode.useItem(player, InteractionHand.OFF_HAND);
            return;
        }

        Inventory inventory = player.getInventory();
        int hotbarSlot = findHotbarSpyglass(inventory);
        if (hotbarSlot != -1) {
            this.target = SpyglassTarget.HOTBAR;
            this.originalHotbarSlot = inventory.getSelectedSlot();
            inventory.setSelectedSlot(hotbarSlot);
            gameMode.useItem(player, InteractionHand.MAIN_HAND);
            return;
        }

        int inventorySlot = findInventorySpyglass(inventory);
        if (inventorySlot != -1) {
            int selectedHotbar = inventory.getSelectedSlot();
            this.target = SpyglassTarget.INVENTORY;
            this.sourceSlot = inventorySlot;
            this.originalHotbarSlot = selectedHotbar;
            swapWithHotbar(gameMode, player, inventorySlot, selectedHotbar);
            gameMode.useItem(player, InteractionHand.MAIN_HAND);
            return;
        }

        player.sendOverlayMessage(Component.translatable("simple_inventory_spyglass.spyglass_not_present"));
    }

    private void maintain(Player player, MultiPlayerGameMode gameMode) {
        if (!isActive()) {
            return;
        }

        InteractionHand hand = (this.target == SpyglassTarget.OFFHAND)
                ? InteractionHand.OFF_HAND
                : InteractionHand.MAIN_HAND;

        if (!player.getItemInHand(hand).is(Items.SPYGLASS)) {
            abort(player, gameMode);
            return;
        }

        if (!player.isUsingItem()) {
            gameMode.useItem(player, hand);
        }
    }

    private int findHotbarSpyglass(Inventory inventory) {
        for (int i = 0; i < Inventory.SELECTION_SIZE; i++) {
            if (inventory.getItem(i).is(Items.SPYGLASS)) {
                return i;
            }
        }
        return -1;
    }

    private int findInventorySpyglass(Inventory inventory) {
        for (int i = InventoryMenu.INV_SLOT_START; i < InventoryMenu.INV_SLOT_END; i++) {
            if (inventory.getItem(i).is(Items.SPYGLASS)) {
                return i;
            }
        }
        return -1;
    }

    private void revertInventory(Player player, MultiPlayerGameMode gameMode) {
        if (this.target == SpyglassTarget.HOTBAR && this.originalHotbarSlot != -1) {
            player.getInventory().setSelectedSlot(this.originalHotbarSlot);
        } else if (this.target == SpyglassTarget.INVENTORY && this.sourceSlot != -1 && this.originalHotbarSlot != -1) {
            if (player.containerMenu.containerId == InventoryMenu.CONTAINER_ID) {
                swapWithHotbar(gameMode, player, this.sourceSlot, this.originalHotbarSlot);
            }
        }
    }

    private void swapWithHotbar(MultiPlayerGameMode gameMode, Player player, int menuSlot, int hotbarIndex) {
        gameMode.handleContainerInput(InventoryMenu.CONTAINER_ID, menuSlot, hotbarIndex, ContainerInput.SWAP, player);
    }

    private void reset() {
        this.target = null;
        this.originalHotbarSlot = -1;
        this.sourceSlot = -1;
    }
}
