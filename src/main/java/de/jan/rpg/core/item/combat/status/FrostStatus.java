package de.jan.rpg.core.item.combat.status;

import de.jan.rpg.api.item.combat.Status;
import de.jan.rpg.core.Core;
import de.jan.rpg.core.enemy.CoreHostile;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;

public class FrostStatus implements Status {

    private final CoreHostile coreHostile;
    private final int duration;
    private int seconds;
    private int resistanceValue;
    private int currentValue;

    private boolean isRunning = false;
    private int taskID;

    public FrostStatus(CoreHostile coreHostile, int resistanceValue, int currentValue, int duration) {
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
        if(isRunning) return;
        currentValue = value;
        halfIndicator();
        if(currentValue >= resistanceValue) {
            start();
            currentValue = resistanceValue;
        }
    }

    @Override
    public void addCurrentValue(int value) {
        setCurrentValue(currentValue + value);
    }

    @Override
    public Type getType() {
        return Type.FROST;
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
        int hafResistance = resistanceValue / 2;
        if(currentValue < hafResistance) return;
        spawnParticle(1);
    }

    @Override
    public void start() {
        if(isRunning) return;
        isRunning = true;
        seconds = duration;
        
        coreHostile.getEntity().setFreezeTicks(20*seconds);
        taskID = Bukkit.getScheduler().runTaskTimer(Core.instance, () -> {
            if(seconds == 0) {
                stop();
                return;
            }
            int currentLife = coreHostile.getCurrentLife();
            coreHostile.setCurrentLife(currentLife - 5);
            spawnParticle(20);
            seconds--;
        }, 0, 20).getTaskId();
    }

    @Override
    public void stop() {
        if(!isRunning) return;
        isRunning = false;
        Bukkit.getScheduler().cancelTask(taskID);
        setCurrentValue(0);
    }

    private void spawnParticle(int count) {
        Location location = coreHostile.getLocation();
        location.getWorld().spawnParticle(Particle.SNOWFLAKE, location.clone().add(0, 1.8, 0), count, 0.5, 0.5, 0.5, 0.05);
        location.getWorld().playSound(location, Sound.ENTITY_PLAYER_HURT_FREEZE, 1, 1);
    }
}
