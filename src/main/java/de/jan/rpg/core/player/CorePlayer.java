package de.jan.rpg.core.player;

import de.jan.rpg.api.component.ComponentSerializer;
import de.jan.rpg.api.entity.*;
import de.jan.rpg.api.event.RPGPlayerDamageByHostileEvent;
import de.jan.rpg.api.event.RPGPlayerDamageByPlayerEvent;
import de.jan.rpg.api.event.RPGPlayerDamageEvent;
import de.jan.rpg.api.event.RPGPlayerDeathEvent;
import de.jan.rpg.api.exception.OfflineException;
import de.jan.rpg.api.exception.ValueIsNegativeException;
import de.jan.rpg.api.item.combat.Weapon;
import de.jan.rpg.api.translation.Language;
import de.jan.rpg.api.entity.player.RPGPlayer;
import de.jan.rpg.core.Core;
import de.jan.rpg.core.enemy.CoreHostile;
import de.jan.rpg.core.scoreboard.Scoreboard;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.Objects;
import java.util.UUID;

@Getter
public class CorePlayer implements RPGPlayer {

    private final UUID uuid;
    private final String firstJoinDate;

    @Setter
    private Scoreboard scoreboard;

    @Setter
    private boolean isLoaded = false;

    private Language language;
    private int totalJoin;
    private int souls;

    private int level;
    private int xp;
    private int maxLife = 100;
    private int currentLife;
    private int armor = 0;
    private boolean isDeath = false;
    private boolean canDeath = true;
    private boolean canTakeDamage = true;
    private RPGLivingEntity lastDamager;

    private int noHitTime = 10;
    private int regenerationValue = 1;
    private boolean canRegenerate =  true;
    private long lasHitTime;

    private int dashResetTime = 3;
    private double dashDistance = 3.0;
    private boolean canDash =  true;

    public CorePlayer(@NotNull UUID uuid, @NotNull Language language, String firstJoinDate, int totalJoin, int souls, int level, int xp) {
        this.uuid = uuid;
        this.language = language;
        this.firstJoinDate = firstJoinDate;
        this.totalJoin = totalJoin;
        this.souls = souls;
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
    public void addSouls(int amount) {
        setSouls(souls + amount);
    }

    @Override
    public void setSouls(int amount) {
        if(Core.offlineMode) throw new OfflineException();
        if(isNegative(amount)) throw new ValueIsNegativeException("coins", amount);
        souls = amount;
    }

    @Override
    public boolean removeSouls(int amount) {
        setSouls(souls - amount);
        return true;
    }

    @Override
    public int getSouls() {
        if(Core.offlineMode) throw new OfflineException();
        return souls;
    }

    @Override
    public boolean hasEnoughSouls(int amount) {
        return souls >= amount;
    }

    @Override
    public void sendMessage(String message) {
        getPlayer().sendMessage(ComponentSerializer.deserialize(message));
    }

    @Override
    public void sendMessage(Component message) {
        getPlayer().sendMessage(message);
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
    public Location getLocation() {
        return getPlayer().getLocation();
    }

    @Override
    public RPGEntityType getRPGEntityType() {
        return RPGEntityType.RPG_PLAYER;
    }

    @Override
    public void setArmor(int value) {
        armor = value;
        sendActionbar();
    }

    @Override
    public void addArmor(int value) {
        setArmor(armor + value);
        sendActionbar();
    }

    @Override
    public void removeArmor(int value) {
        setArmor(armor - value);
    }

    @Override
    public void setMaxLife(int value) {
        maxLife = value;
        sendActionbar();
    }

    @Override
    public void addMaxLife(int value) {
        setMaxLife(maxLife + value);
    }

    @Override
    public void removeMaxLife(int value) {
        setMaxLife(maxLife - value);
        if(currentLife > maxLife) setCurrentLife(maxLife);
    }

    @Override
    public int getCurrentLife() {
        return currentLife;
    }

    @Override
    public void setCurrentLife(int value) {
        if(value < 0) throw new IllegalArgumentException("");
        currentLife = Math.min(value, maxLife);
        //getPlayer().setHealth(calculateHealth(getPlayer()));
        sendActionbar();
    }

    @Override
    public void addCurrentLife(int value) {
       setCurrentLife(currentLife + value);
    }

    @Override
    public int getNoHitTime() {
        return noHitTime;
    }

    @Override
    public void setNoHitTime(int seconds) {
        noHitTime = seconds;
    }

    @Override
    public long getLastHitTime() {
        return lasHitTime;
    }

    @Override
    public void addRegenerationValue(int value) {
        regenerationValue = value;
    }

    @Override
    public void setCanRegenerate(boolean aBoolean) {
        canRegenerate = aBoolean;
    }

    @Override
    public boolean canRegenerate() {
        return canRegenerate;
    }

    public void regeneration() {
        if(!canRegenerate) {
            long time = System.currentTimeMillis() - lasHitTime;
            if(time < noHitTime * 1000L) return;
            canRegenerate = true;
        }
        addCurrentLife(regenerationValue);
    }

    private double calculateHealth(Player player) {
        double ratio = (double) currentLife / maxLife;
        double maxHealth = Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue();
        return ratio * maxHealth;
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
    public void death(@NotNull DeathReason deathReason) {
        if(isDeath || !canDeath) return;
        isDeath = true;
        setCurrentLife(0);
        canTakeDamage = false;
        removeTargetStateFromHostileEntities();
        runDeathAnimation();
        Bukkit.getPluginManager().callEvent(new RPGPlayerDeathEvent(this, deathReason));
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
    public void damage(int value, @NotNull DamageReason damageReason) {
        Bukkit.getPluginManager().callEvent(new RPGPlayerDamageEvent(this, value, damageReason));

        canRegenerate = false;
        lasHitTime = System.currentTimeMillis();

        if(value >= currentLife) {
            death(DeathReason.NO_LIFE);
            return;
        }

        int reducedDamage = value - (armor * 5 / 100);
        setCurrentLife(currentLife - reducedDamage);
        setInvulnerable(10);
    }

    @Override
    public RPGLivingEntity getLastDamager() {
        return lastDamager;
    }

    @Override
    public int getDashResetTime() {
        return dashResetTime;
    }

    @Override
    public void setDashResetTime(int seconds) {
        dashResetTime = seconds;
    }

    @Override
    public double getDashDistance() {
        return dashDistance;
    }

    @Override
    public void setDashDistance(double value) {
        dashDistance = value;
    }

    @Override
    public void setCanDash(boolean aBoolean) {
        canDash = aBoolean;
        sendActionbar();
    }

    @Override
    public boolean canDash() {
        return canDash;
    }

    @Override
    public void dash(Vector direction) {
        Entity entity = getPlayer();
        if(!entity.isOnGround() || !canDash()) return;

        Vector dashVector = direction.multiply(getDashDistance());
        entity.setVelocity(dashVector);
        entity.getWorld().playSound(entity.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 1.0f, 1.0f);
        setCanDash(false);

        Bukkit.getScheduler().runTaskLaterAsynchronously(Core.instance, () -> setCanDash(true), 20L * getDashResetTime());
    }

    public void damageByHostile(@NotNull CoreHostile damager) {
        if(!canTakeDamage) return;
        lastDamager = damager;
        int damage = damager.getTotalDamage();
        damage(damage, DamageReason.HOSTILE_HIT);
        Bukkit.getPluginManager().callEvent(new RPGPlayerDamageByHostileEvent(this, damager, damage));
    }

    public void damageByRPGPlayer(@NotNull CorePlayer damager, @NotNull Weapon weapon) {
        if(!canTakeDamage) return;
        lastDamager = damager;
        int damage = weapon.getRandomDamage();
        damage(damage, DamageReason.PLAYER_HIT);
        Bukkit.getPluginManager().callEvent(new RPGPlayerDamageByPlayerEvent(this, damager, damage));
    }

    private void setInvulnerable(int delayInTicks) {
        canTakeDamage = false;
        Bukkit.getScheduler().runTaskLaterAsynchronously(Core.instance, () -> canTakeDamage = true, delayInTicks);
    }

    public void sendActionbar() {
        if(getPlayer() == null) return;
        String separator = " <dark_gray>- ";
        String life = "<red>" + getCurrentLife() + "/" + getMaxLife() + "❤";
        String armor = "<gray>" + getArmor() + "\uD83D\uDEE1";
        String dash = canDash ? "<green><bold>➾" : "<gray><bold>➾";
        Component component = ComponentSerializer.deserialize(life + separator + armor + separator + dash);
        getPlayer().sendActionBar(component);
    }

    int countdown = 5;
    int taskID;
    private void runDeathAnimation() {
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
