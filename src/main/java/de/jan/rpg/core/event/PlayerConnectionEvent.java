package de.jan.rpg.core.event;

import de.jan.rpg.api.component.ComponentSerializer;
import de.jan.rpg.api.event.RPGPlayerJoinEvent;
import de.jan.rpg.api.event.RPGPlayerQuitEvent;
import de.jan.rpg.api.item.combat.Status;
import de.jan.rpg.core.APIImpl;
import de.jan.rpg.core.Core;
import de.jan.rpg.core.enemy.CoreHostile;
import de.jan.rpg.core.item.CoreItemManager;
import de.jan.rpg.core.player.CorePlayer;
import de.jan.rpg.core.player.CorePlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.util.RayTraceResult;

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
        playerManager.initializePlayer(corePlayer);
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

    @EventHandler
    public void onPlayerUseSpyglass(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        // Check if the player is holding a spyglass
        if (player.getInventory().getItemInMainHand().getType() == Material.SPYGLASS) {
            // Perform a ray trace to detect what the player is looking at
            RayTraceResult result = player.getWorld().rayTraceEntities(
                    player.getEyeLocation(),
                    player.getEyeLocation().getDirection(),
                    50, // Maximum distance for the ray trace
                    entity -> !entity.equals(player) // Ignore the player themselves
            );

            if (result != null) {
                Entity targetedEntity = result.getHitEntity();
                if (targetedEntity != null) {
                    CoreHostile hostile = Core.instance.getCoreAPI().getCoreEntityManager().getCoreHostile(targetedEntity);
                    if(hostile == null) return;
                    player.sendMessage("hostile: " + hostile.getEntity());
                    player.sendMessage(ComponentSerializer.deserialize("displayName: ").append(hostile.displayName()));
                    player.sendMessage("currentLife: " + hostile.getCurrentLife());
                    player.sendMessage("armor: " + hostile.getArmor());
                    player.sendMessage("damage: " + hostile.getTotalDamage());

                    Status status = hostile.getStatus(Status.Type.FIRE);
                    player.sendMessage("resistanceValue: " + status.getResistanceValue());
                    player.sendMessage("currentValue: " + status.getCurrentValue());
                    player.sendMessage("isActivated: " + status.isRunning());
                }
            }
        }
    }
}
