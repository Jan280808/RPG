package de.jan.rpg.core.command.sub;

import de.jan.rpg.api.builder.ItemBuilder;
import de.jan.rpg.api.component.ComponentSerializer;
import de.jan.rpg.core.APIImpl;
import de.jan.rpg.core.command.CoreCommands;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.List;

public class PlayerCommand implements CoreCommands {

    @Override
    public void onCommand(APIImpl api, Player player, String[] args) {
        Inventory inventory = Bukkit.createInventory(player, 54, ComponentSerializer.deserialize("registerPlayers"));
        api.getCorePlayerManager().getPlayerMap().forEach((uuid, corePlayer) -> inventory.addItem(new ItemBuilder(Material.PLAYER_HEAD).setDisplayName(corePlayer.displayName()).setLore("uuid" + corePlayer.getUUID(),
                "language: " + corePlayer.getLanguage(),
                "souls: " + corePlayer.getSouls(),
                "maxLife: " + corePlayer.getMaxLife(),
                "level: " + corePlayer.getLevel(),
                "xp: " + corePlayer.getXP()).build()));
        player.openInventory(inventory);
    }

    @Override
    public List<String> subCommands() {
        return null;
    }
}
