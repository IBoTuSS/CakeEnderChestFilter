package dev.cakestudio.cakeenderchestfilter.configuration;

import lombok.Getter;
import lombok.NonNull;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;

public class Config {
    @Getter
    private static FileConfiguration config;

    private static File configFile;

    public static void loadYaml(@NonNull Plugin plugin) {
        configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            plugin.saveResource("config.yml", true);
        }
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public static void reload(@NonNull Plugin plugin) {
        config = YamlConfiguration.loadConfiguration(configFile);
    }
}
