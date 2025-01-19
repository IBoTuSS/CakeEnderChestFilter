package dev.cakestudio.cakeenderchestfilter.utils;

import dev.cakestudio.cakeenderchestfilter.configuration.Config;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Utils {

    public static boolean isEnderChest(Inventory inventory) {
        return inventory != null && inventory.getType() == InventoryType.ENDER_CHEST;
    }

    public static boolean isItemsFilter(ItemStack itemStack) {
        return itemStack == null || Config.getConfig().getStringList("settings.items-filter").stream()
                .noneMatch(item -> item.equalsIgnoreCase(itemStack.getType().toString()));
    }
}
