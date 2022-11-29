package org.example.equipment.stats;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public record PlayerStats(UUID uuid, double armor) {

    public Player getPlayer() {
        if(Bukkit.getPlayer(uuid) != null)
            return Bukkit.getPlayer(uuid);
        return null;
    }
}
