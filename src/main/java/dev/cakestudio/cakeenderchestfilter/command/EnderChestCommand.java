package dev.cakestudio.cakeenderchestfilter.command;

import dev.cakestudio.cakeenderchestfilter.manager.ConfigManager;
import dev.cakestudio.cakeenderchestfilter.model.Messages;
import dev.cakestudio.cakeenderchestfilter.service.FilterManager;
import dev.cakestudio.cakeenderchestfilter.util.HexColor;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.stream.Collectors;

public class EnderChestCommand implements TabExecutor {

    private final ConfigManager configManager;
    private final FilterManager filterManager;

    public EnderChestCommand(ConfigManager configManager, FilterManager filterManager) {
        this.configManager = configManager;
        this.filterManager = filterManager;
    }

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, String[] args) {
        Messages messages = configManager.getMessages();

        if (!(sender instanceof Player player)) {
            sender.sendMessage(HexColor.color(messages.getMustBePlayer()));
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            if (!player.hasPermission("cakeenderchestfilter.admin")) {
                player.sendMessage(HexColor.color(messages.getNoPermission()));
                return true;
            }
            configManager.reload();
            player.sendMessage(HexColor.color(messages.getReload()));
            return true;
        }

        if (args.length != 1) {
            player.sendMessage(HexColor.color(messages.getUsage()));
            return true;
        }

        if (!player.hasPermission("cakeenderchestfilter.admin")) {
            player.sendMessage(HexColor.color(messages.getNoPermission()));
            return true;
        }

        InventoryType type;
        try {
            type = InventoryType.valueOf(args[0].toUpperCase());
            if (!configManager.getStorageRules().containsKey(type)) {
                throw new IllegalArgumentException();
            }
        } catch (IllegalArgumentException e) {
            String message = messages.getUnknownStorage().replace("%storage%", args[0]);
            player.sendMessage(HexColor.color(message));
            return true;
        }

        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        if (itemInHand.getType() == Material.AIR) {
            player.sendMessage(HexColor.color(messages.getMustHoldItem()));
            return true;
        }

        String result = filterManager.toggleFilter(type, itemInHand);
        String itemName = itemInHand.hasItemMeta() && itemInHand.getItemMeta().hasDisplayName()
                ? itemInHand.getItemMeta().getDisplayName()
                : itemInHand.getType().name();

        String messageTemplate = switch (result) {
            case "REMOVED" -> messages.getItemRemovedFromFilter();
            case "CUSTOM_ADDED", "MATERIAL_ADDED" -> messages.getItemAddedToFilter();
            default -> "&cПроизошла неизвестная ошибка.";
        };

        String formattedMessage = messageTemplate
                .replace("%item%", itemName)
                .replace("%storage%", type.name());

        player.sendMessage(HexColor.color(formattedMessage));
        return true;
    }

    @Override
    public List<String> onTabComplete(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, String @NonNull [] args) {
        if (args.length == 1) {
            List<String> completions = configManager.getStorageRules().keySet().stream()
                    .map(Enum::name)
                    .collect(Collectors.toList());
            if (sender.hasPermission("cakeenderchestfilter.admin")) {
                completions.add("reload");
            }
            return completions.stream()
                    .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return null;
    }
}