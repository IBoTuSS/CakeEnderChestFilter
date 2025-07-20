package dev.cakestudio.cakeenderchestfilter.listener;

import dev.cakestudio.cakeenderchestfilter.configuration.Config;
import dev.cakestudio.cakeenderchestfilter.utils.HexColor;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class InventoryClickListener implements Listener {

    private boolean isTargetInventory(Inventory inventory) {
        if (inventory == null) return false;

        switch (inventory.getType()) {
            case PLAYER, CREATIVE, CRAFTING:
                return false;
            default:
        }

        FileConfiguration config = Config.getConfig();
        String inventoryTypeName = inventory.getType().name();
        ConfigurationSection storages = config.getConfigurationSection("storages");

        if (storages == null) return false;

        return storages.getKeys(false).stream()
                .anyMatch(inventoryTypeName::equalsIgnoreCase);
    }

    private boolean isBlockedItem(@NonNull String storageType, ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() == Material.AIR) return false;

        FileConfiguration config = Config.getConfig();
        List<String> filtered = config.getStringList("storages." + storageType + ".items-filter");

        if (filtered.isEmpty()) return false;

        return filtered.stream().anyMatch(f -> itemStack.getType().name().equalsIgnoreCase(f));
    }

    private void denyAction(@NonNull Player player, @NonNull String storageType) {
        FileConfiguration config = Config.getConfig();
        ConfigurationSection section = config.getConfigurationSection("storages." + storageType);

        if (section != null) {
            String soundName = section.getString("sound");
            if (soundName != null && !soundName.isEmpty()) {
                Sound sound = Sound.BLOCK_NOTE_BLOCK_BASS;
                try {
                    sound = Sound.valueOf(soundName.toUpperCase());
                } catch (IllegalArgumentException ignored) {}
                player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
            }
        }

        player.sendMessage(HexColor.color(config.getString("messages.player")));
    }

    @EventHandler
    public void onInventoryDragEvent(@NonNull InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (player.hasPermission("cakeenderchestfilter.bypass")) return;

        ItemStack draggedItem = event.getOldCursor();

        for (int slot : event.getRawSlots()) {
            Inventory inv = event.getView().getInventory(slot);
            if (isTargetInventory(inv)) {
                if (isBlockedItem(inv.getType().name(), draggedItem)) {
                    event.setCancelled(true);
                    denyAction(player, inv.getType().name());
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClickEvent(@NonNull InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (player.hasPermission("cakeenderchestfilter.bypass")) return;

        if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
            Inventory topInventory = event.getView().getTopInventory();
            if (isTargetInventory(topInventory)) {
                if (isBlockedItem(topInventory.getType().name(), event.getCurrentItem())) {
                    event.setCancelled(true);
                    denyAction(player, topInventory.getType().name());
                }
            }
            return;
        }

        Inventory clickedInventory = event.getClickedInventory();
        if (!isTargetInventory(clickedInventory)) {
            return;
        }

        ItemStack itemToCheck = switch (event.getClick()) {
            case NUMBER_KEY -> player.getInventory().getItem(event.getHotbarButton());
            case SWAP_OFFHAND -> player.getInventory().getItemInOffHand();
            default -> event.getCursor();
        };

        if (isBlockedItem(clickedInventory.getType().name(), itemToCheck)) {
            event.setCancelled(true);
            denyAction(player, clickedInventory.getType().name());
        }
    }
}