package de.jan.rpg.core.item.combat;

import de.jan.rpg.api.builder.ItemBuilder;
import de.jan.rpg.api.item.ItemData;
import de.jan.rpg.api.item.ItemRarity;
import de.jan.rpg.api.item.ItemType;
import de.jan.rpg.api.item.combat.Range;
import de.jan.rpg.api.item.combat.Status;
import de.jan.rpg.api.item.combat.Weapon;
import de.jan.rpg.api.item.combat.data.WeaponData;
import de.jan.rpg.api.item.combat.data.WeaponType;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.concurrent.ThreadLocalRandom;

@Getter
@Setter
public class RangeWeapon implements Range {

    private final int id;
    private final String translationKey;
    private final Material material;
    private final ItemType itemType;
    private final WeaponType weaponType;
    private final Status.Type statusType;
    private final ItemRarity itemRarity;

    private int minDamage;
    private int maxDamage;
    private double criticalChance;
    private int level;
    private int xp;

    private int projectileSpeed;
    private int projectileRange;

    private boolean criticalHit = false;
    
    public RangeWeapon(int id, String translationKey, Material material, ItemType itemType, WeaponType weaponType, Status.Type statusType, ItemRarity itemRarity, int minDamage, int maxDamage, double criticalChance, int level, int xp, int projectileSpeed, int projectileRange) {
        this.id = id;
        this.translationKey = translationKey;
        this.material = material;
        this.itemType = itemType;
        this.weaponType = weaponType;
        this.statusType = statusType;
        this.itemRarity = itemRarity;
        this.minDamage = minDamage;
        this.maxDamage = maxDamage;
        this.criticalChance = criticalChance;
        this.level = level;
        this.xp = xp;
        this.projectileSpeed = projectileSpeed;
        this.projectileRange = projectileRange;
    }

    public RangeWeapon(Weapon weapon, int projectileSpeed, int projectileRange) {
        this.id = weapon.getId();
        this.translationKey = weapon.getTranslationKey();
        this.material = weapon.getMaterial();
        this.itemType = weapon.getItemType();
        this.weaponType = weapon.getWeaponType();
        this.statusType = weapon.getStatusType();
        this.itemRarity = weapon.getItemRarity();
        this.minDamage = weapon.getMinDamage();
        this.maxDamage = weapon.getMaxDamage();
        this.criticalChance = weapon.getCriticalChance();
        this.level = weapon.getLevel();
        this.xp = weapon.getXp();
        this.projectileSpeed = projectileSpeed;
        this.projectileRange = projectileRange;
    }

    public void shootProjectile(Player shooter) {
        Location spawnLocation = shooter.getLocation().add(0, 1.5, 0);
        Vector direction = shooter.getLocation().getDirection();

        Arrow arrow = spawnLocation.getWorld().spawn(spawnLocation, Arrow.class);
        arrow.setVelocity(direction.multiply(projectileSpeed));
        arrow.setShooter(shooter);
    }

    @Override
    public int getRandomDamage() {
        return ThreadLocalRandom.current().nextInt(minDamage, maxDamage + 1);
    }

    @Override
    public Object getAbility() {
        return null;
    }

    @Override
    public ItemStack getItemStack() {
        return new ItemBuilder(material).setDisplayName("key:" + translationKey)
                .setData(ItemData.ID, "" + id)
                .setData(ItemData.RARITY, "" + itemRarity)
                .setData(ItemData.ITEM_TYPE, "" + itemType)
                .setData(ItemData.TRANSLATION_KEY, translationKey)

                .setData(WeaponData.MIN_DAMAGE, "" + minDamage)
                .setData(WeaponData.MAX_DAMAGE, "" + maxDamage)
                .setData(WeaponData.CRITICAL_CHANCE, "" + criticalChance)
                .setData(WeaponData.WEAPON_TYPE, "" + weaponType)
                .setData(WeaponData.STATUS_DAMAGE, "" + statusType)
                .setData(WeaponData.LEVEL, "" + level)
                .setData(WeaponData.XP, "" + xp)
                .setData(Data.PROJECTILE_RANGE, "" + projectileRange)
                .setData(Data.PROJECTILE_SPEED, "" + projectileSpeed)

                .setLore("id: " + id,
                        "minDamage: " + minDamage,
                        "maxDamage: " + maxDamage,
                        "criticalChance: " + criticalChance,
                        "WeaponType: " + weaponType,
                        "StatusDamageType: " + statusType.getIcon(),
                        "itemRarity: " + itemRarity,
                        "xp: " + xp,
                        "level: " + level,
                        "projectileRange: " + projectileRange,
                        "projectileSpeed: " + projectileSpeed)

                //.hideAllFlags()
                .build();
    }
}
