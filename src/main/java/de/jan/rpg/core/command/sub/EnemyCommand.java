package de.jan.rpg.core.command.sub;

import de.jan.rpg.core.APIImpl;
import de.jan.rpg.core.command.CoreCommands;
import de.jan.rpg.core.enemy.CoreEntityManager;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.List;

public class EnemyCommand implements CoreCommands {

    @Override
    public void onCommand(APIImpl api, Player player, String[] args) {
        CoreEntityManager entityManager = api.getCoreEntityManager();
        entityManager.spawn(player.getLocation(), EntityType.ZOMBIE);
    }

    @Override
    public List<String> subCommands() {
        return List.of();
    }
}
