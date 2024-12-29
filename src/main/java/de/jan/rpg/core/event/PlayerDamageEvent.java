package de.jan.rpg.core.event;

import de.jan.rpg.api.event.RPGPlayerDamageEvent;
import de.jan.rpg.core.APIImpl;
import de.jan.rpg.core.player.CorePlayer;
import de.jan.rpg.core.player.CorePlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class PlayerDamageEvent implements Listener {

    private final CorePlayerManager corePlayerManager;

    public PlayerDamageEvent(APIImpl api) {
        this.corePlayerManager = api.getCorePlayerManager();
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if(!event.getEntityType().equals(EntityType.PLAYER)) return;

        Player player = (Player) event.getEntity();
        CorePlayer corePlayer = corePlayerManager.getCorePlayer(player.getUniqueId());
        int damage = (int) Math.round(event.getDamage());
        corePlayer.damage(damage, null);
        event.setDamage(0);
        Bukkit.getPluginManager().callEvent(new RPGPlayerDamageEvent(corePlayer, damage, event.getCause()));
    }
}
