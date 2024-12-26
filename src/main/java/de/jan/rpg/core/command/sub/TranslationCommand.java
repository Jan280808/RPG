package de.jan.rpg.core.command.sub;

import de.jan.rpg.core.APIImpl;
import de.jan.rpg.core.command.CoreCommands;
import org.bukkit.entity.Player;

import java.util.List;

public class TranslationCommand implements CoreCommands {

    @Override
    public void onCommand(APIImpl api, Player player, String[] args) {
        api.getCoreTranslation().loadTranslationCache();
        player.sendMessage("loaded translation");
    }

    @Override
    public List<String> subCommands() {
        return List.of();
    }
}
