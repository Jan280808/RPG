package de.jan.rpg.core.enemy;

import de.jan.rpg.api.entity.EntityManager;
import de.jan.rpg.api.entity.RPGLivingEntity;
import lombok.Getter;
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

    @Override
    public RPGLivingEntity spawn(@NotNull Location location, @NotNull EntityType entityType) {
        Entity entity = location.getWorld().spawnEntity(location, entityType);
        CoreHostile coreHostile = new CoreHostile(1, entity);
        entityMap.put(entity, coreHostile);
        return coreHostile;
    }

    @Override
    public void remove(@NotNull RPGLivingEntity livingEntity) {
        Entity entity = livingEntity.getEntity();
        if(entity == null) return;
        if(!entityMap.containsKey(entity)) return;
        entityMap.remove(entity);
        entity.remove();
    }

    @Override
    public void remove(@NotNull Entity entity) {
        if(!entityMap.containsKey(entity)) return;
        entityMap.remove(entity);
        entity.remove();
    }

    public CoreHostile getCoreHostile(Entity entity) {
        if(!entityMap.containsKey(entity)) return null;
        return entityMap.get(entity);
    }
}
