package org.example.equipment.weapon;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.example.cluster.builder.ItemBuilder;
import org.example.cluster.object.Rarity;
import org.example.cluster.object.WeaponType;

public enum WeaponList {
    COMMON_MELEE_STICK(new ItemBuilder(Material.STICK).setDisplayName("Hello there").build(), "Hello there", "loreLine1", "loreLine2", 1.00, 0.01, Rarity.COMMON, WeaponType.MELEE),
    COMMON_SWORD_WOODEN(new ItemBuilder(Material.WOODEN_SWORD).setDisplayName("Hello").build(), "Hello", "loreLine1", "loreLine2", 2.00, 0.1, Rarity.COMMON, WeaponType.MELEE),
    LEGENDARY_SWORD_GOLD(new ItemBuilder(Material.GOLDEN_SWORD).setDisplayName(ChatColor.GOLD + "EXOTIC").build(), ChatColor.GOLD + "EXOTIC", "loreLine1", "loreLine2", 20.00, 0.4, Rarity.EXOTIC, WeaponType.MELEE);

    private final ItemStack itemStack;
    private final String name;
    private final String loreLine1;
    private final String loreLine2;
    private final double damage;
    private final double critical;
    private final Rarity rarity;
    private final WeaponType type;

    WeaponList(ItemStack itemStack, String name, String loreLine1, String loreLine2, double damage, double critical, Rarity rarity, WeaponType type) {
        this.itemStack = itemStack;
        this.name = name;
        this.loreLine1 = loreLine1;
        this.loreLine2 = loreLine2;
        this.damage = damage;
        this.critical = critical;
        this.rarity = rarity;
        this.type = type;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public String getName() {
        return name;
    }

    public String getLoreLine1() {
        return loreLine1;
    }

    public String getLoreLine2() {
        return loreLine2;
    }

    public double getDamage() {
        return damage;
    }

    public double getCritical() {
        return critical;
    }

    public Rarity getRarity() {
        return rarity;
    }

    public WeaponType getType() {
        return type;
    }
}
