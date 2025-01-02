package de.jan.rpg.core.event;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

public class CanceledEvent implements Listener {

    @EventHandler
    public void onFood(FoodLevelChangeEvent event) {
        if(event.getFoodLevel() >= 20){
            event.setCancelled(true);
            return;
        }
        event.setFoodLevel(20);
    }

    @EventHandler
    public void onWeather(WeatherChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void spawningEntity(CreatureSpawnEvent event) {
        CreatureSpawnEvent.SpawnReason spawnReason = event.getSpawnReason();
        if(spawnReason.equals(CreatureSpawnEvent.SpawnReason.CUSTOM) || spawnReason.equals(CreatureSpawnEvent.SpawnReason.COMMAND)) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onRegain(EntityRegainHealthEvent event) {
        event.setCancelled(true);
    }

}
