package de.jan.rpg.core.player;

import de.jan.rpg.api.player.RPGPlayer;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class CorePlayerManager implements de.jan.rpg.api.player.PlayerManager {

    private final Map<UUID, CorePlayer> playerMap;

    public CorePlayerManager() {
        this.playerMap =  new HashMap<>();
    }

    public CorePlayer getCorePlayer(UUID uuid) {
        if(isRegistered(uuid)) playerMap.get(uuid);

        //methode to register new corePlayer
        return playerMap.put(uuid, new CorePlayer(uuid));
    }

    @Override
    public RPGPlayer getRPGPlayer(UUID uuid) {
        return getCorePlayer(uuid);
    }

    @Override
    public boolean isRegistered(UUID uuid) {
        return playerMap.containsKey(uuid);
    }
}
