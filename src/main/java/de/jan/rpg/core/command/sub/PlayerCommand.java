package de.jan.rpg.core.command.sub;

import de.jan.rpg.api.builder.ItemBuilder;
import de.jan.rpg.core.APIImpl;
import de.jan.rpg.core.Core;
import de.jan.rpg.core.command.CoreCommands;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;

public class PlayerCommand implements CoreCommands {

    @Override
    public void onCommand(APIImpl api, Player player, String[] args) {
        api.getCorePlayerManager().getPlayerMap().forEach((uuid, corePlayer) -> {
            String name = Objects.requireNonNull(Bukkit.getPlayer(corePlayer.getUUID())).getName();
            player.sendMessage("playerName: " + name);
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
