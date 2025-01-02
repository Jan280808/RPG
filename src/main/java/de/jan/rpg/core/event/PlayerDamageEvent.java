package de.jan.rpg.core.event;

import de.jan.rpg.core.APIImpl;
import de.jan.rpg.core.enemy.CoreEntityManager;
import de.jan.rpg.core.enemy.CoreHostile;
import de.jan.rpg.core.item.CoreItemManager;
import de.jan.rpg.core.item.combat.CoreWeapon;
import de.jan.rpg.core.player.CorePlayer;
import de.jan.rpg.core.player.CorePlayerManager;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerDamageEvent implements Listener {

    private final CorePlayerManager corePlayerManager;
    private final CoreEntityManager coreEntityManager;
    private final CoreItemManager coreItemManager;

    public PlayerDamageEvent(APIImpl api) {
        this.corePlayerManager = api.getCorePlayerManager();
        this.coreEntityManager = api.getCoreEntityManager();
        this.coreItemManager = api.getCoreItemManager();
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if(!event.getEntityType().equals(EntityType.PLAYER)) return;
        event.setDamage(0);
    }

    @EventHandler
    public void onDamageByHostile(EntityDamageByEntityEvent event) {
        if(!event.getEntity().getType().equals(EntityType.PLAYER)) return;

        Player player = (Player) event.getEntity();
        CorePlayer target = corePlayerManager.getCorePlayer(player.getUniqueId());

        CoreHostile damager = coreEntityManager.getCoreHostile(event.getDamager());
        if(damager == null) return;

        target.damageByHostile(damager);
    }

    @EventHandler
    public void onDamageByPlayer(EntityDamageByEntityEvent event) {
        if(!event.getDamager().getType().equals(EntityType.PLAYER)) return;
        Player damagerPlayer = (Player) event.getDamager();
        CorePlayer damager = corePlayerManager.getCorePlayer(damagerPlayer.getUniqueId());

        if(!event.getEntity().getType().equals(EntityType.PLAYER)) return;
        Player targetPlayer = (Player) event.getEntity();
        CorePlayer target = corePlayerManager.getCorePlayer(targetPlayer.getUniqueId());

        ItemStack itemStack = targetPlayer.getInventory().getItemInMainHand();
        if(itemStack.getType().isAir()) return;

        CoreWeapon weapon = coreItemManager.getCoreWeapon(itemStack);
        if(weapon == null) return;

        target.damageByRPGPlayer(damager, weapon);
    }
}
