package de.jan.rpg.core.item.combat.status;

import de.jan.rpg.api.item.combat.Status;
import de.jan.rpg.core.Core;
import de.jan.rpg.core.enemy.CoreHostile;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;

public class HolyStatus implements Status {

    private final CoreHostile coreHostile;
    private final int duration;
    private int resistanceValue;
    private int currentValue;

    private boolean isRunning = false;
    private int taskID;
    private int indicatorTask;

    public HolyStatus(CoreHostile coreHostile, int resistanceValue, int currentValue, int duration) {
        this.coreHostile = coreHostile;
        this.resistanceValue = resistanceValue;
        this.currentValue = currentValue;
        this.duration = duration;
    }

    @Override
    public int getResistanceValue() {
        return resistanceValue;
    }

    @Override
    public void setResistanceValue(int value) {
        resistanceValue = value;
    }

    @Override
    public void addResistanceValue(int value) {
        setResistanceValue(resistanceValue + value);
    }

    @Override
    public int getCurrentValue() {
        return currentValue;
    }

    @Override
    public void setCurrentValue(int value) {
        currentValue = value;
        start();
    }

    @Override
    public void addCurrentValue(int value) {
        setCurrentValue(currentValue + value);
    }

    @Override
    public Type getType() {
        return Type.HOLY;
    }

    @Override
    public int getDuration() {
        return duration;
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public void halfIndicator() {
        double ratio = (double) currentValue / resistanceValue;
        int stadium = (int) (ratio * 4);
        if(stadium >= 1 && stadium <= 4) {
            spawnParticleCircle(stadium);
        }
    }

    @Override
    public void start() {
        if(isRunning) return;
        isRunning = true;

        taskID = Bukkit.getScheduler().runTaskTimer(Core.instance, () -> {
            if(currentValue <= 0) {
                stop();
                return;
            }

            if(currentValue >= resistanceValue) {
                int currentLife = coreHostile.getCurrentLife();
                coreHostile.getLocation().createExplosion(2, false, false);
                coreHostile.getLocation().getWorld().spawnEntity(coreHostile.getLocation(), EntityType.LIGHTNING_BOLT);
                coreHostile.setCurrentLife(currentLife - 50);
                stop();
            }

            currentValue--;
        }, 0, 20).getTaskId();

        indicatorTask = Bukkit.getScheduler().runTaskTimerAsynchronously(Core.instance, () -> {
            double ratio = (double) currentValue / resistanceValue;
            int stadium = (int) (ratio * 4);
            if(stadium >= 1 && stadium <= 4) spawnParticleCircle(stadium);
        }, 0, 1).getTaskId();
    }

    @Override
    public void stop() {
        if(!isRunning) return;
        isRunning = false;
        Bukkit.getScheduler().cancelTask(taskID);
        Bukkit.getScheduler().cancelTask(indicatorTask);
        currentValue = 0;
    }

    private void spawnParticleCircle(int stadium) {
        Location center = coreHostile.getLocation();
        double radius = 2.0;
        int points = 100;
        double angleIncrement = 2 * Math.PI / points;
        int visiblePoints = points * stadium / 4;

        for (int i = 0; i < visiblePoints; i++) {
            double angle = i * angleIncrement;
            double x = radius * Math.cos(angle);
            double z = radius * Math.sin(angle);
            Location particleLocation = center.clone().add(x, 0.1, z);
            center.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, particleLocation.add(new Vector(0, 0, 0)), 3, 0, 0, 0, 0);
        }
    }
}
