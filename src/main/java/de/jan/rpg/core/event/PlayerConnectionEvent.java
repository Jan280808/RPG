package de.jan.rpg.core.event;

import de.jan.rpg.api.event.RPGPlayerJoinEvent;
import de.jan.rpg.api.event.RPGPlayerQuitEvent;
import de.jan.rpg.core.APIImpl;
import de.jan.rpg.core.player.CorePlayer;
import de.jan.rpg.core.player.CorePlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerConnectionEvent implements Listener {

    private final CorePlayerManager playerManager;

    public PlayerConnectionEvent(APIImpl api) {
        this.playerManager = api.getCorePlayerManager();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        CorePlayer corePlayer = playerManager.getCorePlayer(player.getUniqueId());
        corePlayer.addTotalJoin();
        Bukkit.getPluginManager().callEvent(new RPGPlayerJoinEvent(corePlayer));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        CorePlayer corePlayer = playerManager.getCorePlayer(player.getUniqueId());
        Bukkit.getPluginManager().callEvent(new RPGPlayerQuitEvent(corePlayer));
    }
}
