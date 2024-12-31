package de.jan.rpg.core.command.sub;

import de.jan.rpg.core.APIImpl;
import de.jan.rpg.core.command.CoreCommands;
import de.jan.rpg.core.player.CorePlayer;
import org.bukkit.entity.Player;

import java.util.List;

public class SoulsCommand implements CoreCommands {

    @Override
    public void onCommand(APIImpl api, Player player, String[] args) {
        CorePlayer corePlayer = api.getCorePlayerManager().getCorePlayer(player.getUniqueId());
        corePlayer.addSouls(500);
        player.sendMessage("currentCoins: " + corePlayer.getSouls());
    }

    @Override
    public List<String> subCommands() {
        return null;
    }

}
