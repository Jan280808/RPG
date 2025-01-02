package de.jan.rpg.core.item;

import de.jan.rpg.api.component.ComponentSerializer;
import de.jan.rpg.api.item.ItemData;
import de.jan.rpg.api.item.ItemManager;
import de.jan.rpg.api.item.ItemRarity;
import de.jan.rpg.api.item.ItemType;
import de.jan.rpg.api.item.combat.DamageType;
import de.jan.rpg.api.item.combat.Weapon;
import de.jan.rpg.api.item.combat.WeaponType;
import de.jan.rpg.core.APIImpl;
import de.jan.rpg.core.Core;
import de.jan.rpg.core.database.CoreDataBase;
import de.jan.rpg.core.item.combat.CoreWeapon;
import lombok.Synchronized;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.*;

public class CoreItemManager implements ItemManager {

    private final CoreDataBase dataBase;

    public CoreItemManager(APIImpl api) {
        this.dataBase = api.getCoreDataBase();
        dataBase.createTable("inventory", "uuid VARCHAR(100), inventory LONGTEXT");
    }

    public void convertItem(Player player) {
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        if(itemStack.getType().isAir()) return;
        CoreWeapon coreWeapon = new CoreWeapon(1, ComponentSerializer.deserialize("bow"), itemStack.getType(), ItemType.WEAPON, WeaponType.RANGE, DamageType.FIRE, ItemRarity.LEGENDARY, 1, 3, 0.3);
        player.getInventory().setItemInMainHand(coreWeapon.getItemStack());
    }

    public void giveItem(Player player) {
        CoreWeapon coreWeapon = new CoreWeapon(1, ComponentSerializer.deserialize("waffe"), Material.POTATO, ItemType.WEAPON, WeaponType.MELEE, DamageType.FROST, ItemRarity.LEGENDARY, 1, 3, 0.3);
        ItemStack itemStack = coreWeapon.getItemStack();
        player.getInventory().addItem(itemStack);
    }

    public void loadInventory(Player player) {
        Map<String, Object> result = dataBase.selectDataFromUUID("inventory", "inventory", player.getUniqueId());
        if(result == null) throw new NullPointerException("no data found");
        String string = (String) result.get("inventory");
        try {
            player.getInventory().setContents(deserialization(string));
        } catch (Exception exception) {
            Core.LOGGER.error("could not compile string to inventory", exception);
        }
    }

    public void savePlayerInventory(Player player) {
        String uuid = player.getUniqueId().toString();
        dataBase.removeData("inventory", "uuid = '" + uuid + "'");
        String string = serialization(player.getInventory());
        dataBase.insertData("inventory", "uuid, inventory", uuid, string);
    }

    @Override
    public String serialization(PlayerInventory inventory) throws IllegalStateException {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
            dataOutput.writeInt(inventory.getSize());

            for(int i = 0; i < inventory.getSize(); i++) dataOutput.writeObject(inventory.getItem(i));
            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());

        } catch (Exception exception) {
            Core.LOGGER.error("Unable to save item stacks", exception);
        }
        return null;
    }

    @Override
    public ItemStack[] deserialization(String data) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            ItemStack[] items = new ItemStack[dataInput.readInt()];

            for (int i = 0; i < items.length; i++) items[i] = (ItemStack) dataInput.readObject();
            dataInput.close();
            return items;

        } catch(Exception exception) {
            Core.LOGGER.error("Unable to decode class type.", exception);
        }
        return null;
    }

    @Override
    public Weapon getWeapon(@NotNull ItemStack itemStack) {
        return getCoreWeapon(itemStack);
    }

    public void readItemStack(Player player, ItemStack itemStack) {
        PersistentDataContainer dataContainer = itemStack.getItemMeta().getPersistentDataContainer();
        Arrays.stream(ItemData.values()).forEach(itemData -> {
            String data = dataContainer.get(new NamespacedKey("core", itemData.name().toLowerCase()), PersistentDataType.STRING);
            player.sendMessage(itemData.name() + " - " + data);
        });
    }

    @Synchronized
    public CoreWeapon getCoreWeapon(@NotNull ItemStack itemStack) {
        Component displayName = itemStack.displayName();
        try {
            int id = Integer.parseInt(getData(itemStack, ItemData.ID));
            int minDamage = Integer.parseInt(getData(itemStack, ItemData.MIN_DAMAGE));
            int maxDamage = Integer.parseInt(getData(itemStack, ItemData.MAX_DAMAGE));
            double criticalChance = Double.parseDouble(getData(itemStack, ItemData.CRITICAL));
            WeaponType weaponType = WeaponType.valueOf(getData(itemStack, ItemData.WEAPON_TYPE).toUpperCase());
            DamageType damageType = DamageType.valueOf(getData(itemStack, ItemData.DAMAGE_TYPE).toUpperCase());
            ItemRarity itemRarity = ItemRarity.valueOf(getData(itemStack, ItemData.RARITY).toUpperCase());
            return new CoreWeapon(id, displayName, itemStack.getType(), ItemType.WEAPON, weaponType, damageType, itemRarity, minDamage, maxDamage, criticalChance);
        } catch (Exception ignored) {
            return null;
        }
    }

    private String getData(@NotNull ItemStack itemStack, ItemData itemData) {
        return itemStack.getItemMeta().getPersistentDataContainer().get(new NamespacedKey("core", itemData.name().toLowerCase()), PersistentDataType.STRING);
    }
}
