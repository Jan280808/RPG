package de.jan.rpg.core.command.sub;

import de.jan.rpg.core.APIImpl;
import de.jan.rpg.core.command.CoreCommands;
import de.jan.rpg.core.player.CorePlayer;
import org.bukkit.entity.Player;

import java.util.List;

public class DeathCommand implements CoreCommands {

    @Override
    public void onCommand(APIImpl api, Player player, String[] args) {
        CorePlayer corePlayer = api.getCorePlayerManager().getCorePlayer(player.getUniqueId());
        corePlayer.death();
    }

    @Override
    public List<String> subCommands() {
        return List.of();
    }
}
