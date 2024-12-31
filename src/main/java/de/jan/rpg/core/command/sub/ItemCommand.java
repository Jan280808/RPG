package de.jan.rpg.core.command.sub;

import de.jan.rpg.core.APIImpl;
import de.jan.rpg.core.command.CoreCommands;
import de.jan.rpg.core.item.CoreItemManager;
import org.bukkit.entity.Player;

import java.util.List;

public class ItemCommand implements CoreCommands {

    @Override
    public void onCommand(APIImpl api, Player player, String[] args) {
        CoreItemManager itemManager = api.getCoreItemManager();
        if(args.length == 2) {
            if(args[1].equalsIgnoreCase("safe")) {
                itemManager.savePlayerInventory(player);
                return;
            }
            if(args[1].equalsIgnoreCase("load")) {
                itemManager.loadInventory(player);
                return;
            }
        }
    }

    @Override
    public List<String> subCommands() {
        return List.of("safe", "load");
    }
}
