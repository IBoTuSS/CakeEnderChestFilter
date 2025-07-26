package dev.cakestudio.cakeenderchestfilter;

import dev.cakestudio.cakeenderchestfilter.command.EnderChestCommand;
import dev.cakestudio.cakeenderchestfilter.database.DatabaseManager;
import dev.cakestudio.cakeenderchestfilter.database.FilterRepository;
import dev.cakestudio.cakeenderchestfilter.listener.PlayerInventoryListener;
import dev.cakestudio.cakeenderchestfilter.manager.ConfigManager;
import dev.cakestudio.cakeenderchestfilter.service.FilterManager;
import dev.cakestudio.cakeenderchestfilter.util.HexColor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class CakeEnderChestFilter extends JavaPlugin {

    @Getter
    private ConfigManager configManager;
    @Getter
    private DatabaseManager databaseManager;

    private void msg(String msg) {
        String prefix = "#C102FACakeEnderChestFilter &7| ";
        Bukkit.getConsoleSender().sendMessage(HexColor.color(prefix + msg));
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        configManager = new ConfigManager(this);

        databaseManager = new DatabaseManager(this);
        FilterRepository filterRepository = new FilterRepository(databaseManager, this);

        FilterManager filterManager = new FilterManager(filterRepository, configManager, this);

        getServer().getPluginManager().registerEvents(new PlayerInventoryListener(filterManager, configManager), this);

        getCommand("EnderChestFilter").setExecutor(new EnderChestCommand(configManager, filterManager));
        getCommand("EnderChestFilter").setTabCompleter(new EnderChestCommand(configManager, filterManager));

        Bukkit.getConsoleSender().sendMessage("");
        msg("&fDeveloper: #C102FACakeStudio");
        msg("&fVersion: #C102FAv" + getDescription().getVersion());
        Bukkit.getConsoleSender().sendMessage("");
    }

    @Override
    public void onDisable() {
        if (databaseManager != null) {
            databaseManager.close();
        }
        Bukkit.getConsoleSender().sendMessage("");
        msg("&fDisable plugin.");
        Bukkit.getConsoleSender().sendMessage("");
    }
}