package org.example.equipment.stats;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.example.equipment.armor.Armor;
import org.example.equipment.armor.ArmorManager;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerManager {

    private final ArmorManager armorManager;
    private final Map<UUID, PlayerStats> playerStatsMap;

    public PlayerManager(ArmorManager armorManager) {
        this.playerStatsMap = new HashMap<>();
        this.armorManager = armorManager;
    }

    double armorPoints;
    public void loadPlayerStats(Player player) {
        ItemStack[] armorStack = player.getInventory().getArmorContents();
        Arrays.stream(armorStack).forEach(itemStack -> {
            if(itemStack != null) {
                if(armorManager.armorMap.get(itemStack.getItemMeta().getDisplayName()) != null) {
                    Armor armor = armorManager.armorMap.get(itemStack.getItemMeta().getDisplayName());
                    armorPoints = armor.armor();
                }
            }
        });
        PlayerStats playerStats = new PlayerStats(player.getUniqueId(), armorPoints);
        playerStatsMap.put(playerStats.uuid(), playerStats);
        player.sendMessage("armorPoints: " + armorPoints);
    }

    public PlayerStats getPlayerStats(UUID uuid) {
        if(playerStatsMap.get(uuid) != null)
            return playerStatsMap.get(uuid);
        return null;
    }

    public Map<UUID, PlayerStats> getPlayerStatsMap() {
        return playerStatsMap;
    }
}
