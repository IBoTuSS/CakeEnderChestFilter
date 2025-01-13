package dev.cakestudio.cakeenderchestfilter.utils;

import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

public class HexColor {

    private static final Pattern HEX_COLOR_PATTERN = Pattern.compile("#[a-fA-F0-9]{6}");

    public static @NotNull String color(@NotNull String input) {
        String text = HEX_COLOR_PATTERN
                .matcher(input)
                .replaceAll(match -> ChatColor.of(match.group()).toString());
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
