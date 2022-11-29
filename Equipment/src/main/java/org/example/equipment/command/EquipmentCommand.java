package org.example.equipment.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.example.equipment.Equipment;
import org.example.equipment.armor.ArmorManager;
import org.example.equipment.weapon.WeaponManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EquipmentCommand implements TabExecutor {

    private final WeaponManager weaponManager;
    private final ArmorManager armorManager;

    public EquipmentCommand() {
        this.weaponManager = Equipment.inst.getWeaponManager();
        this.armorManager = Equipment.inst.getArmorManager();
    }

    @Override @Deprecated
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender instanceof ConsoleCommandSender) {
            sender.sendMessage("you must be a player");
            return false;
        }
        Player player = (Player) sender;
        if(!player.hasPermission(""))
            return false;

        Inventory inventory = Bukkit.createInventory(null, 54, "");
        weaponManager.weaponList.forEach(weapon -> inventory.addItem(weapon.itemStack()));
        armorManager.armorList.forEach(armor -> inventory.addItem(armor.itemStack()));
        player.openInventory(inventory);
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return null;
    }
}
