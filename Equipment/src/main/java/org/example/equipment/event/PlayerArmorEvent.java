package org.example.equipment.event;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.example.equipment.Equipment;
import org.example.equipment.armor.ArmorManager;
import org.example.equipment.stats.PlayerManager;

public class PlayerArmorEvent implements Listener {

    private final ArmorManager armorManager;
    private final PlayerManager playerManager;

    public PlayerArmorEvent() {
        this.armorManager = Equipment.inst.getArmorManager();
        this.playerManager = Equipment.inst.getPlayerManager();
    }

    @EventHandler
    public void onChange(PlayerArmorChangeEvent event) {
        Player player = event.getPlayer();
        playerManager.loadPlayerStats(player);
    }
}
