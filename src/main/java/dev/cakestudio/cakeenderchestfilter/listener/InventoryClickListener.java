package dev.cakestudio.cakeenderchestfilter.listener;

import dev.cakestudio.cakeenderchestfilter.configuration.Config;
import dev.cakestudio.cakeenderchestfilter.utils.HexColor;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class InventoryClickListener implements Listener {

    @EventHandler
    public void onInventoryClickEvent(@NotNull InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (!player.hasPermission("cakeenderchestfilter.bypass")) {
            if (event.getInventory().getType().equals(InventoryType.ENDER_CHEST)) {
                if (event.getCurrentItem() != null && Config.getConfig().getStringList("settings.items-filter").contains(event.getCurrentItem().getType().toString())) {
                    if (event.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)) {
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
            List<HumanEntity> viewers = event.getInventory().getViewers();
            if (!viewers.isEmpty()) {
                Player player = (Player) viewers.get(0);
                if (!player.hasPermission("cakeenderchestfilter.bypass")) {
                    ItemStack[] enderChestItems = event.getInventory().getContents();
                    ArrayList<ItemStack> enderChestArray = new ArrayList<>();

                    ArrayList<ItemStack> toGive = new ArrayList<>();

                    for (ItemStack item : enderChestItems) {
                        if (item != null) {
                            if (Config.getConfig().getStringList("settings.items-filter").contains(item.getType().toString())) {
                                toGive.add(item);
                            } else {
                                enderChestArray.add(item);
                            }
                        }
                    }

                    ItemStack[] enderChestItemsNew = enderChestArray.toArray(new ItemStack[0]);
                    event.getInventory().setContents(enderChestItemsNew);

                    for (ItemStack item : toGive) {
                        HashMap<Integer, ItemStack> notAdded = player.getInventory().addItem(item);
                        if (!notAdded.isEmpty()) {
                            player.getWorld().dropItem(player.getLocation(), notAdded.get(0));
                        }
                    }

                    if (!toGive.isEmpty()) {
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
    }

    @EventHandler
    public void onInventoryOpenEvent(@NotNull InventoryOpenEvent event) {
        if (event.getInventory().getType() == InventoryType.ENDER_CHEST) {
            List<HumanEntity> viewers = event.getInventory().getViewers();
            if (!viewers.isEmpty()) {
                Player player = (Player) viewers.get(0);
                if (!player.hasPermission("cakeenderchestfilter.bypass")) {
                    ItemStack[] enderChestItems = event.getInventory().getContents();
                    ArrayList<ItemStack> enderChestArray = new ArrayList<>();

                    ArrayList<ItemStack> toGive = new ArrayList<>();

                    for (ItemStack item : enderChestItems) {
                        if (item != null) {
                            if (Config.getConfig().getStringList("settings.items-filter").contains(item.getType().toString())) {
                                toGive.add(item);
                            } else {
                                enderChestArray.add(item);
                            }
                        }
                    }

                    ItemStack[] enderChestItemsNew = enderChestArray.toArray(new ItemStack[0]);
                    event.getInventory().setContents(enderChestItemsNew);

                    for (ItemStack item : toGive) {
                        player.getInventory().addItem(item);

                        String sound = Config.getConfig().getString("settings.sound");
                        Sound sound1 = Sound.valueOf(sound);
                        player.playSound(player.getLocation(), sound1, 1.0f, 1.0f);

                        String message = Config.getConfig().getString("messages.player");
                        player.sendMessage(HexColor.color(message));
                    }

                    if (!toGive.isEmpty()) {
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
    }

    @EventHandler
    public void onPlayerDeath(@NotNull PlayerDeathEvent event) {
        Player player = event.getEntity();
        Inventory openInventory = player.getOpenInventory().getTopInventory();

        if (openInventory.getType().equals(InventoryType.ENDER_CHEST)) {
            ArrayList<ItemStack> toDrop = new ArrayList<>();

            for (ItemStack item : openInventory.getContents()) {
                if (item != null && Config.getConfig().getStringList("settings.items-filter").contains(item.getType().toString())) {
                    toDrop.add(item);
                }
            }

            for (ItemStack item : toDrop) {
                openInventory.remove(item);
                player.getWorld().dropItemNaturally(player.getLocation(), item);
            }
        }
    }

    @EventHandler
    public void onPlayerSwapHandItems(@NotNull PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPermission("cakeenderchestfilter.bypass")) {
            ItemStack mainHandItem = event.getMainHandItem();
            ItemStack offHandItem = event.getOffHandItem();

            assert mainHandItem != null;
            if (Config.getConfig().getStringList("settings.items-filter").contains(mainHandItem.getType().toString()) || Config.getConfig().getStringList("settings.items-ban-list").contains(Objects.requireNonNull(offHandItem).getType().toString())) {
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
