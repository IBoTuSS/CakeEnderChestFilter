package dev.cakestudio.cakeenderchestfilter.command;

import dev.cakestudio.cakeenderchestfilter.CakeEnderChestFilter;
import dev.cakestudio.cakeenderchestfilter.configuration.Config;
import dev.cakestudio.cakeenderchestfilter.utils.HexColor;
import lombok.NonNull;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EnderChestCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, @NonNull String @NonNull [] args) {
        if (args.length == 0) {
            sender.sendMessage(HexColor.color(Config.getConfig().getString("messages.usage")));
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("cakeenderchestfilter.reload")) {
                sender.sendMessage(HexColor.color(Config.getConfig().getString("messages.no-permission")));
                return true;
            }
            Config.reload(CakeEnderChestFilter.instance);
            sender.sendMessage(HexColor.color(Config.getConfig().getString("messages.reload")));
        } else {
            sender.sendMessage(HexColor.color(Config.getConfig().getString("messages.unknown")));
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, @NonNull String @NonNull [] args) {
        if (args.length == 1) return List.of("reload");
        return null;
    }
}
