package org.example.equipment.weapon;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeaponManager {

    public final List<Weapon> weaponList;
    public final Map<String, Weapon> weaponMap;

    public WeaponManager() {
        this.weaponList = new ArrayList<>();
        this.weaponMap = new HashMap<>();
    }

    public void createObjects() {
        for(WeaponList list : WeaponList.values()) {
            Weapon weapon = new Weapon(list.getItemStack(), list.getName(), list.getLoreLine1(), list.getLoreLine2(), list.getDamage(), list.getCritical(), list.getRarity(), list.getType());
            weapon.setupItemStack();

            weaponList.add(weapon);
            weaponMap.put(weapon.name(), weapon);
            Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "create object " + weapon.name());
        }
    }

    public List<Weapon> getWeaponList() {
        return weaponList;
    }

    public Map<String, Weapon> getWeaponMap() {
        return weaponMap;
    }
}
