package dev.cakestudio.cakeenderchestfilter.listener;

import dev.cakestudio.cakeenderchestfilter.configuration.Config;
import dev.cakestudio.cakeenderchestfilter.utils.HexColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class InventoryClickListener implements Listener {

    @EventHandler
    public void onInventoryClickEvent(@NotNull InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (!player.hasPermission("cakeenderchestfilter.bypass")) {
            if (event.getInventory().getType().equals(InventoryType.ENDER_CHEST)) {
                if (event.getCurrentItem() != null) {
                    boolean items = Config.getConfig().getStringList("settings.items-filter").stream()
                            .anyMatch(item -> item.equals(event
                                    .getCurrentItem()
                                    .getType()
                                    .toString()));

                    if (items && event.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)) {
                        event.setCancelled(true);

                        String sound = Config.getConfig().getString("settings.sound");
                        Sound sound1 = Sound.valueOf(sound);
                        player.playSound(player.getLocation(), sound1, 1.0f, 1.0f);

                        String message = Config.getConfig().getString("messages.player");
                        player.sendMessage(HexColor.color(message));
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInventoryCloseEvent(@NotNull InventoryCloseEvent event) {
        if (event.getInventory().getType() == InventoryType.ENDER_CHEST) {
            Player player = (Player) event.getPlayer();
            if (!player.hasPermission("cakeenderchestfilter.bypass")) {
                ItemStack[] items = event.getInventory().getContents();
                List<ItemStack> give = new ArrayList<>();

                for (int i = 0; i < items.length; i++) {
                    ItemStack item = items[i];
                    if (item != null && Config.getConfig().getStringList("settings.items-filter").contains(item.getType().toString())) {
                        give.add(item);
                        event.getInventory().setItem(i, null);
                    }
                }

                give.forEach(item -> {
                    HashMap<Integer, ItemStack> notAdded = player.getInventory().addItem(item);
                    if (!notAdded.isEmpty()) {
                        player.getWorld().dropItem(player.getLocation(), notAdded.values().iterator().next());
                    }
                });

                if (!give.isEmpty()) {
                    String sound = Config.getConfig().getString("settings.sound");
                    Sound sound1 = Sound.valueOf(sound);
                    player.playSound(player.getLocation(), sound1, 1.0f, 1.0f);

                    String message = Config.getConfig().getString("messages.player");
                    player.sendMessage(HexColor.color(message));
                }
            }
        }
    }

    @EventHandler
    public void onInventoryOpenEvent(@NotNull InventoryOpenEvent event) {
        if (event.getInventory().getType() == InventoryType.ENDER_CHEST) {
            Player player = (Player) event.getPlayer();
            if (!player.hasPermission("cakeenderchestfilter.bypass")) {
                ItemStack[] items = event.getInventory().getContents();
                List<ItemStack> give = new ArrayList<>();

                for (int i = 0; i < items.length; i++) {
                    ItemStack item = items[i];
                    if (item != null && Config.getConfig().getStringList("settings.items-filter").contains(item.getType().toString())) {
                        give.add(item);
                        event.getInventory().setItem(i, null);
                    }
                }

                give.forEach(item -> {
                    player.getInventory().addItem(item);

                    String sound = Config.getConfig().getString("settings.sound");
                    Sound sound1 = Sound.valueOf(sound);
                    player.playSound(player.getLocation(), sound1, 1.0f, 1.0f);

                    String message = Config.getConfig().getString("messages.player");
                    player.sendMessage(HexColor.color(message));
                });

                if (!give.isEmpty()) {
                    player.closeInventory();

                    String sound = Config.getConfig().getString("settings.sound");
                    Sound sound1 = Sound.valueOf(sound);
                    player.playSound(player.getLocation(), sound1, 1.0f, 1.0f);

                    String message = Config.getConfig().getString("messages.player");
                    player.sendMessage(HexColor.color(message));
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(@NotNull PlayerDeathEvent event) {
        Player player = event.getEntity();
        Inventory openInventory = player.getOpenInventory().getTopInventory();

        if (openInventory.getType().equals(InventoryType.ENDER_CHEST)) {
            List<ItemStack> drop = Arrays.stream(openInventory.getContents())
                    .filter(item -> item != null && Config.getConfig().getStringList("settings.items-filter").contains(item.getType().toString()))
                    .toList();

            drop.forEach(item -> {
                openInventory.remove(item);
                player.getWorld().dropItemNaturally(player.getLocation(), item);
            });
        }
    }
}
