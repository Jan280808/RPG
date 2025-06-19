package de.jan.rpg.core.item;

import de.jan.rpg.api.item.ItemManager;
import de.jan.rpg.core.APIImpl;
import de.jan.rpg.core.Core;
import de.jan.rpg.core.database.CoreDataBase;
import de.jan.rpg.core.item.combat.CoreCombatManager;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.*;

public class CoreItemManager implements ItemManager {

    private final CoreDataBase dataBase;

    @Getter
    private final CoreCombatManager coreCombatManager;

    public CoreItemManager(APIImpl api) {
        this.dataBase = api.getCoreDataBase();
        this.coreCombatManager = new CoreCombatManager();
        dataBase.createTable("inventory", "uuid VARCHAR(100), inventory LONGTEXT");
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
}
