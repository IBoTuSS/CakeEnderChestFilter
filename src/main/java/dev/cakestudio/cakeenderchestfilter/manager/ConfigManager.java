package dev.cakestudio.cakeenderchestfilter.manager;

import dev.cakestudio.cakeenderchestfilter.model.Messages;
import dev.cakestudio.cakeenderchestfilter.model.StorageRule;
import lombok.Getter;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

@Getter
public class ConfigManager {

    private final JavaPlugin plugin;
    private Map<InventoryType, StorageRule> storageRules;
    private Messages messages;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        load();
    }

    public void reload() {
        plugin.reloadConfig();
        load();
    }

    private void load() {
        FileConfiguration config = plugin.getConfig();
        storageRules = loadStorageRules(config.getConfigurationSection("storages"));
        ConfigurationSection messagesSection = config.getConfigurationSection("messages");
        messages = new Messages(messagesSection);
    }

    private Map<InventoryType, StorageRule> loadStorageRules(ConfigurationSection storagesSection) {
        if (storagesSection == null) {
            return Collections.emptyMap();
        }

        Map<InventoryType, StorageRule> rules = new EnumMap<>(InventoryType.class);

        for (String key : storagesSection.getKeys(false)) {
            try {
                InventoryType type = InventoryType.valueOf(key.toUpperCase());
                ConfigurationSection storage = storagesSection.getConfigurationSection(key);
                if (storage == null) continue;

                Sound sound = Sound.BLOCK_NOTE_BLOCK_BASS;
                String soundName = storage.getString("sound");
                if (soundName != null && !soundName.isEmpty()) {
                    try {
                        sound = Sound.valueOf(soundName.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        plugin.getLogger().warning("Invalid sound '" + soundName + "' specified for storage: " + key);
                    }
                }
                rules.put(type, new StorageRule(sound));

            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Configured storage type '" + key + "' is not a valid InventoryType.");
            }
        }
        return Collections.unmodifiableMap(rules);
    }
}