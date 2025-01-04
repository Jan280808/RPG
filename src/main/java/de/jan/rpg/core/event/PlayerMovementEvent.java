package de.jan.rpg.core.event;

import de.jan.rpg.core.APIImpl;
import de.jan.rpg.core.player.CorePlayer;
import de.jan.rpg.core.player.CorePlayerManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.UUID;

public class PlayerMovementEvent implements Listener {

    private final CorePlayerManager corePlayerManager;
    private final HashMap<UUID, Long> lastSneakTime = new HashMap<>();
    private final HashMap<UUID, Vector> lastMovementDirection = new HashMap<>();

    public PlayerMovementEvent(APIImpl api) {
        this.corePlayerManager = api.getCorePlayerManager();
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location from = event.getFrom();
        Location to = event.getTo();
        if(from.distanceSquared(to) > 0) lastMovementDirection.put(player.getUniqueId(), to.toVector().subtract(from.toVector()).normalize());
    }

    @EventHandler
    public void onPlayerSneak(PlayerToggleSneakEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        CorePlayer corePlayer = corePlayerManager.getCorePlayer(uuid);

        if(!event.isSneaking()) return;
        long currentTime = System.currentTimeMillis();

        if (lastSneakTime.containsKey(uuid) && currentTime - lastSneakTime.get(uuid) <= 500) {
            Vector dashDirection = lastMovementDirection.getOrDefault(uuid, corePlayer.getPlayer().getLocation().getDirection().normalize());
            corePlayer.dash(dashDirection);
            lastSneakTime.remove(uuid);
        } else {
            lastSneakTime.put(uuid, currentTime);
        }
    }
}
