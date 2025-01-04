package de.jan.rpg.core.item.combat;

import de.jan.rpg.api.builder.ItemBuilder;
import de.jan.rpg.api.item.ItemData;
import de.jan.rpg.api.item.ItemRarity;
import de.jan.rpg.api.item.ItemType;
import de.jan.rpg.api.item.combat.Armor;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class CoreArmor implements Armor {

    private final int id;
    private final Component displayName;
    private final Material material;
    private final ItemType itemType;
    private final ItemRarity itemRarity;
    private final int armorValue;
    private final int extraLife;

    public CoreArmor(int id, Component displayName, Material material, ItemRarity rarity, int armorValue, int extraLife) {
        this.id = id;
        this.displayName = displayName;
        this.material = material;
        this.itemType = ItemType.ARMOR;
        this.itemRarity = rarity;
        this.armorValue = armorValue;
        this.extraLife = extraLife;
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public int getArmorValue() {
        return armorValue;
    }

    @Override
    public int getExtraLife() {
        return extraLife;
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
                .setData(ItemData.ARMOR, "" + armorValue)
                .setData(ItemData.EXTRA_LIFE, "" + extraLife)
                .setData(ItemData.RARITY, "" + itemRarity)
                .setLore("id: " + id, "armorValue:: " + armorValue, "extraLife: " + extraLife, "itemRarity: " + itemRarity, "itemType: " + itemType)
                .build();
    }
}
