package dev.cakestudio.cakeenderchestfilter.command;

import dev.cakestudio.cakeenderchestfilter.CakeEnderChestFilter;
import dev.cakestudio.cakeenderchestfilter.configuration.Config;
import dev.cakestudio.cakeenderchestfilter.utils.HexColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class EnderChestCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (args.length == 0) {
            sender.sendMessage(HexColor.color(Config.getConfig().getString("messages.usage")));
            return true;
        }
        if(sender instanceof Player player) {
            switch (args[0].toLowerCase()) {
                case "reload":
                    Config.loadYaml(CakeEnderChestFilter.instance);
                    player.sendMessage(HexColor.color(Config.getConfig().getString("messages.reload")));
                    break;
                case "menu":
                    //Soon...
                    break;

                default:
                    player.sendMessage(HexColor.color(Config.getConfig().getString("messages.unknown")));
                    break;
            }
        }

        return true;
    }
}
