package org.example.equipment.armor;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.*;

public class ArmorManager {

    public final Map<String, Armor> armorMap;
    public final List<Armor> armorList;

    public ArmorManager() {
        this.armorMap = new HashMap<>();
        this.armorList = new ArrayList<>();
    }

    public void createObjects() {
        Arrays.stream(ArmorList.values()).forEach(armorList1 -> {
            Armor armor = new Armor(armorList1.getItemStack(), armorList1.getName(), armorList1.getArmor(), armorList1.getDurability(), armorList1.getStats(), armorList1.getRarity());
            armor.setupItemStack();
            armorMap.put(armor.name(), armor);
            armorList.add(armor);
            Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "create object " + armor.name());
        });
    }
}
