package dev.cakestudio.cakeenderchestfilter.listener;

import dev.cakestudio.cakeenderchestfilter.configuration.Config;
import dev.cakestudio.cakeenderchestfilter.utils.HexColor;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class InventoryClickListener implements Listener {

    private static final Set<String> VALID_MATERIALS = Arrays.stream(Material.values())
            .map(Enum::name)
            .collect(Collectors.toSet());

    private static final Set<String> VALID_INVENTORY_TYPES = Arrays.stream(InventoryType.values())
            .map(Enum::name)
            .collect(Collectors.toSet());

    private boolean isTargetInventory(Inventory inventory, Configuration config) {
        return inventory != null && config.getStringList("settings.type").stream()
                .anyMatch(type -> VALID_INVENTORY_TYPES.contains(type.toUpperCase()) && inventory.getType().name().equalsIgnoreCase(type));
    }

    private boolean isBlockedItem(ItemStack itemStack, Configuration config) {
        if (itemStack == null || itemStack.getType() == Material.AIR) return true;
        List<String> filter = config.getStringList("settings.items-filter");
        return filter.stream()
                .noneMatch(entry -> VALID_MATERIALS.contains(entry.toUpperCase()) && itemStack.getType().name().equalsIgnoreCase(entry));
    }

    private void denyAction(@NonNull Player player, @NonNull Configuration config) {
        Sound sound = getSafeSound(config.getString("settings.sound"));
        player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
        player.sendMessage(HexColor.color(config.getString("messages.player")));
    }

    private Sound getSafeSound(String name) {
        try {
            return Sound.valueOf(name);
        } catch (IllegalArgumentException e) {
            return Sound.BLOCK_NOTE_BLOCK_BASS;
        }
    }

    @EventHandler
    public void onInventoryDragEvent(@NonNull InventoryDragEvent event) {
        Player player = (Player) event.getWhoClicked();
        Configuration config = Config.getConfig();

        if (player.hasPermission("cakeenderchestfilter.bypass")) return;

        if (event.getRawSlots().stream()
                .noneMatch(slot -> isTargetInventory(event.getView().getInventory(slot), config))) return;

        if (isBlockedItem(event.getOldCursor(), config)) return;

        event.setCancelled(true);
        denyAction(player, config);
    }

    @EventHandler
    public void onInventoryClickEvent(@NonNull InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Configuration config = Config.getConfig();

        if (player.hasPermission("cakeenderchestfilter.bypass")) return;

        Inventory clickedInventory = event.getClickedInventory();
        Inventory topInventory = event.getView().getTopInventory();

        if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
            if (!isTargetInventory(topInventory, config)) return;
            if (isBlockedItem(event.getCurrentItem(), config)) return;

            event.setCancelled(true);
            denyAction(player, config);
            return;
        }

        if (!isTargetInventory(clickedInventory, config)) return;

        switch (event.getClick()) {
            case NUMBER_KEY -> {
                ItemStack hotbarItem = player.getInventory().getItem(event.getHotbarButton());
                if (isBlockedItem(hotbarItem, config)) return;
            }
            case SWAP_OFFHAND -> {
                ItemStack offhandItem = player.getInventory().getItemInOffHand();
                if (isBlockedItem(offhandItem, config)) return;
            }
            default -> {
                if (isBlockedItem(event.getCursor(), config)) return;
            }
        }

        event.setCancelled(true);
        denyAction(player, config);
    }
}