package de.jan.rpg.core.player;

import de.jan.rpg.api.component.ComponentSerializer;
import de.jan.rpg.api.event.RPGPlayerDeathEvent;
import de.jan.rpg.api.exception.OfflineException;
import de.jan.rpg.api.exception.ValueIsNegativeException;
import de.jan.rpg.api.translation.Language;
import de.jan.rpg.api.player.RPGPlayer;
import de.jan.rpg.core.Core;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.UUID;

@Getter
public class CorePlayer implements RPGPlayer {

    private final UUID uuid;
    private final String firstJoinDate;

    private Language language;
    private int totalJoin;
    private int coins;

    private int level;
    private int xp;
    private int maxLife = 20;
    private int currentLife;
    private int armor = 0;
    private boolean isDeath = false;
    private boolean canDeath = true;
    private boolean canTakeDamage = true;

    public CorePlayer(@NotNull UUID uuid, @NotNull Language language, String firstJoinDate, int totalJoin, int coins, int level, int xp) {
        this.uuid = uuid;
        this.language = language;
        this.firstJoinDate = firstJoinDate;
        this.totalJoin = totalJoin;
        this.coins = coins;
        this.currentLife = maxLife;
        this.level = level;
        this.xp = xp;
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

    @Override
    public void sendMessage(String message) {
        getPlayer().sendMessage(ComponentSerializer.deserialize(message));
    }

    @Override
    public int getLevel() {
        return level;
    }

    public void levelUp() {
        level = level +1;
    }

    @Override
    public int getXP() {
        return xp;
    }

    @Override
    public void addXP(int value) {
        xp = xp + value;
    }

    public void addTotalJoin() {
        totalJoin = totalJoin + 1;
    }

    private boolean isNegative(int value) {
        return value < 0;
    }

    @Override
    public Component displayName() {
        return null;
    }

    @Override
    public Entity getEntity() {
        if(getPlayer() == null) return null;
        return getPlayer();
    }

    @Override
    public int level() {
        return 0;
    }

    @Override
    public boolean canTakeDamage() {
        return canTakeDamage;
    }

    @Override
    public void canTakeDamage(Boolean aBoolean) {
        canTakeDamage = aBoolean;
    }

    @Override
    public boolean isDeath() {
        return isDeath;
    }

    @Override
    public boolean canDeath() {
        return canDeath;
    }

    @Override
    public void canDeath(Boolean aBoolean) {
        canDeath = aBoolean;
    }

    @Override
    public void setMaxLife(int value) {
        maxLife = value;
        sendActionbar();
    }

    @Override
    public int getCurrentLife() {
        return currentLife;
    }

    @Override
    public void setCurrentLife(int value) {
        currentLife = value;
        sendActionbar();
    }

    @Override
    public void addCurrentLife(int value) {
        currentLife = currentLife + value;
        sendActionbar();
    }

    @Override
    public void damage(int value, RPGPlayer damager) {
        if(!canTakeDamage) return;

        if(value >= currentLife) {
            Bukkit.getPluginManager().callEvent(new RPGPlayerDeathEvent(this));
            currentLife = 0;
            sendActionbar();
            death();
            return;
        }
        currentLife -= value;
        sendActionbar();

        //invisible time
        canTakeDamage = false;
        Bukkit.getScheduler().scheduleSyncDelayedTask(Core.instance, () -> canTakeDamage = true, 10);
    }

    @Override
    public void deathByPlayer(@NotNull RPGPlayer killer) {

    }

    @Override
    public void death() {
        if(isDeath) return;
        currentLife = 0;
        isDeath = true;
        canTakeDamage = false;
        removeTargetStateFromHostileEntities();
        runDeathAnimation();
    }

    @Override
    public int getArmor() {
        return armor;
    }

    @Override
    public void setArmor(int value) {
        armor = value;
        sendActionbar();
    }

    @Override
    public void addArmor(int value) {
        armor = value;
        sendActionbar();
    }

    @Override
    public void removeArmor(int value) {
        if(value >= armor) {
            armor = 0;
            return;
        }
        armor -= value;
    }

    public void sendActionbar() {
        if(getPlayer() == null) return;
        getPlayer().sendActionBar(ComponentSerializer.deserialize("<red>" + currentLife + "/" + maxLife + "❤ <dark_gray>- <gray>" + armor + "\uD83D\uDEE1"));
    }

    int countdown = 5;
    int taskID;
    public void runDeathAnimation() {
        Player player = getPlayer();
        if(player == null) return;

        player.setInvisible(true);
        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 0, false, false));
        TextDisplay display = (TextDisplay) player.getWorld().spawnEntity(player.getLocation().add(0, 3, 0), EntityType.TEXT_DISPLAY);
        display.setVisibleByDefault(false);
        player.showEntity(Core.instance, display);
        display.addPassenger(player);

        taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(Core.instance, () -> {
            if(countdown == 0) {
                Bukkit.getScheduler().cancelTask(taskID);
                countdown = 5;
                display.remove();
                setCurrentLife(maxLife);
                isDeath = false;
                canTakeDamage = true;
                player.setInvisible(false);
                player.sendMessage("Du lebst wieder");
                player.clearTitle();
                player.removePotionEffect(PotionEffectType.BLINDNESS);
                return;
            }
            player.showTitle(Title.title(ComponentSerializer.deserialize("<red>Gestorben"), ComponentSerializer.deserialize("Respawn in: " + countdown + " Sekunden"), Title.Times.times(Duration.ZERO, Duration.ofSeconds(1), Duration.ZERO)));
            countdown--;
        }, 0, 20);
    }

    private void removeTargetStateFromHostileEntities() {
        getPlayer().getNearbyEntities(50, 50, 50).forEach(entity -> {
            if(entity instanceof Monster monster) {
                LivingEntity target = monster.getTarget();
                if(target == null) return;
                if(!target.equals(getPlayer())) return;
                monster.setTarget(null);
            }
        });
    }
}
