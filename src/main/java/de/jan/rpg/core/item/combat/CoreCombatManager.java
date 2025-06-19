package de.jan.rpg.core.item.combat;

import de.jan.rpg.api.item.ItemData;
import de.jan.rpg.api.item.ItemRarity;
import de.jan.rpg.api.item.ItemType;
import de.jan.rpg.api.item.combat.*;
import de.jan.rpg.api.item.combat.data.WeaponData;
import de.jan.rpg.api.item.combat.data.WeaponType;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class CoreCombatManager implements CombatManager {

    private final Map<WeaponType, Map<Integer, Weapon>> weaponMap;

    public CoreCombatManager() {
        this.weaponMap =  new HashMap<>();
        loadWeapons();
    }

    private void loadWeapons() {
        //create all weaponMaps
        Arrays.stream(WeaponType.values()).forEach(weaponType -> weaponMap.put(weaponType, new HashMap<>()));

        //loadAllWeaponDummy
    }

    @Override
    public Melee getMeleeWeapon(@NotNull ItemStack itemStack) {
        Weapon weapon = getWeapon(itemStack);
        if(weapon == null) return null;
        Melee.Cooldown cooldown = Melee.Cooldown.valueOf(getData(itemStack, Melee.Data.COOLDOWN));
        return new MeleeWeapon(weapon, cooldown);
    }

    @Override
    public Melee createMeleeWeapon(int id, @NotNull String displayKey, @NotNull Material material, Status.@NotNull Type statusType, @NotNull ItemRarity itemRarity, int minDamage, int maxDamage, double criticalChance, int level, int xp, Melee.Cooldown cooldown) {
        return new MeleeWeapon(id, displayKey, material, ItemType.WEAPON, WeaponType.MELEE, statusType, itemRarity, minDamage, maxDamage, criticalChance, cooldown, level, xp);
    }

    @Override
    public Range getRangeWeapon(@NotNull ItemStack itemStack) {
        Weapon weapon = getWeapon(itemStack);
        if(weapon == null) return null;
        int projectileSpeed = Integer.parseInt(getData(itemStack, Range.Data.PROJECTILE_SPEED));
        int projectileRange = Integer.parseInt(getData(itemStack, Range.Data.PROJECTILE_RANGE));
        return new RangeWeapon(weapon, projectileSpeed, projectileRange);
    }

    @Override
    public Range createRangeWeapon(int id, @NotNull String displayKey, @NotNull Material material, Status.@NotNull Type statusType, @NotNull ItemRarity itemRarity, int minDamage, int maxDamage, double criticalChance, int level, int xp, int projectileSpeed, int projectileRange) {
        return new RangeWeapon(id, displayKey, material, ItemType.WEAPON, WeaponType.RANGE, statusType, itemRarity, minDamage, maxDamage, criticalChance, level, xp, projectileSpeed, projectileRange);
    }

    public Weapon getWeapon(ItemStack itemStack) {
        try {
            int id = Integer.parseInt(getData(itemStack, ItemData.ID));
            ItemRarity itemRarity = ItemRarity.valueOf(getData(itemStack, ItemData.RARITY).toUpperCase());
            ItemType itemType = ItemType.valueOf(getData(itemStack, ItemData.ITEM_TYPE).toUpperCase());
            String translationKey = getData(itemStack, ItemData.TRANSLATION_KEY);

            int minDamage = Integer.parseInt(getData(itemStack, WeaponData.MIN_DAMAGE));
            int maxDamage = Integer.parseInt(getData(itemStack, WeaponData.MAX_DAMAGE));
            double criticalChance = Double.parseDouble(getData(itemStack, WeaponData.CRITICAL_CHANCE));
            WeaponType weaponType = WeaponType.valueOf(getData(itemStack, WeaponData.WEAPON_TYPE).toUpperCase());
            Status.Type statusDamageType = Status.Type.valueOf(getData(itemStack, WeaponData.STATUS_DAMAGE).toUpperCase());
            int level = Integer.parseInt(getData(itemStack, WeaponData.LEVEL));
            int xp = Integer.parseInt(getData(itemStack, WeaponData.XP));

            return new Weapon() {
                @Override
                public int getMinDamage() {
                    return minDamage;
                }

                @Override
                public void setMinDamage(int value) {
                }

                @Override
                public int getMaxDamage() {
                    return maxDamage;
                }

                @Override
                public void setMaxDamage(int value) {

                }

                @Override
                public int getRandomDamage() {
                    return ThreadLocalRandom.current().nextInt(getMinDamage(), getMaxDamage() + 1);
                }

                @Override
                public double getCriticalChance() {
                    return criticalChance;
                }

                @Override
                public void setCriticalChance(double value) {

                }

                @Override
                public WeaponType getWeaponType() {
                    return weaponType;
                }

                @Override
                public Status.Type getStatusType() {
                    return statusDamageType;
                }

                @Override
                public Object getAbility() {
                    return null;
                }

                @Override
                public int getLevel() {
                    return level;
                }

                @Override
                public int getXp() {
                    return xp;
                }

                @Override
                public void setXp(int xp) {

                }

                @Override
                public int getId() {
                    return id;
                }

                @Override
                public String getTranslationKey() {
                    return translationKey;
                }

                @Override
                public Material getMaterial() {
                    return itemStack.getType();
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
                    return itemStack;
                }
            };
        } catch (Exception ignored) {
            return null;
        }
    }

    private String getData(@NotNull ItemStack itemStack, Enum data) {
        if(!itemStack.hasItemMeta()) return "null";
        PersistentDataContainer dataContainer = itemStack.getItemMeta().getPersistentDataContainer();
        String value = dataContainer.get(new NamespacedKey("core", data.name().toLowerCase()), PersistentDataType.STRING);
        if(value == null) return "null";
        return value;
    }
}
