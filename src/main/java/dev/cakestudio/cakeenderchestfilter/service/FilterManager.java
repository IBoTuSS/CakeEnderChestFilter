package dev.cakestudio.cakeenderchestfilter.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dev.cakestudio.cakeenderchestfilter.database.FilterRepository;
import dev.cakestudio.cakeenderchestfilter.manager.ConfigManager;
import dev.cakestudio.cakeenderchestfilter.model.FilterRule;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FilterManager {

    private final FilterRepository repository;
    private final JavaPlugin plugin;
    private final Map<InventoryType, List<FilterRule>> filterCache;
    private final Gson gson = new Gson();

    public FilterManager(FilterRepository repository, @NonNull ConfigManager configManager, JavaPlugin plugin) {
        this.repository = repository;
        this.plugin = plugin;
        this.filterCache = new EnumMap<>(InventoryType.class);
        configManager.getStorageRules().keySet().forEach(this::reloadFiltersFor);
    }

    public void reloadFiltersFor(InventoryType type) {
        filterCache.put(type, repository.loadFiltersFor(type));
    }

    public boolean isFiltered(InventoryType type, ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return false;
        List<FilterRule> rules = filterCache.get(type);
        if (rules == null || rules.isEmpty()) return false;

        for (FilterRule rule : rules) {
            if (rule.matches(item)) {
                return true;
            }
        }
        return false;
    }

    public String toggleFilter(@NonNull InventoryType type, @NonNull ItemStack item) {
        for (FilterRule rule : filterCache.getOrDefault(type, List.of())) {
            if (rule.matches(item)) {
                repository.removeRuleById(rule.id());
                reloadFiltersFor(type);
                return "REMOVED";
            }
        }

        ItemMeta meta = item.getItemMeta();

        JsonObject criteria = new JsonObject();
        String filterType = "MATERIAL";

        if (meta != null) {
            PersistentDataContainer container = meta.getPersistentDataContainer();
            NamespacedKey uniqueKey = new NamespacedKey(plugin, "unique_id");

            if (container.has(uniqueKey, PersistentDataType.STRING)) {
                String uniqueValue = container.get(uniqueKey, PersistentDataType.STRING);
                Map<String, String> nbtMap = new HashMap<>();
                nbtMap.put("unique_id", uniqueValue);
                criteria.add("nbt", gson.toJsonTree(nbtMap));
                filterType = "CUSTOM";
            } else if (meta.hasDisplayName() || meta.hasLore() || meta.hasCustomModelData()) {
                if (meta.hasDisplayName()) criteria.addProperty("name", meta.getDisplayName());
                if (meta.hasLore()) criteria.add("lore", gson.toJsonTree(meta.getLore()));
                if (meta.hasCustomModelData()) criteria.addProperty("model", meta.getCustomModelData());
                filterType = "CUSTOM";
            }
        }

        String criteriaString = filterType.equals("CUSTOM") ? criteria.toString() : item.getType().name();
        repository.addRule(type, filterType, criteriaString);
        reloadFiltersFor(type);

        return filterType.equals("CUSTOM") ? "CUSTOM_ADDED" : "MATERIAL_ADDED";
    }
}
