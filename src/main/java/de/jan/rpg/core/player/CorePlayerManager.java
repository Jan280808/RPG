package de.jan.rpg.core.player;

import de.jan.rpg.api.translation.Language;
import de.jan.rpg.api.player.RPGPlayer;
import de.jan.rpg.core.Core;
import de.jan.rpg.core.database.CoreDataBase;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
public class CorePlayerManager implements de.jan.rpg.api.player.PlayerManager {

    private final Map<UUID, CorePlayer> playerMap;
    private final CoreDataBase dataBase;

    public CorePlayerManager(CoreDataBase dataBase) {
        this.dataBase = dataBase;
        this.playerMap =  new HashMap<>();
    }

    public CorePlayer getCorePlayer(UUID uuid) {
        if(isRegistered(uuid)) return playerMap.get(uuid);
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        CorePlayer corePlayer = new CorePlayer(uuid, Language.ENGLISH, date, 1, 1000);
        register(corePlayer);
        playerMap.put(uuid, corePlayer);
        return corePlayer;
    }

    @Override
    public RPGPlayer getRPGPlayer(UUID uuid) {
        return getCorePlayer(uuid);
    }

    @Override
    public boolean isRegistered(UUID uuid) {
        return playerMap.containsKey(uuid);
    }

    private void register(CorePlayer corePlayer) {
        String columns = "uuid, language, firstJoinDate, totalJoin, coins";
        dataBase.insertData("corePlayer", columns, corePlayer.getUUID(), corePlayer.getLanguage(), corePlayer.getFirstJoinDate(), corePlayer.getTotalJoin(), corePlayer.getCoins());
    }

    //maybe too much cache?
    public void loadAllCorePlayer() {
        if(Core.offlineMode) return;

        Object result = dataBase.selectData("corePlayer","uuid, language, firstJoinDate, totalJoin, coins");
        if(result instanceof List<?>) {
            List<Map<String, Object>> rows = (List<Map<String, Object>>) result;

            for(Map<String, Object> row : rows) {
                UUID uuid = UUID.fromString((String) row.get("uuid"));
                String languageString = (String) row.get("language");
                Language language = Language.valueOf(languageString.toUpperCase());
                String firstJoinDate = (String) row.get("firstJoinDate");
                int totalJoin = (int) row.get("totalJoin");
                int coins = (int) row.get("coins");
                playerMap.put(uuid, new CorePlayer(uuid, language, firstJoinDate, totalJoin, coins));
            }
            Core.LOGGER.info("Total loaded corePlayer: {}", playerMap.size());
        } else {
            Core.LOGGER.info("No data found");
        }
    }
}
