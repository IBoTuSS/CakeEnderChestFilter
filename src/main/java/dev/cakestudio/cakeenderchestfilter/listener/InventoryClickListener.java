package dev.cakestudio.cakeenderchestfilter.listener;

import dev.cakestudio.cakeenderchestfilter.configuration.Config;
import dev.cakestudio.cakeenderchestfilter.utils.HexColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class InventoryClickListener implements Listener {

    private boolean isInventory(Inventory inventory) {
        return inventory != null && Config.getConfig().getStringList("settings.type").contains(inventory.getType().toString());
    }

    private boolean isItemsFilter(ItemStack itemStack) {
        return itemStack == null || Config.getConfig().getStringList("settings.items-filter").stream()
                .noneMatch(item -> item.equalsIgnoreCase(itemStack.getType().toString()));
    }

    @EventHandler
    public void onInventoryDragEvent(@NotNull InventoryDragEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (player.hasPermission("cakeenderchestfilter.bypass")) {
            return;
        }

        if (event.getRawSlots().stream()
                .noneMatch(slot -> isInventory(event.getView().getInventory(slot)))) {
            return;
        }
        if (isItemsFilter(event.getOldCursor())) {
            return;
        }

        event.setCancelled(true);
        Sound sound = Sound.valueOf(Config.getConfig().getString("settings.sound"));
        player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
        player.sendMessage(HexColor.color(Config.getConfig().getString("messages.player")));
    }

    @EventHandler
    public void onInventoryClickEvent(@NotNull InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (player.hasPermission("cakeenderchestfilter.bypass")) {
            return;
        }

        if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
            if (!isInventory(event.getView().getTopInventory())) {
                return;
            }

            if (isItemsFilter(event.getCurrentItem())) {
                return;
            }

            event.setCancelled(true);
            Sound sound = Sound.valueOf(Config.getConfig().getString("settings.sound"));
            player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
            player.sendMessage(HexColor.color(Config.getConfig().getString("messages.player")));
            return;
        }

        if (!isInventory(event.getClickedInventory())) {
            return;
        }

        if (event.getClick() == ClickType.NUMBER_KEY) {
            if (isItemsFilter(event.getWhoClicked().getInventory().getItem(event.getHotbarButton()))) {
                return;
            }
        } else if (event.getClick() == ClickType.SWAP_OFFHAND) {
            if (isItemsFilter(event.getWhoClicked().getInventory().getItemInOffHand())) {
                return;
            }
        } else if (isItemsFilter(event.getCursor())) {
            return;
        }

        event.setCancelled(true);
        Sound sound = Sound.valueOf(Config.getConfig().getString("settings.sound"));
        player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
        player.sendMessage(HexColor.color(Config.getConfig().getString("messages.player")));
    }
}
