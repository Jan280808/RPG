package de.jan.rpg.core.command.sub;

import de.jan.rpg.core.APIImpl;
import de.jan.rpg.core.command.CoreCommands;
import org.bukkit.entity.Player;

import java.util.List;

public class RefreshCommand implements CoreCommands {

    @Override
    public void onCommand(APIImpl api, Player player, String[] args) {
        api.getCoreDataBase().refresh(api.getCorePlayerManager());
        player.sendMessage("refresh corePlayers");
    }

    @Override
    public List<String> subCommands() {
        return null;
    }
}
