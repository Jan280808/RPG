package de.jan.rpg.core.event;

import de.jan.rpg.api.event.RPGPlayerDeathEvent;
import de.jan.rpg.api.entity.player.RPGPlayer;
import de.jan.rpg.core.APIImpl;
import de.jan.rpg.core.player.CorePlayer;
import de.jan.rpg.core.player.CorePlayerManager;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDismountEvent;
import org.bukkit.event.entity.EntityTargetEvent;

public class PlayerDeathEvent implements Listener {

    private final CorePlayerManager corePlayerManager;

    public PlayerDeathEvent(APIImpl api) {
        this.corePlayerManager = api.getCorePlayerManager();
    }

    @EventHandler
    public void onDeath(RPGPlayerDeathEvent event) {
        RPGPlayer rpgPlayer = event.getRpgPlayer();
        CorePlayer corePlayer = corePlayerManager.getCorePlayer(rpgPlayer.getUUID());

    }

    @EventHandler
    public void onDeath(org.bukkit.event.entity.PlayerDeathEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityTarget(EntityTargetEvent event) {
        if(event.getTarget() == null) return;
        if(!event.getTarget().getType().equals(EntityType.PLAYER)) return;
        Player player = (Player) event.getTarget();
        CorePlayer corePlayer = corePlayerManager.getCorePlayer(player.getUniqueId());
        if(!corePlayer.isDeath()) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onMount(EntityDismountEvent event) {
        if(!event.getEntity().getType().equals(EntityType.PLAYER)) return;
        Player player =  (Player) event.getEntity();
        CorePlayer corePlayer = corePlayerManager.getCorePlayer(player.getUniqueId());

        if(!corePlayer.isDeath()) return;
        event.setCancelled(true);
    }
}
