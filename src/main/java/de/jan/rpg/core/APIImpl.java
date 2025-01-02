package de.jan.rpg.core;

import de.jan.rpg.api.CoreAPI;
import de.jan.rpg.api.Unsafe;
import de.jan.rpg.api.dataBase.DataBase;
import de.jan.rpg.api.entity.EntityManager;
import de.jan.rpg.api.item.ItemManager;
import de.jan.rpg.api.entity.player.PlayerManager;
import de.jan.rpg.api.translation.Translation;
import de.jan.rpg.core.database.CoreDataBase;
import de.jan.rpg.core.enemy.CoreEntityManager;
import de.jan.rpg.core.item.CoreItemManager;
import de.jan.rpg.core.player.CorePlayerManager;
import de.jan.rpg.core.translation.CoreTranslation;
import lombok.Getter;

@Getter
public class APIImpl implements CoreAPI {

    private final Core core;
    private final CoreDataBase coreDataBase;
    private final CoreTranslation coreTranslation;
    private final CorePlayerManager corePlayerManager;
    private final CoreEntityManager coreEntityManager;
    private final CoreItemManager coreItemManager;

    public APIImpl(Core core) {
        this.core = core;
        Unsafe.setAPI(this);
        this.coreDataBase = core.getCoreDataBase();
        this.coreTranslation = new CoreTranslation();
        this.corePlayerManager = new CorePlayerManager(core.getCoreDataBase());
        this.coreEntityManager = new CoreEntityManager();
        this.coreItemManager = new CoreItemManager(this);
        Core.LOGGER.info("CoreAPI has been initialized successfully");
    }

    @Override
    public PlayerManager getPlayerManager() {
        return corePlayerManager;
    }

    @Override
    public DataBase getDataBase() {
        return coreDataBase;
    }

    @Override
    public Translation getTranslation() {
        return coreTranslation;
    }

    @Override
    public EntityManager getEntityManager() {
        return coreEntityManager;
    }

    @Override
    public ItemManager getItemManager() {
        return coreItemManager;
    }
}
