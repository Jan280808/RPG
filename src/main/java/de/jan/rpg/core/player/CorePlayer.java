package de.jan.rpg.core.player;

import de.jan.rpg.api.exception.OfflineException;
import de.jan.rpg.api.exception.ValueIsNegativeException;
import de.jan.rpg.api.translation.Language;
import de.jan.rpg.api.player.RPGPlayer;
import de.jan.rpg.core.Core;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Getter
public class CorePlayer implements RPGPlayer {

    private final UUID uuid;
    private final String firstJoinDate;

    private Language language;
    private int totalJoin;
    private int coins;

    public CorePlayer(@NotNull UUID uuid, @NotNull Language language, String firstJoinDate, int totalJoin, int coins) {
        this.uuid = uuid;
        this.language = language;
        this.firstJoinDate = firstJoinDate;
        this.totalJoin = totalJoin;
        this.coins = coins;
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    @Override
    public String getFirstJoinDate() {
        return firstJoinDate;
    }

    @Override
    public Language getLanguage() {
        if(language == null) return Language.ENGLISH;
        return language;
    }

    @Override
    public void setLanguage(Language language) {
        this.language = language;
        getPlayer().sendMessage("change language to: " + language);
    }

    @Override
    public void addCoins(int amount) {
        if(Core.offlineMode) throw new OfflineException();
        coins = coins + amount;
    }

    @Override
    public void setCoins(int amount) {
        if(Core.offlineMode) throw new OfflineException();
        if(isNegative(amount)) throw new ValueIsNegativeException("coins", amount);
        coins = amount;
    }

    @Override
    public boolean removeCoins(int amount) {
        if(Core.offlineMode) throw new OfflineException();
        if(isNegative(amount)) throw new ValueIsNegativeException("coins", amount);
        coins = coins - amount;
        return true;
    }

    @Override
    public int getCoins() {
        if(Core.offlineMode) throw new OfflineException();
        return coins;
    }

    @Override
    public boolean hasEnoughCoins(int amount) {
        return coins >= amount;
    }

    public void addTotalJoin() {
        totalJoin = totalJoin + 1;
    }

    private boolean isNegative(int value) {
        return value < 0;
    }

}
