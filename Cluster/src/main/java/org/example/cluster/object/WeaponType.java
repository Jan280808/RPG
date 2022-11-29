package org.example.cluster.object;

import org.bukkit.ChatColor;

public enum WeaponType {
    MELEE(0, ChatColor.BLUE + "melee", "\uD83D\uDDE1"),
    ARCHER(1, ChatColor.GREEN + "archer", "➹");

    private final int ID;
    private final String type;
    private final String icon;

    WeaponType(int id, String type, String icon) {
        ID = id;
        this.type = type;
        this.icon = icon;
    }

    public int getID() {
        return ID;
    }

    public String getType() {
        return type;
    }

    public String getIcon() {
        return icon;
    }
}
