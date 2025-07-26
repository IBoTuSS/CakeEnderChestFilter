package dev.cakestudio.cakeenderchestfilter.model;

import lombok.Getter;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Getter
public class CustomItemFilterRule implements FilterRule {
    private final long id;
    private final String displayName;
    private final List<String> loreContains;
    private final Integer customModelData;
    private final Map<String, String> nbtTags;
    private final transient JavaPlugin plugin;

    public CustomItemFilterRule(long id, JavaPlugin plugin, String displayName, List<String> lore, Integer customModelData, Map<String, String> nbtTags) {
        this.id = id;
        this.plugin = plugin;
        this.displayName = displayName;
        this.loreContains = lore;
        this.customModelData = customModelData;
        this.nbtTags = nbtTags;
    }

    @Override
    public long id() {
        return this.id;
    }

    @Override
    public boolean matches(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        assert meta != null;

        if (nbtTags != null && !nbtTags.isEmpty()) {
            PersistentDataContainer container = meta.getPersistentDataContainer();
            for (Map.Entry<String, String> entry : nbtTags.entrySet()) {
                NamespacedKey key = new NamespacedKey(plugin, entry.getKey());
                if (!container.has(key, PersistentDataType.STRING)) {
                    return false;
                }
                String value = container.get(key, PersistentDataType.STRING);
                if (!Objects.equals(entry.getValue(), value)) {
                    return false;
                }
            }
            return true;
        }

        if (customModelData != null) {
            if (!meta.hasCustomModelData() || meta.getCustomModelData() != this.customModelData) {
                return false;
            }
        }

        if (displayName != null) {
            if (!meta.hasDisplayName() || !meta.getDisplayName().equals(this.displayName)) {
                return false;
            }
        }

        if (loreContains != null && !loreContains.isEmpty()) {
            if (!meta.hasLore()) return false;
            List<String> itemLore = meta.getLore();
            if (itemLore == null) return false;
            for (String requiredLine : loreContains) {
                if (itemLore.stream().noneMatch(line -> line.contains(requiredLine))) {
                    return false;
                }
            }
        }

        return displayName != null || loreContains != null || customModelData != null;
    }

    @Override
    public String toString() {
        return String.format("ID: %d, Type: CUSTOM, Name: %s, Model: %s, NBT: %s", id, displayName, customModelData, nbtTags);
    }
}
