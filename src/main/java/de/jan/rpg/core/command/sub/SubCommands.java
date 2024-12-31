package de.jan.rpg.core.command.sub;

import de.jan.rpg.core.command.CoreCommands;
import lombok.Getter;

@Getter
public enum SubCommands {
    PLAYER(new PlayerCommand(), "player"),
    COINS(new SoulsCommand(), "souls"),
    LANGUAGE(new LanguageCommand(), "language"),
    TRANSLATION(new TranslationCommand(), "translation"),
    DEATH(new DeathCommand(), "death"),
    ENEMY(new EnemyCommand(), "enemy"),
    PARTICLE(new ParticleCommand(), "particle"),
    ITEM(new ItemCommand(), "item");

    private final CoreCommands commands;
    private final String subCommand;

    SubCommands(CoreCommands commands, String subCommand) {
        this.commands = commands;
        this.subCommand = subCommand;
    }
}
