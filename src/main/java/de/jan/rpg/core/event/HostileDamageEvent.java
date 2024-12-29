package de.jan.rpg.core.event;

import de.jan.rpg.core.APIImpl;
import de.jan.rpg.core.enemy.CoreEntityManager;
import de.jan.rpg.core.enemy.CoreHostile;
import de.jan.rpg.core.player.CorePlayer;
import de.jan.rpg.core.player.CorePlayerManager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;

public class HostileDamageEvent implements Listener {

    private final CoreEntityManager coreEntityManager;
    private final CorePlayerManager corePlayerManager;

    public HostileDamageEvent(APIImpl api) {
        this.coreEntityManager = api.getCoreEntityManager();
        this.corePlayerManager = api.getCorePlayerManager();
    }

    @EventHandler
    public void onDamageByPlayer(EntityDamageByEntityEvent event) {
        if(!event.getDamager().getType().equals(EntityType.PLAYER)) return;
        Player player = (Player) event.getDamager();

        CorePlayer damager = corePlayerManager.getCorePlayer(player.getUniqueId());
        CoreHostile target = coreEntityManager.getCoreHostile(event.getEntity());
        if(target == null) return;

        int damageValue = (int) Math.round(event.getDamage());
        event.setDamage(0);
        target.damage(damageValue, damager);
    }

    /*
    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if(entity.getType().equals(EntityType.PLAYER)) return;
        CoreHostile coreHostile = coreEntityManager.getCoreHostile(entity);
        if(coreHostile == null) return;

        int damageValue = (int) Math.round(event.getDamage());
        event.setDamage(0);

        coreHostile.damage(damageValue);
        coreHostile.getEntity().customName(coreHostile.displayName());
        Core.LOGGER.info("ede: damageValue:" + damageValue);
    }
     */

    @EventHandler
    public void onDamageByProjectile(EntityDamageByEntityEvent event) {
        if(event.getDamager() instanceof Projectile projectile) {
            CorePlayer damager = corePlayerManager.getCorePlayer(projectile.getOwnerUniqueId());
            if(damager == null) return;

            CoreHostile target = coreEntityManager.getCoreHostile(event.getEntity());
            if(target == null) return;

            int damageValue = (int) Math.round(event.getDamage());
            event.setDamage(0);
            target.damage(damageValue, damager);
        }
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        if(entity.getType().equals(EntityType.PLAYER)) return;
        coreEntityManager.remove(entity);
    }
}
