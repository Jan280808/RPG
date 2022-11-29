package org.example.equipment;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.example.equipment.armor.ArmorManager;
import org.example.equipment.command.EquipmentCommand;
import org.example.equipment.event.PlayerArmorEvent;
import org.example.equipment.stats.PlayerManager;
import org.example.equipment.weapon.WeaponManager;

import java.util.Objects;

public final class Equipment extends JavaPlugin {

    public static Equipment inst;
    private WeaponManager weaponManager;
    private ArmorManager armorManager;
    private PlayerManager playerManager;

    @Override
    public void onEnable() {
        // Plugin startup logic
        inst = this;
        this.weaponManager = new WeaponManager();
        weaponManager.createObjects();

        this.armorManager = new ArmorManager();
        armorManager.createObjects();

        this.playerManager = new PlayerManager(armorManager);
        registerCommands();
        registerEvents(Bukkit.getPluginManager());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void registerEvents(PluginManager pluginManager) {
        pluginManager.registerEvents(new PlayerArmorEvent(), this);
    }

    private void registerCommands() {
        Objects.requireNonNull(getCommand("weapon")).setExecutor(new EquipmentCommand());
    }

    public WeaponManager getWeaponManager() {
        return weaponManager;
    }

    public ArmorManager getArmorManager() {
        return armorManager;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }
}
