package de.jan.rpg.core.enemy;

import de.jan.rpg.api.component.ComponentSerializer;
import de.jan.rpg.api.entity.RPGLivingEntity;
import de.jan.rpg.api.player.RPGPlayer;
import de.jan.rpg.core.Core;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.TextDisplay;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class CoreHostile implements RPGLivingEntity {

    private final int level;
    private final LivingEntity entity;

    private int maxLife;
    private int currentLife;
    private int armor;

    private boolean isDeath = false;
    private boolean canDeath = true;
    private boolean canTakeDamage = true;

    public CoreHostile(int level, Entity entity) {
        this.level = level;
        this.entity = (LivingEntity) entity;
        this.maxLife = 10 * level;
        this.currentLife = maxLife;
        setup();
    }

    @Override
    public Component displayName() {
        return ComponentSerializer.deserialize("<gray>" + entity.getType().name() + " [" + level + "]" +" - <red>" + currentLife + "/" + maxLife + "❤");
    }

    @Override
    public Entity getEntity() {
        return entity;
    }

    @Override
    public int level() {
        return level;
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
    public int getMaxLife() {
        return maxLife;
    }

    @Override
    public void setMaxLife(int value) {
        maxLife = value;
    }

    @Override
    public int getCurrentLife() {
        return currentLife;
    }

    @Override
    public void setCurrentLife(int value) {
        currentLife = value;

        //update displayingName
        entity.customName(displayName());
    }

    @Override
    public void addCurrentLife(int value) {
        setCurrentLife(currentLife + value);
    }

    @Override
    public void damage(int value, RPGPlayer rpgPlayer) {
        if(!canTakeDamage) return;

        runDamageIndicator(value, rpgPlayer);

        if(value >= currentLife) {
            setCurrentLife(0);
            death();
            return;
        }
        setCurrentLife(currentLife-value);
    }

    @Override
    public void deathByPlayer(@NotNull RPGPlayer killer) {
        death();
        Location targetLocation = entity.getLocation();
        for(int i = 0; i < 2; i++) {
            int delay = 20 + (i * 5);
            Bukkit.getScheduler().scheduleSyncDelayedTask(Core.instance, () -> targetLocation.getWorld().spawnParticle(Particle.VIBRATION, targetLocation.clone().add(0, 1, 0), 1, new Vibration(entity.getLocation().clone().subtract(0, 1, 0), new Vibration.Destination.EntityDestination(killer.getPlayer()), 20)), delay);
        }
    }

    @Override
    public void death() {
        if(isDeath) return;
        isDeath = true;

        //kill the spigot entity
        entity.setHealth(0);

        //spawn soul particle
        Location location = entity.getLocation().add(0, 2, 0).add(new Vector(0, 0, 0));
        location.getWorld().spawnParticle(Particle.SCULK_SOUL, location, 1, 0, 0, 0, 0);
    }

    @Override
    public int getArmor() {
        return armor;
    }

    @Override
    public void setArmor(int value) {
        armor = value;
    }

    @Override
    public void addArmor(int value) {
        armor = value;
    }

    @Override
    public void removeArmor(int value) {
        if(value >= armor) {
            armor = 0;
            return;
        }
        armor -= value;
    }

    private void setup() {
        entity.setCustomNameVisible(true);
        entity.customName(displayName());
    }

    private void runDamageIndicator(int damage, RPGPlayer damager) {
        if(!canTakeDamage) return;
        Location location = entity.getLocation();
        TextDisplay textDisplay = (TextDisplay) location.getWorld().spawnEntity(location.clone().add(0, 1, 0), EntityType.TEXT_DISPLAY);

        Component text = ComponentSerializer.deserialize("<red>" + damage);
        if(damager != null) text = text.append(ComponentSerializer.deserialize(" <gray>[" + damager.getPlayer().getName() + "]"));
        textDisplay.customName(text);

        textDisplay.setBillboard(TextDisplay.Billboard.CENTER);
        textDisplay.setShadowed(true);
        textDisplay.setSeeThrough(true);
        textDisplay.setCustomNameVisible(true);

        new BukkitRunnable() {
            int ticks = 0;
            final double movementStep = 0.02;
            final int lifetimeTicks = 30;

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
}
