package de.jan.rpg.core.item.combat;

import de.jan.rpg.api.builder.ItemBuilder;
import de.jan.rpg.api.event.RPGWeaponLevelUpEvent;
import de.jan.rpg.api.item.ItemRarity;
import de.jan.rpg.api.item.ItemType;
import de.jan.rpg.api.item.combat.Status;
import de.jan.rpg.api.item.combat.Weapon;
import de.jan.rpg.api.item.ItemData;
import de.jan.rpg.api.item.combat.WeaponType;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

@Getter
public class CoreWeapon implements Weapon {

    private final int id;
    private final Component displayName;
    private final Material material;
    private final ItemType itemType;
    private final WeaponType weaponType;
    private final Status.Type statusType;
    private final ItemRarity itemRarity;

    private final int minDamage;
    private final int maxDamage;
    private final double criticalChance;
    private boolean criticalHit = false;

    private int level;
    private int xp;

    public CoreWeapon(int id, Component displayName, Material material, WeaponType weaponType, Status.Type statusType, ItemRarity itemRarity, int minDamage, int maxDamage, double criticalChance, int level, int xp) {
        this.id = id;
        this.displayName = displayName;
        this.material = material;
        this.itemType = ItemType.WEAPON;
        this.weaponType = weaponType;
        this.statusType = statusType;
        this.itemRarity = itemRarity;
        this.minDamage = minDamage;
        this.maxDamage = maxDamage;
        this.criticalChance = criticalChance;
        this.level = level;
        this.xp = xp;
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public int getDamage() {
        Random random = new Random();
        int damageValue = random.nextInt(maxDamage - minDamage + 1) + minDamage;
        if(random.nextDouble() < criticalChance) {
            damageValue += damageValue;
            criticalHit = true;
            return damageValue;
        }
        criticalHit = false;
        return damageValue;
    }

    @Override
    public WeaponType getType() {
        return weaponType;
    }

    @Override
    public Status.Type getStatusType() {
        return statusType;
    }

    @Override
    public boolean isCriticalHit() {
        return criticalHit;
    }

    @Override
    public int getLevel() {
        return level;
    }

    public void levelUP() {
        level += 1;
        Bukkit.getPluginManager().callEvent(new RPGWeaponLevelUpEvent(this, getLevel()));
    }

    @Override
    public int getXP() {
        return xp;
    }

    @Override
    public void addXP(int value) {
        xp = xp + value;
    }

    @Override
    public Component displayName() {
        return displayName;
    }

    @Override
    public Material getMaterial() {
        return material;
    }

    @Override
    public ItemType getItemType() {
        return itemType;
    }

    @Override
    public ItemRarity getItemRarity() {
        return itemRarity;
    }

    @Override
    public ItemStack getItemStack() {
        return new ItemBuilder(material).setDisplayName(displayName)
                .setData(ItemData.ID, "" + id)
                .setData(ItemData.MIN_DAMAGE, "" + minDamage)
                .setData(ItemData.MAX_DAMAGE, "" + maxDamage)
                .setData(ItemData.WEAPON_TYPE, weaponType.toString())
                .setData(ItemData.STATUS_DAMAGE_TYPE, statusType.toString())
                .setData(ItemData.RARITY, itemRarity.toString())
                .setData(ItemData.CRITICAL, "" + criticalChance).setLore("id: " + id, "minDamage: " + minDamage, "maxDamage: " + maxDamage, "criticalChance: " + criticalChance, "WeaponType: " + weaponType, "StatusDamageType: " + statusType.getIcon() ,"itemRarity: " + itemRarity)
                .build();
    }
}
