package de.jan.rpg.core.command.sub;

import de.jan.rpg.core.APIImpl;
import de.jan.rpg.core.command.CoreCommands;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Objects;

public class PlayerCommand implements CoreCommands {

    @Override
    public void onCommand(APIImpl api, Player player, String[] args) {
        api.getCorePlayerManager().getPlayerMap().forEach((uuid, corePlayer) -> {
            String name = Objects.requireNonNull(Bukkit.getPlayer(corePlayer.getUUID())).getName();
            player.sendMessage("playerName:" + name);
            player.sendMessage("uuid:" + corePlayer.getUUID());
        });
    }
}
