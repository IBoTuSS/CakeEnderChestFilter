package dev.cakestudio.cakeenderchestfilter.model;

import org.bukkit.inventory.ItemStack;

public interface FilterRule {
    long id();
    boolean matches(ItemStack item);
}
