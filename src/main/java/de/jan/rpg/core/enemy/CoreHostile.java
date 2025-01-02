package de.jan.rpg.core.enemy;

import de.jan.rpg.api.component.ComponentSerializer;
import de.jan.rpg.api.entity.*;
import de.jan.rpg.api.entity.hostile.RPGHostile;
import de.jan.rpg.api.event.RPGHostileDamageByPlayerEvent;
import de.jan.rpg.api.event.RPGHostileDeathEvent;
import de.jan.rpg.api.item.combat.Weapon;
import de.jan.rpg.api.entity.player.RPGPlayer;
import de.jan.rpg.core.Core;
import de.jan.rpg.core.item.combat.CoreWeapon;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.TextDisplay;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Getter
public class CoreHostile implements RPGHostile {

    private final LivingEntity entity;
    private final String displayName;

    private int level;
    private int maxLife;
    private int currentLife;

    private int minBaseDamage;
    private int maxBaseDamage;

    private boolean isDeath = false;
    private boolean canDeath = true;
    private boolean canTakeDamage = true;
    private Weapon weapon;

    private RPGLivingEntity lastDamager;

    public CoreHostile(Entity entity, String displayName, int level, int minBaseDamage, int maxBaseDamage, CoreWeapon coreWeapon) {
        this.entity = (LivingEntity) entity;
        this.displayName = displayName;
        this.level = level;
        this.maxLife = 50 * level;
        this.currentLife = maxLife;
        this.minBaseDamage = minBaseDamage;
        this.maxBaseDamage = maxBaseDamage;
        this.weapon = coreWeapon;
        entity.setCustomNameVisible(true);
        entity.customName(displayName());
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public void setLevel(int value) {
        level = value;
    }

    @Override
    public void addLevel(int value) {
        setLevel(level + value);
    }

    @Override
    public Component displayName() {
        return ComponentSerializer.deserialize("<gray>" + this.displayName + " [" + level + "]" +" - <red>" + currentLife + "/" + maxLife + "❤");
    }

    @Override
    public Location getLocation() {
        return entity.getLocation();
    }

    @Override
    public RPGEntityType getRPGEntityType() {
        return RPGEntityType.RPG_HOSTILE;
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
    public int getMaxLife() {
        return maxLife;
    }

    @Override
    public void setMaxLife(int value) {
        if(value < 0) throw new IllegalArgumentException("maxLife value: " + value + " can not be negative");
        maxLife = value;
    }

    @Override
    public void addMaxLife(int value) {
        setMaxLife(maxLife + value);
    }

    @Override
    public int getCurrentLife() {
        return currentLife;
    }

    @Override
    public void setCurrentLife(int value) {
        currentLife = value;
        entity.customName(displayName());
    }

    @Override
    public void addCurrentLife(int value) {
        setCurrentLife(currentLife + value);
    }

    @Override
    public int getArmor() {
        return 0;
    }

    @Override
    public void setArmor(int value) {

    }

    @Override
    public void addArmor(int value) {

    }

    @Override
    public Weapon getWeapon() {
        return weapon;
    }

    @Override
    public void setWeapon(Weapon weapon) {
        this.weapon = weapon;
    }

    @Override
    public void setMinBaseDamage(int value) {
        if(value < 0) throw new IllegalArgumentException("minBaseDamage value: " + value + " can not be negative");
        minBaseDamage = value;
    }

    @Override
    public void addMinBaseDamage(int value) {
        setMaxBaseDamage(minBaseDamage + value);
    }

    @Override
    public void setMaxBaseDamage(int value) {
        if(value < 0) throw new IllegalArgumentException("maxBaseDamage value: " + value + " can not be negative");
        maxBaseDamage = value;
    }

    @Override
    public void addMaxBaseDamage(int value) {
        setMaxBaseDamage(maxLife + value);
    }

    @Override
    public int getTotalDamage() {
        Random random = new Random();
        int damageValue = random.nextInt(maxBaseDamage - minBaseDamage + 1) + minBaseDamage;
        if(weapon == null) return damageValue;
        damageValue += weapon.getDamage();
        return damageValue;
    }

    @Override
    public void damageByPlayer(@NotNull RPGPlayer rpgPlayer, @NotNull Weapon weapon) {
        if(!canTakeDamage) return;
        lastDamager = rpgPlayer;
        int damage = weapon.getDamage();

        runDamageIndicator(damage, weapon.getType().getIcon(), weapon.isCriticalHit());

        if(damage >= currentLife) {
            setCurrentLife(0);
            death(DeathReason.KILL_BY_PLAYER);
            return;
        }
        setCurrentLife(currentLife-damage);
        Bukkit.getPluginManager().callEvent(new RPGHostileDamageByPlayerEvent(this, rpgPlayer, damage));
        setInvulnerable(10);
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
        if(!canDeath || isDeath) return;
        isDeath = true;
        entity.setHealth(0);
        Bukkit.getPluginManager().callEvent(new RPGHostileDeathEvent(this, deathReason));
    }

    @Override
    public RPGLivingEntity getLastDamager() {
        return lastDamager;
    }

    private void runDamageIndicator(int damage, String icon, boolean criticalHit) {
        if(!canTakeDamage) return;
        Location location = entity.getLocation();
        double randomXOffset = ThreadLocalRandom.current().nextDouble(-0.8, 0.8);
        double randomYOffset = ThreadLocalRandom.current().nextDouble(-0.8, 0.8);
        double randomZOffset = ThreadLocalRandom.current().nextDouble(-0.8, 0.8);
        Location randomLocation = location.clone().add(randomXOffset, randomYOffset, randomZOffset);

        TextDisplay textDisplay = (TextDisplay) randomLocation.getWorld().spawnEntity(randomLocation.clone().add(0, 1, 0), EntityType.TEXT_DISPLAY);

        Component text;
        if(criticalHit) text = ComponentSerializer.deserialize("<gold><bold>" + damage + " <gray>" + icon);
        else text = ComponentSerializer.deserialize("<red>" + damage + " <gray>" + icon);
        textDisplay.customName(text);

        textDisplay.setBillboard(TextDisplay.Billboard.CENTER);
        textDisplay.setShadowed(true);
        textDisplay.setSeeThrough(true);
        textDisplay.setCustomNameVisible(true);

        new BukkitRunnable() {
            int ticks = 0;
            final double movementStep = 0.02;
            final int lifetimeTicks = 15;

            @Override
            public void run() {
                if(ticks >= lifetimeTicks) {
                    textDisplay.remove();
                    cancel();
                    return;
                }

                Location loc = textDisplay.getLocation();
                loc.add(0, movementStep, 0);
                textDisplay.teleport(loc);

                ticks++;
            }
        }.runTaskTimer(Core.instance, 0, 1);
    }

    private void setInvulnerable(int delayInTicks) {
        canTakeDamage = false;
        Bukkit.getScheduler().runTaskLaterAsynchronously(Core.instance, () -> canTakeDamage = true, delayInTicks);
    }
}
