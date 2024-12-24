package de.jan.rpg.core.player;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CorePlayer implements de.jan.rpg.api.player.RPGPlayer {

    private final UUID uuid;

    public CorePlayer(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }
}
