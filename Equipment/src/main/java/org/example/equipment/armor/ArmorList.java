package org.example.equipment.armor;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.example.cluster.builder.ItemBuilder;
import org.example.cluster.object.Rarity;

public enum ArmorList {
    LEATHER_HELM(new ItemBuilder(Material.LEATHER_HELMET).build(), "Chest", 8, 10, null, Rarity.COMMON),
    LEATHER_CHESTPLATE(new ItemBuilder(Material.LEATHER_CHESTPLATE).build(), "Helm", 10, 5, null, Rarity.COMMON),
    LEATHER_LEGGINGS(new ItemBuilder(Material.LEATHER_LEGGINGS).build(), "Leggings", 4, 20, null, Rarity.COMMON),
    LEATHER_BOOTS(new ItemBuilder(Material.LEATHER_BOOTS).build(), "Helm", 10, 5, null, Rarity.COMMON);

    private final ItemStack itemStack;
    private final String name;
    private final double armor;
    private final double durability;
    private final Object stats;
    private final Rarity rarity;

    ArmorList(ItemStack itemStack, String name, double armor, double durability, Object stats, Rarity rarity) {
        this.itemStack = itemStack;
        this.name = name;
        this.armor = armor;
        this.durability = durability;
        this.stats = stats;
        this.rarity = rarity;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public String getName() {
        return name;
    }

    public double getArmor() {
        return armor;
    }

    public double getDurability() {
        return durability;
    }

    public Object getStats() {
        return stats;
    }

    public Rarity getRarity() {
        return rarity;
    }
}
