package de.jan.rpg.core.command.sub;

import de.jan.rpg.core.APIImpl;
import de.jan.rpg.core.command.CoreCommands;
import de.jan.rpg.core.enemy.CoreEntityManager;
import org.bukkit.entity.Player;

import java.util.List;

public class DummyCommand implements CoreCommands {

    @Override
    public void onCommand(APIImpl api, Player player, String[] args) {
        CoreEntityManager coreEntityManager = api.getCoreEntityManager();
        coreEntityManager.spawnDummy(player.getLocation());
    }

    @Override
    public List<String> subCommands() {
        return List.of();
    }
}
