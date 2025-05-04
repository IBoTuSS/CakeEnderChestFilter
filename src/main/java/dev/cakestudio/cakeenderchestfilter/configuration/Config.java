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

    public static void loadYaml(@NonNull Plugin plugin) {
        File file = new File(plugin.getDataFolder(), "config.yml");
        if (!file.exists()) {
            plugin.saveResource("config.yml", true);
        }

        config = YamlConfiguration.loadConfiguration(file);
    }
}
