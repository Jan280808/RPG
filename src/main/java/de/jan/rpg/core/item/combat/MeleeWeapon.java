package de.jan.rpg.core.item.combat;

import de.jan.rpg.api.builder.ItemBuilder;
import de.jan.rpg.api.item.ItemData;
import de.jan.rpg.api.item.ItemRarity;
import de.jan.rpg.api.item.ItemType;
import de.jan.rpg.api.item.combat.Melee;
import de.jan.rpg.api.item.combat.Status;
import de.jan.rpg.api.item.combat.Weapon;
import de.jan.rpg.api.item.combat.data.WeaponData;
import de.jan.rpg.api.item.combat.data.WeaponType;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ThreadLocalRandom;

@Getter
@Setter
public class MeleeWeapon implements Melee {

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
    private Cooldown cooldown;
    private int level;
    private int xp;

    private boolean criticalHit = false;

    public MeleeWeapon(int id, String translationKey, Material material, ItemType itemType, WeaponType weaponType, Status.Type statusType, ItemRarity itemRarity, int minDamage, int maxDamage, double criticalChance, Cooldown cooldown, int level, int xp) {
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
        this.cooldown = cooldown;
        this.level = level;
        this.xp = xp;
    }

    public MeleeWeapon(Weapon weapon, Cooldown cooldown) {
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
        this.cooldown = cooldown;
        this.level = weapon.getLevel();
        this.xp = weapon.getXp();
    }


    @Override
    public int getRandomDamage() {
        return ThreadLocalRandom.current().nextInt(minDamage, maxDamage + 1);
    }

    @Override
    public Object getAbility() {
        return null;
    }

    public double getCooldown() {
        return cooldown.getValue();
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
                .setData(Data.COOLDOWN, "" + cooldown)

                .setAttackSpeed(cooldown.getValue())

                .setLore("id: " + id,
                        "minDamage: " + minDamage,
                        "maxDamage: " + maxDamage,
                        "criticalChance: " + criticalChance,
                        "WeaponType: " + weaponType,
                        "StatusDamageType: " + statusType.getIcon(),
                        "itemRarity: " + itemRarity,
                        "xp: " + xp,
                        "level: " + level,
                        "cooldown: " + cooldown)

                //.hideAllFlags()
                .build();
    }
}
