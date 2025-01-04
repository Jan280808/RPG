package de.jan.rpg.core.enemy;

import de.jan.rpg.api.entity.EntityManager;
import de.jan.rpg.api.entity.RPGLivingEntity;
import de.jan.rpg.core.Core;
import de.jan.rpg.core.item.combat.status.*;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@Getter
public class CoreEntityManager implements EntityManager {

    private final Map<Entity, CoreHostile> entityMap;

    public CoreEntityManager() {
        this.entityMap = new HashMap<>();
    }

    public void spawnDummy(Location location) {
        Entity entity = location.getWorld().spawnEntity(location, EntityType.ILLUSIONER);
        CoreHostile coreHostile = new CoreHostile(entity, "Dummy", 100, 5, 10, null, 1, 1);
        coreHostile.getEntity().setAI(false);
        coreHostile.canDeath(false);
        entityMap.put(entity, coreHostile);
    }

    @Override
    public RPGLivingEntity spawn(@NotNull Location location, @NotNull EntityType entityType) {
        Entity entity = location.getWorld().spawnEntity(location, entityType);
        CoreHostile coreHostile = new CoreHostile(entity, "Undead", 10, 5, 10, null, 1, 1);
        coreHostile.addStatus(new FireStatus(coreHostile, 50, 0, 3));
        coreHostile.addStatus(new FrostStatus(coreHostile, 25, 0, 3));
        coreHostile.addStatus(new PosionStatus(coreHostile, 50, 0, 3));
        coreHostile.addStatus(new BleedingStatus(coreHostile, 50, 0, 3));
        coreHostile.addStatus(new HolyStatus(coreHostile, 100, 0, 3));
        entityMap.put(entity, coreHostile);
        return coreHostile;
    }

    @Override
    public void remove(@NotNull Entity entity) {
        if(!entityMap.containsKey(entity)) return;
        entityMap.remove(entity);
        Bukkit.getScheduler().scheduleSyncDelayedTask(Core.instance, entity::remove, 10);
    }

    public CoreHostile getCoreHostile(Entity entity) {
        if(!entityMap.containsKey(entity)) return null;
        return entityMap.get(entity);
    }
}
