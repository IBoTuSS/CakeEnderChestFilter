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
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class InventoryClickListener implements Listener {

    private boolean isTargetInventory(Inventory inventory) {
        if (inventory == null) return false;
        FileConfiguration config = Config.getConfig();
        return config.getConfigurationSection("storages") != null &&
                config.getConfigurationSection("storages").getKeys(false).stream()
                        .anyMatch(type -> inventory.getType().name().equalsIgnoreCase(type));
    }

    private boolean isBlockedItem(@NonNull Inventory inventory, ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() == Material.AIR) return false;

        FileConfiguration config = Config.getConfig();
        String type = inventory.getType().name();
        ConfigurationSection section = config.getConfigurationSection("storages." + type);
        if (section == null) return false;

        List<String> filtered = section.getStringList("items-filter");
        return filtered.stream().anyMatch(f -> itemStack.getType().name().equalsIgnoreCase(f));
    }

    private void denyAction(@NonNull Player player, @NonNull Inventory inventory) {
        FileConfiguration config = Config.getConfig();
        String type = inventory.getType().name();
        ConfigurationSection section = config.getConfigurationSection("storages." + type);

        if (section != null) {
            String soundName = section.getString("sound");
            Sound sound = Sound.BLOCK_NOTE_BLOCK_BASS;
            try {
                sound = Sound.valueOf(soundName.toUpperCase());
            } catch (IllegalArgumentException ignored) {}
            player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
        }

        player.sendMessage(HexColor.color(config.getString("messages.player")));
    }

    @EventHandler
    public void onInventoryDragEvent(@NonNull InventoryDragEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (player.hasPermission("cakeenderchestfilter.bypass")) return;

        InventoryView view = event.getView();
        for (int slot : event.getRawSlots()) {
            Inventory inv = view.getInventory(slot);
            if (isTargetInventory(inv) && isBlockedItem(inv, event.getOldCursor())) {
                event.setCancelled(true);
                denyAction(player, inv);
                return;
            }
        }
    }

    @EventHandler
    public void onInventoryClickEvent(@NonNull InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (player.hasPermission("cakeenderchestfilter.bypass")) return;

        Inventory clickedInventory = event.getClickedInventory();
        Inventory topInventory = event.getView().getTopInventory();

        if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
            if (isTargetInventory(topInventory) && isBlockedItem(topInventory, event.getCurrentItem())) {
                event.setCancelled(true);
                denyAction(player, topInventory);
            }
            return;
        }

        if (!isTargetInventory(clickedInventory)) return;

        ItemStack itemToCheck = switch (event.getClick()) {
            case NUMBER_KEY -> player.getInventory().getItem(event.getHotbarButton());
            case SWAP_OFFHAND -> player.getInventory().getItemInOffHand();
            default -> event.getCursor();
        };

        if (isBlockedItem(clickedInventory, itemToCheck)) {
            event.setCancelled(true);
            denyAction(player, clickedInventory);
        }
    }
}