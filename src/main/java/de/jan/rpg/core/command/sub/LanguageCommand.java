package de.jan.rpg.core.command.sub;

import de.jan.rpg.api.translation.Language;
import de.jan.rpg.core.APIImpl;
import de.jan.rpg.core.command.CoreCommands;
import de.jan.rpg.core.player.CorePlayer;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class LanguageCommand implements CoreCommands {

    @Override
    public void onCommand(APIImpl api, Player player, String[] args) {
        CorePlayer corePlayer = api.getCorePlayerManager().getCorePlayer(player.getUniqueId());
        Language language = Language.fromString(args[1]);
        corePlayer.setLanguage(language);
    }

    @Override
    public List<String> subCommands() {
        return Arrays.stream(Language.values()).map(Enum::name).toList();
    }
}
