package de.jan.rpg.core.command.sub;

import de.jan.rpg.core.command.CoreCommands;
import lombok.Getter;

@Getter
public enum SubCommands {
    PLAYER(new PlayerCommand(), "player"),
    REFRESH(new RefreshCommand(), "refresh"),
    COINS(new CoinCommand(), "coins"),
    LANGUAGE(new LanguageCommand(), "language"),
    TRANSLATION(new TranslationCommand(), "translation");

    private final CoreCommands commands;
    private final String subCommand;

    SubCommands(CoreCommands commands, String subCommand) {
        this.commands = commands;
        this.subCommand = subCommand;
    }
}
