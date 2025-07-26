package dev.cakestudio.cakeenderchestfilter.listener;

import dev.cakestudio.cakeenderchestfilter.manager.ConfigManager;
import dev.cakestudio.cakeenderchestfilter.model.StorageRule;
import dev.cakestudio.cakeenderchestfilter.service.FilterManager;
import dev.cakestudio.cakeenderchestfilter.util.HexColor;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class PlayerInventoryListener implements Listener {

    private final FilterManager filterManager;
    private final ConfigManager configManager;

    public PlayerInventoryListener(FilterManager filterManager, ConfigManager configManager) {
        this.filterManager = filterManager;
        this.configManager = configManager;
    }

    private void executeDenyAction(Player player, @NonNull Inventory inventory) {
        StorageRule rule = configManager.getStorageRules().get(inventory.getType());
        if (rule != null) {
            player.playSound(player.getLocation(), rule.sound(), 1.0f, 1.0f);
        }
        player.sendMessage(HexColor.color(configManager.getMessages().getPlayerDeny()));
    }

    private boolean checkAndDeny(Player player, Inventory inventory, ItemStack item) {
        if (inventory == null || item == null) {
            return false;
        }

        boolean isTracked = configManager.getStorageRules().containsKey(inventory.getType());

        if (isTracked && filterManager.isFiltered(inventory.getType(), item)) {
            executeDenyAction(player, inventory);
            return true;
        }

        return false;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInventoryDrag(@NonNull InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (player.hasPermission("cakeenderchestfilter.bypass")) return;

        ItemStack draggedItem = event.getOldCursor();

        for (int slot : event.getRawSlots()) {
            Inventory inv = event.getView().getInventory(slot);
            if (checkAndDeny(player, inv, draggedItem)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInventoryClick(@NonNull InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (player.hasPermission("cakeenderchestfilter.bypass")) return;

        if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
            Inventory destinationInventory = event.getView().getTopInventory();
            if (checkAndDeny(player, destinationInventory, event.getCurrentItem())) {
                event.setCancelled(true);
            }
            return;
        }

        Inventory clickedInventory = event.getClickedInventory();

        ItemStack itemToCheck = switch (event.getClick()) {
            case NUMBER_KEY -> player.getInventory().getItem(event.getHotbarButton());
            case SWAP_OFFHAND -> player.getInventory().getItemInOffHand();
            default -> event.getCursor();
        };

        if (checkAndDeny(player, clickedInventory, itemToCheck)) {
            event.setCancelled(true);
        }
    }
}