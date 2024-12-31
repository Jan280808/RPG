package de.jan.rpg.core.item.combat;

import de.jan.rpg.api.builder.ItemBuilder;
import de.jan.rpg.api.item.ItemRarity;
import de.jan.rpg.api.item.ItemType;
import de.jan.rpg.api.item.combat.Weapon;
import de.jan.rpg.api.item.ItemData;
import de.jan.rpg.core.Core;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

@Getter
public class CoreWeapon implements Weapon {

    private final String uuid;
    private final Component displayName;
    private final Material material;
    private final ItemType itemType;
    private final WeaponType weaponType;
    private final ItemRarity itemRarity;

    private int minDamage;
    private int maxDamage;

    public CoreWeapon(Component displayName, Material material, ItemType itemType, WeaponType weaponType, ItemRarity itemRarity, int minDamage, int maxDamage) {
        this.uuid = UUID.randomUUID().toString().replace("-", "");
        this.displayName = displayName;
        this.material = material;
        this.itemType = itemType;
        this.weaponType = weaponType;
        this.itemRarity = itemRarity;
        this.minDamage = minDamage;
        this.maxDamage = maxDamage;
    }

    @Override
    public String getUUID() {
        return uuid;
    }

    @Override
    public void setDamage(int value) {
        maxDamage = value;
    }

    @Override
    public int getDamage() {
        return maxDamage;
    }

    @Override
    public WeaponType getWeaponType() {
        return null;
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
        Core.LOGGER.info("uuid: " + uuid);
        return new ItemBuilder(material).setDisplayName(displayName)
                .setData(ItemData.ID, uuid)
                .setData(ItemData.MIN_DAMAGE, "" + minDamage)
                .setData(ItemData.MAX_DAMAGE, "" + maxDamage)
                .setData(ItemData.WEAPON_TYPE, weaponType.toString())
                .setData(ItemData.RARITY, itemRarity.toString())
                .build();
    }
}
