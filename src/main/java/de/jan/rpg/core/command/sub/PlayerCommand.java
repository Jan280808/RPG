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
        api.getCorePlayerManager().safeCorePlayer(player);
    }

    @Override
    public List<String> subCommands() {
        return null;
    }
}
