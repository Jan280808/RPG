package org.example.enemies.event;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.example.enemies.Enemies;
import org.example.equipment.weapon.Weapon;
import org.example.equipment.weapon.WeaponManager;

public class EntityDamage implements Listener {

    private final WeaponManager weaponManager;
    public EntityDamage() {
        this.weaponManager = Enemies.inst.getWeaponManager();
    }

    @EventHandler
    private void onDamage(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        EntityType type = event.getEntityType();

        if(!event.getDamager().getType().equals(EntityType.PLAYER))
            return;
        Player damager = (Player) event.getDamager();
        if(type.equals(EntityType.ARMOR_STAND) || type.equals(EntityType.PLAYER))
            return;
        if(damager.getInventory().getItemInMainHand().getType() == Material.AIR)
            return;
        ItemStack useItem = damager.getInventory().getItemInMainHand();

        String key = useItem.getItemMeta().getDisplayName();
        if(weaponManager.getWeaponMap().get(key) == null)
            return;
        Weapon weapon = weaponManager.getWeaponMap().get(key);
        double weaponDamage = weapon.damage();

        boolean critical = Math.random() < weapon.critical();
        if(critical) {
            weaponDamage = weaponDamage*2;
            event.setDamage(weaponDamage*2);
        }
        spawnDamageHologram(entity.getLocation(),weaponDamage, critical);
    }

    private void spawnDamageHologram(Location location, double damage, boolean critical) {
        ArmorStand hologram = location.getWorld().spawn(location.add(0, 1.5, 0), ArmorStand.class);
        hologram.setSmall(true);
        hologram.setVisible(false); hologram.setGravity(false);
        hologram.setSmall(true); hologram.setCustomNameVisible(true);

        if(critical) {
            hologram.setCustomName(ChatColor.YELLOW + "" + damage);
        } else
            hologram.setCustomName(ChatColor.RED + "" + damage);
        Bukkit.getScheduler().scheduleSyncDelayedTask(Enemies.inst, hologram::remove, 10);
    }
}
