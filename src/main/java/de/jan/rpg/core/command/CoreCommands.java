package de.jan.rpg.core.command;

import de.jan.rpg.core.APIImpl;
import org.bukkit.entity.Player;

public interface CoreCommands {

    void onCommand(APIImpl api, Player player, String[] args);
}
