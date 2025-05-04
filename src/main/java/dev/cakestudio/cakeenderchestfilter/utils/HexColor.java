package dev.cakestudio.cakeenderchestfilter.utils;

import lombok.NonNull;
import net.md_5.bungee.api.ChatColor;

import java.util.regex.Pattern;

public class HexColor {

    private static final Pattern HEX_COLOR_PATTERN = Pattern.compile("#[a-fA-F0-9]{6}");

    public static @NonNull String color(@NonNull String input) {
        return ChatColor.translateAlternateColorCodes('&',
                HEX_COLOR_PATTERN.matcher(input)
                        .replaceAll(match -> ChatColor.of(match.group()).toString()));
    }
}
