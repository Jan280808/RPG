package de.jan.rpg.core.command.sub;

import de.jan.rpg.core.APIImpl;
import de.jan.rpg.core.Core;
import de.jan.rpg.core.command.CoreCommands;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Vibration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.List;

public class ParticleCommand implements CoreCommands {

    @Override
    public void onCommand(APIImpl api, Player player, String[] args) {
        Location location = player.getLocation();
        location.getNearbyEntities(50, 50,50).forEach(entity -> {
            Vibration vibration = new Vibration(player.getLocation(), new Vibration.Destination.EntityDestination(entity), 20);
            location.getWorld().spawnParticle(Particle.VIBRATION, location, 1, vibration);
        });
    }

    @Override
    public List<String> subCommands() {
        return List.of();
    }
}
