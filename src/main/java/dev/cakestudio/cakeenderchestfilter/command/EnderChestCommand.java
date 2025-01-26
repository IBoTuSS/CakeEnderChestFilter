package dev.cakestudio.cakeenderchestfilter.command;

import dev.cakestudio.cakeenderchestfilter.CakeEnderChestFilter;
import dev.cakestudio.cakeenderchestfilter.configuration.Config;
import dev.cakestudio.cakeenderchestfilter.utils.HexColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class EnderChestCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (args.length == 0) {
            sender.sendMessage(HexColor.color(Config.getConfig().getString("messages.usage")));
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload":
                if (!sender.hasPermission("cakeenderchestfilter.reload")) {
                    sender.sendMessage(HexColor.color(Config.getConfig().getString("messages.no-permission")));
                    return true;
                }
                Config.loadYaml(CakeEnderChestFilter.instance);
                sender.sendMessage(HexColor.color(Config.getConfig().getString("messages.reload")));
                break;

            default:
                sender.sendMessage(HexColor.color(Config.getConfig().getString("messages.unknown")));
                break;
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.add("reload");
        }

        return completions;
    }
}
