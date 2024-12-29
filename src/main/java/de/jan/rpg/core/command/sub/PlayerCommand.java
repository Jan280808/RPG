package de.jan.rpg.core.command.sub;

import de.jan.rpg.api.builder.ItemBuilder;
import de.jan.rpg.core.APIImpl;
import de.jan.rpg.core.Core;
import de.jan.rpg.core.command.CoreCommands;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

public class PlayerCommand implements CoreCommands {

    @Override
    public void onCommand(APIImpl api, Player player, String[] args) {
        api.getCorePlayerManager().getPlayerMap().forEach((uuid, corePlayer) -> {
            player.sendMessage("uuid: " + corePlayer.getUUID());
            player.sendMessage("firstJoin: " + corePlayer.getFirstJoinDate());
            player.sendMessage("totalJoin: " + corePlayer.getTotalJoin());
            player.sendMessage("coins: " + corePlayer.getCoins());
            player.sendMessage("language: " + corePlayer.getLanguage());
            player.getInventory().addItem(new ItemBuilder(Material.STONE).setDisplayName(Core.instance.getCoreAPI().getCoreTranslation().getTranslation(corePlayer.getLanguage(), "dungeon.test")).build());
        });
    }

    @Override
    public List<String> subCommands() {
        return null;
    }
}
