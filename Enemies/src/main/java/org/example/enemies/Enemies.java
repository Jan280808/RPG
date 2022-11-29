package org.example.enemies;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.example.enemies.event.EntityDamage;
import org.example.equipment.Equipment;
import org.example.equipment.armor.ArmorManager;
import org.example.equipment.weapon.WeaponManager;

public final class Enemies extends JavaPlugin {

    public static Enemies inst;
    private Equipment equipment;
    private WeaponManager weaponManager;
    private ArmorManager armorManager;

    @Override
    public void onEnable() {
        // Plugin startup logic
        inst = this;
        this.equipment = Equipment.inst;
        this.weaponManager = equipment.getWeaponManager();
        this.armorManager = equipment.getArmorManager();
        registerListener(Bukkit.getPluginManager());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void registerListener(PluginManager pluginManager) {
        pluginManager.registerEvents(new EntityDamage(), this);
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public WeaponManager getWeaponManager() {
        return weaponManager;
    }

    public ArmorManager getArmorManager() {
        return armorManager;
    }
}
