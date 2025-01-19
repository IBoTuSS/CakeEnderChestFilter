package dev.cakestudio.cakeenderchestfilter.listener;

import dev.cakestudio.cakeenderchestfilter.configuration.Config;
import dev.cakestudio.cakeenderchestfilter.utils.HexColor;
import dev.cakestudio.cakeenderchestfilter.utils.Utils;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.jetbrains.annotations.NotNull;

public class InventoryClickListener implements Listener {

    private final String SOUND = Config.getConfig().getString("settings.sound");
    private final String MESSAGE = Config.getConfig().getString("messages.player");

    @EventHandler
    public void onInventoryDragEvent(@NotNull InventoryDragEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (player.hasPermission("cakeenderchestfilter.bypass")) {
            return;
        }

        if (event.getRawSlots().stream()
                .noneMatch(slot -> Utils.isEnderChest(event.getView().getInventory(slot)))) {
            return;
        }
        if (Utils.isItemsFilter(event.getOldCursor())) {
            return;
        }

        event.setCancelled(true);
        Sound sound = Sound.valueOf(SOUND);
        player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
        player.sendMessage(HexColor.color(MESSAGE));
    }

    @EventHandler
    public void onInventoryClickEvent(@NotNull InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (player.hasPermission("cakeenderchestfilter.bypass")) {
            return;
        }

        if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
            if (!Utils.isEnderChest(event.getView().getTopInventory())) {
                return;
            }

            if (Utils.isItemsFilter(event.getCurrentItem())) {
                return;
            }

            event.setCancelled(true);
            Sound sound = Sound.valueOf(SOUND);
            player.playSound(player.getLocation(), sound, 1.0f, 1.0f);

            player.sendMessage(HexColor.color(MESSAGE));
            return;
        }

        if (!Utils.isEnderChest(event.getClickedInventory())) {
            return;
        }

        if (event.getClick() == ClickType.NUMBER_KEY) {
            if (Utils.isItemsFilter(event.getWhoClicked().getInventory().getItem(event.getHotbarButton()))) {
                return;
            }
        } else if (event.getClick() == ClickType.SWAP_OFFHAND) {
            if (Utils.isItemsFilter(event.getWhoClicked().getInventory().getItemInOffHand())) {
                return;
            }
        } else if (Utils.isItemsFilter(event.getCursor())) {
            return;
        }

        event.setCancelled(true);
        Sound sound = Sound.valueOf(SOUND);
        player.playSound(player.getLocation(), sound, 1.0f, 1.0f);

        player.sendMessage(HexColor.color(MESSAGE));
    }
}
