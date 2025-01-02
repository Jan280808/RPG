package de.jan.rpg.core.command.sub;

import de.jan.rpg.core.APIImpl;
import de.jan.rpg.core.command.CoreCommands;
import de.jan.rpg.core.item.CoreItemManager;
import de.jan.rpg.core.player.CorePlayerManager;
import org.bukkit.entity.Player;

import java.util.List;

public class ItemCommand implements CoreCommands {

    @Override
    public void onCommand(APIImpl api, Player player, String[] args) {
        CoreItemManager coreItemManager = api.getCoreItemManager();

        if(args.length == 2) {
            if(args[1].equalsIgnoreCase("give")) {
                coreItemManager.giveItem(player);
            }
            if(args[1].equalsIgnoreCase("read")) {
                coreItemManager.readItemStack(player, player.getInventory().getItemInMainHand());
            }
            if(args[1].equalsIgnoreCase("convert")) {
                coreItemManager.convertItem(player);
            }
        }
        coreItemManager.giveItem(player);
    }

    @Override
    public List<String> subCommands() {
        return List.of("give", "read", "convert");
    }
}
