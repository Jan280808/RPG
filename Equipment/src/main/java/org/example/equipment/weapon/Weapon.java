package org.example.equipment.weapon;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.example.cluster.object.Rarity;
import org.example.cluster.object.WeaponType;

import java.util.ArrayList;
import java.util.List;

public record Weapon(ItemStack itemStack, String name, String loreLine1, String loreLine2, double damage, double critical, Rarity rarity, WeaponType type) {
    public void setupItemStack() {
        ItemMeta meta = itemStack.getItemMeta();
        List<String> lore = new ArrayList<>();
        lore.add(loreLine1);

        if(loreLine2 != null)
            lore.add(loreLine2);

        lore.add(" ");
        lore.add(ChatColor.GRAY + "Damage: ");
        lore.add(ChatColor.RED + " " + damage);
        lore.add(" ");
        lore.add("DamageType: ");
        lore.add(type.name() + " " + type.getIcon());
        meta.setLore(lore);
        itemStack.setItemMeta(meta);
    }
}
