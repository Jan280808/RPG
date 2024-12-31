package de.jan.rpg.core.event;

import de.jan.rpg.api.event.RPGPlayerJoinEvent;
import de.jan.rpg.api.event.RPGPlayerQuitEvent;
import de.jan.rpg.core.APIImpl;
import de.jan.rpg.core.item.CoreItemManager;
import de.jan.rpg.core.player.CorePlayer;
import de.jan.rpg.core.player.CorePlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerConnectionEvent implements Listener {

    private final CorePlayerManager playerManager;
    private final CoreItemManager itemManager;

    public PlayerConnectionEvent(APIImpl api) {
        this.playerManager = api.getCorePlayerManager();
        this.itemManager = api.getCoreItemManager();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        CorePlayer corePlayer = playerManager.getCorePlayer(event.getPlayer().getUniqueId());
        corePlayer.addTotalJoin();
        corePlayer.getPlayer().getInventory().clear();

        itemManager.loadInventory(corePlayer.getPlayer());
        Bukkit.getPluginManager().callEvent(new RPGPlayerJoinEvent(corePlayer));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        CorePlayer corePlayer = playerManager.getCorePlayer(event.getPlayer().getUniqueId());

        playerManager.safeCorePlayer(corePlayer.getPlayer());
        itemManager.savePlayerInventory(corePlayer.getPlayer());
        corePlayer.getPlayer().getInventory().clear();
        Bukkit.getPluginManager().callEvent(new RPGPlayerQuitEvent(corePlayer));
    }
}
