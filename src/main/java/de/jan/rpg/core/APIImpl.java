package de.jan.rpg.core;

import de.jan.rpg.api.CoreAPI;
import de.jan.rpg.api.Unsafe;
import de.jan.rpg.api.player.PlayerManager;
import de.jan.rpg.core.player.CorePlayerManager;
import lombok.Getter;

@Getter
public class APIImpl implements CoreAPI {

    private final CorePlayerManager corePlayerManager;

    public APIImpl() {
        Unsafe.setAPI(this);
        this.corePlayerManager = new CorePlayerManager();
        Core.LOGGER.info("CoreAPI has been initialized successfully");
    }

    @Override
    public PlayerManager getPlayerManager() {
        return corePlayerManager;
    }
}
