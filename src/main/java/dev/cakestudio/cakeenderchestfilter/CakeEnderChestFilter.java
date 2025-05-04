package dev.cakestudio.cakeenderchestfilter;

import dev.cakestudio.cakeenderchestfilter.command.EnderChestCommand;
import dev.cakestudio.cakeenderchestfilter.configuration.Config;
import dev.cakestudio.cakeenderchestfilter.listener.InventoryClickListener;
import dev.cakestudio.cakeenderchestfilter.utils.HexColor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class CakeEnderChestFilter extends JavaPlugin {
    @Getter
    public static CakeEnderChestFilter instance;

    private void msg(String msg) {
        String prefix = "#CakeEnderChestFilter &7| ";
        Bukkit.getConsoleSender().sendMessage(HexColor.color(prefix + msg));
    }

    @Override
    public void onEnable() {
        instance = this;
        Config.loadYaml(this);
        Bukkit.getConsoleSender().sendMessage("");
        msg("&fDeveloper: &aCakeStudio");
        msg("&fVersion: &dv" + getDescription().getVersion());
        Bukkit.getConsoleSender().sendMessage("");
        getServer().getPluginManager().registerEvents(new InventoryClickListener(), this);
        getCommand("EnderChestFilter").setExecutor(new EnderChestCommand());
        getCommand("EnderChestFilter").setTabCompleter(new EnderChestCommand());

    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage("");
        msg("&fDisable plugin.");
        Bukkit.getConsoleSender().sendMessage("");
    }
}
