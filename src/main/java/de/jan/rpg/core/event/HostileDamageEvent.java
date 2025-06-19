package de.jan.rpg.core.event;

import de.jan.rpg.api.entity.hostile.RPGHostile;
import de.jan.rpg.api.entity.player.RPGPlayer;
import de.jan.rpg.api.event.RPGHostileDeathEvent;
import de.jan.rpg.api.item.combat.Weapon;
import de.jan.rpg.api.item.combat.data.WeaponType;
import de.jan.rpg.core.APIImpl;
import de.jan.rpg.core.Core;
import de.jan.rpg.core.enemy.CoreEntityManager;
import de.jan.rpg.core.enemy.CoreHostile;
import de.jan.rpg.core.item.combat.CoreCombatManager;
import de.jan.rpg.core.player.CorePlayer;
import de.jan.rpg.core.player.CorePlayerManager;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class HostileDamageEvent implements Listener {

    private final CorePlayerManager corePlayerManager;
    private final CoreEntityManager coreEntityManager;
    private final CoreCombatManager coreCombatManager;

    public HostileDamageEvent(APIImpl api) {
        this.coreEntityManager = api.getCoreEntityManager();
        this.corePlayerManager = api.getCorePlayerManager();
        this.coreCombatManager = api.getCoreItemManager().getCoreCombatManager();
    }

    @EventHandler
    public void onDeath(RPGHostileDeathEvent event) {
        RPGHostile hostile = event.getHostile();
        RPGPlayer killer = (RPGPlayer) hostile.getLastDamager();
        Player player = killer.getPlayer();
        Location startLocation = hostile.getLocation();

        Location location = hostile.getLocation().add(0, 2, 0).add(new Vector(0, 0, 0));
        location.getWorld().spawnParticle(Particle.SCULK_SOUL, location, 1, 0, 0, 0, 0);

        for(int i = 0; i < 2; i++) {
            int delay = 20 + (i * 5);
            Bukkit.getScheduler().runTaskLaterAsynchronously(Core.instance, () -> player.spawnParticle(Particle.VIBRATION, startLocation.clone().add(0, 1, 0), 1, new Vibration(startLocation.clone().subtract(0, 1, 0), new Vibration.Destination.EntityDestination(player), 20)), delay);
        }
    }

    @EventHandler
    public void onDamageByPlayer(EntityDamageByEntityEvent event) {
        if(!event.getDamager().getType().equals(EntityType.PLAYER)) return;
        Player player = (Player) event.getDamager();
        CorePlayer damager = corePlayerManager.getCorePlayer(player.getUniqueId());

        CoreHostile target = coreEntityManager.getCoreHostile(event.getEntity());
        if(target == null) return;

        ItemStack itemStack = player.getInventory().getItemInMainHand();
        if(itemStack.getType().isAir()) {
            event.setCancelled(true);
            return;
        }

        Weapon weapon = coreCombatManager.getWeapon(itemStack);
        if(weapon == null) {
            event.setCancelled(true);
            return;
        }

        if(!weapon.getWeaponType().equals(WeaponType.MELEE)) {
            event.setCancelled(true);
            return;
        }

        float cooldown = player.getAttackCooldown();
        if(cooldown < 0.9f) {
            event.setCancelled(true);
            return;
        }

        target.damageByPlayer(damager, weapon);
    }

    @EventHandler
    public void onProjectileDamage(EntityDamageByEntityEvent event) {
        if(event.getDamager() instanceof Projectile projectile) {
            CorePlayer damager = corePlayerManager.getCorePlayer(projectile.getOwnerUniqueId());
            if(damager == null) return;

            CoreHostile target = coreEntityManager.getCoreHostile(event.getEntity());
            if(target == null) return;

            ItemStack itemStack = damager.getPlayer().getInventory().getItemInMainHand();
            if(itemStack.getType().isAir()) {
                event.setCancelled(true);
                return;
            }

            Weapon weapon = coreCombatManager.getWeapon(itemStack);
            if(weapon == null) {
                event.setCancelled(true);
                return;
            }
            if(!weapon.getWeaponType().equals(WeaponType.RANGE)) {
                event.setCancelled(true);
                return;
            }

            target.damageByPlayer(damager, weapon);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if(entity.getType().equals(EntityType.PLAYER)) return;
        CoreHostile coreHostile = coreEntityManager.getCoreHostile(entity);
        if(coreHostile == null) return;
        event.setDamage(0);
    }
}
