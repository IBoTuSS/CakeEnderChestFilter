package dev.cakestudio.cakeenderchestfilter.model;

import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public record MaterialFilterRule(long id, Material material) implements FilterRule {

    @Override
    public boolean matches(ItemStack item) {
        return item != null && item.getType() == this.material;
    }

    @Override
    public @NonNull String toString() {
        return String.format("ID: %d, Type: MATERIAL, Criteria: %s", id, material.name());
    }
}