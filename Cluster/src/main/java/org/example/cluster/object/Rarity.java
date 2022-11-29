package org.example.cluster.object;

import org.bukkit.ChatColor;

public enum Rarity {
    COMMON("common", "", ChatColor.GRAY),
    RARE("rare", "", ChatColor.AQUA),
    EPIC("epic", "", ChatColor.DARK_PURPLE),
    EXOTIC("exotic", "", ChatColor.YELLOW),
    MASTER("master", "", ChatColor.RED);

    private final String name;
    private final String icon;
    private final ChatColor chatColor;

    Rarity(String name, String icon, ChatColor chatColor) {
        this.name = name;
        this.icon = icon;
        this.chatColor = chatColor;
    }

    public String getName() {
        return name;
    }

    public String getIcon() {
        return icon;
    }

    public ChatColor getChatColor() {
        return chatColor;
    }
}
