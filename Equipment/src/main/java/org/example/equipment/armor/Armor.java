package org.example.equipment.armor;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.example.cluster.object.Rarity;

import java.util.ArrayList;
import java.util.List;

public record Armor(ItemStack itemStack, String name, double armor, double durability, Object stats, Rarity rarity) {

    public void setupItemStack() {
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(name);
        List<String> lore = new ArrayList<>();
        lore.add(" ");
        lore.add(ChatColor.GRAY + "armor: ");
        lore.add(ChatColor.RED + " " + armor);
        lore.add(" ");
        lore.add("Durability: ");
        lore.add(ChatColor.GOLD + "" +durability);
        meta.setLore(lore);
        itemStack.setItemMeta(meta);
    }
}
