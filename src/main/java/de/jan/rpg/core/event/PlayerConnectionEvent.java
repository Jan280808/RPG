package de.jan.rpg.core.event;

import de.jan.rpg.api.component.ComponentSerializer;
import de.jan.rpg.api.event.RPGPlayerJoinEvent;
import de.jan.rpg.api.event.RPGPlayerQuitEvent;
import de.jan.rpg.core.APIImpl;
import de.jan.rpg.core.item.CoreItemManager;
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
    private final CoreItemManager coreItemManager;

    public PlayerConnectionEvent(APIImpl api) {
        this.playerManager = api.getCorePlayerManager();
        this.coreItemManager = api.getCoreItemManager();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        CorePlayer corePlayer = playerManager.getCorePlayer(player.getUniqueId());
        corePlayer.addTotalJoin();
        Bukkit.getPluginManager().callEvent(new RPGPlayerJoinEvent(corePlayer));
        coreItemManager.loadInventory(player);

        event.joinMessage(ComponentSerializer.deserialize("<gray>[<green>+" + player.getName() + " <gray>]"));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        CorePlayer corePlayer = playerManager.getCorePlayer(player.getUniqueId());
        Bukkit.getPluginManager().callEvent(new RPGPlayerQuitEvent(corePlayer));
        playerManager.safeCorePlayer(corePlayer);
        coreItemManager.savePlayerInventory(player);

        event.quitMessage(ComponentSerializer.deserialize("<gray>[<red>-" + player.getName() + "<gray>]"));
    }
}
