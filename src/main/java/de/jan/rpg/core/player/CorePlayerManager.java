package de.jan.rpg.core.player;

import de.jan.rpg.api.component.ComponentSerializer;
import de.jan.rpg.api.entity.player.PlayerManager;
import de.jan.rpg.api.translation.Language;
import de.jan.rpg.api.entity.player.RPGPlayer;
import de.jan.rpg.core.Core;
import de.jan.rpg.core.database.CoreDataBase;
import de.jan.rpg.core.item.CoreItemManager;
import de.jan.rpg.core.item.combat.CoreArmor;
import de.jan.rpg.core.scoreboard.Scoreboard;
import lombok.Getter;
import lombok.Synchronized;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Getter
public class CorePlayerManager implements PlayerManager {

    private final Map<UUID, CorePlayer> playerMap;
    private final CoreDataBase dataBase;

    public CorePlayerManager(CoreDataBase dataBase) {
        this.dataBase = dataBase;
        this.playerMap =  new HashMap<>();
        dataBase.createTable("corePlayer", "uuid VARCHAR(100), language VARCHAR(100), firstJoinDate VARCHAR(100), totalJoin INT, souls INT, level INT, xp INT");
        loadPlayerWhoHasBeenStayAfterReloadTheServer();
        runUpdateTask();
    }

    @Override
    public RPGPlayer getRPGPlayer(UUID uuid) {
        if(isRegistered(uuid)) return playerMap.get(uuid);
        return loadCorePlayer(uuid);
    }

    @Override
    public boolean isRegistered(UUID uuid) {
        return playerMap.containsKey(uuid);
    }

    public CorePlayer getCorePlayer(UUID uuid) {
        if(isRegistered(uuid)) return playerMap.get(uuid);
        CorePlayer corePlayer = loadCorePlayer(uuid);
        if(corePlayer != null) return corePlayer;
        return registerCorePlayer(uuid);
    }

    private CorePlayer loadCorePlayer(UUID uuid) {
        if(playerMap.containsKey(uuid)) return playerMap.get(uuid);
        if(Core.offlineMode) return null;
        Map<String, Object> result = dataBase.selectDataFromUUID("corePlayer", "language, firstJoinDate, totalJoin, souls, level, xp", uuid);

        if(result == null || result.isEmpty()) return null;

        String languageString = (String) result.get("language");
        Language language = Language.valueOf(languageString.toUpperCase());
        String firstJoinDate = (String) result.get("firstJoinDate");
        int totalJoin = (int) result.get("totalJoin");
        int souls = (int) result.get("souls");
        int level = (int) result.get("level");
        int xp = (int) result.get("xp");
        CorePlayer corePlayer = new CorePlayer(uuid, language, firstJoinDate, totalJoin, souls, level, xp);
        playerMap.put(uuid, corePlayer);
        return corePlayer;
    }

    private CorePlayer registerCorePlayer(UUID uuid) {
        CorePlayer corePlayer = new CorePlayer(uuid, Language.ENGLISH, LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")), 1, 1000, 0, 0);
        playerMap.put(corePlayer.getUUID(), corePlayer);
        dataBase.insertData("corePlayer", "uuid, language, firstJoinDate, totalJoin, souls, level, xp", corePlayer.getUUID(), corePlayer.getLanguage(), corePlayer.getFirstJoinDate(), corePlayer.getTotalJoin(), corePlayer.getSouls(), 0, 0);
        return corePlayer;
    }

    public void safeCorePlayer(@NotNull CorePlayer corePlayer) {
        UUID uuid = corePlayer.getUUID();
        dataBase.updateDataFromUUID("corePlayer", uuid,"language, totalJoin, souls, level, xp", corePlayer.getLanguage().name(), corePlayer.getTotalJoin(), corePlayer.getSouls(), corePlayer.getLevel(), corePlayer.getXP());
    }

    public void initializePlayer(CorePlayer corePlayer) {
        Scoreboard scoreboard = new Scoreboard(corePlayer.getPlayer());
        corePlayer.setScoreboard(scoreboard);
        scoreboard.updateTitle(ComponentSerializer.deserialize(" <bold><gradient:blue:aqua>Fallen Empire "));
        scoreboard.updateLine(0, ComponentSerializer.deserialize(" "));
        scoreboard.updateLine(1, ComponentSerializer.deserialize("Date"));
        scoreboard.updateLine(3, ComponentSerializer.deserialize(" "));
        scoreboard.updateLine(4, ComponentSerializer.deserialize("Souls"));
        scoreboard.updateLine(6, ComponentSerializer.deserialize(" "));
        scoreboard.updateLine(7, ComponentSerializer.deserialize("Language"));
        scoreboard.updateLine(9, ComponentSerializer.deserialize(" "));
        updateScoreboard(corePlayer);
    }

    public void updateScoreboard(CorePlayer corePlayer) {
        Scoreboard scoreboard = corePlayer.getScoreboard();
        if(scoreboard == null) return;
        scoreboard.updateLine(2, ComponentSerializer.deserialize(" " + currentTime()));
        scoreboard.updateLine(5, ComponentSerializer.deserialize( " " + corePlayer.getSouls()));
        scoreboard.updateLine(8, ComponentSerializer.deserialize(" " + corePlayer.getLanguage()));
    }

    private String currentTime() {
        LocalTime currentTime = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        return currentTime.format(formatter);
    }

    //maybe too much cache?
    @Synchronized
    private void loadAllCorePlayer() {
        if(Core.offlineMode) return;

        Object result = dataBase.selectData("corePlayer","uuid, language, firstJoinDate, totalJoin, souls, level, xp");
        if(result instanceof List<?>) {
            List<Map<String, Object>> rows = (List<Map<String, Object>>) result;
            for(Map<String, Object> row : rows) {
                UUID uuid = UUID.fromString((String) row.get("uuid"));
                String languageString = (String) row.get("language");
                Language language = Language.valueOf(languageString.toUpperCase());
                String firstJoinDate = (String) row.get("firstJoinDate");
                int totalJoin = (int) row.get("totalJoin");
                int souls = (int) row.get("souls");
                int level = (int) row.get("level");
                int xp = (int) row.get("xp");
                playerMap.put(uuid, new CorePlayer(uuid, language, firstJoinDate, totalJoin, souls, level, xp));
            }
            Core.LOGGER.info("Total loaded corePlayer: {}", playerMap.size());
        } else {
            Core.LOGGER.info("No data found");
        }
    }

    //deleteMe
    private void loadPlayerWhoHasBeenStayAfterReloadTheServer() {
        Bukkit.getScheduler().runTaskLater(Core.instance, () -> Bukkit.getOnlinePlayers().forEach(player -> {
            CorePlayer corePlayer = getCorePlayer(player.getUniqueId());
            ItemStack[] content = player.getInventory().getArmorContents();
            Arrays.stream(content).filter(itemStack -> itemStack != null && !itemStack.getType().isAir()).forEach(itemStack -> {
                CoreItemManager coreItemManager = Core.instance.getCoreAPI().getCoreItemManager();
                CoreArmor armor = coreItemManager.getCoreArmor(itemStack);
                if(armor != null) {
                    corePlayer.addArmor(armor.getArmorValue());
                    corePlayer.addMaxLife(armor.getExtraLife());
                }
                initializePlayer(corePlayer);
            });
        }), 20);
    }

    //run actionbar
    private void runUpdateTask() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(Core.instance, () -> {
            if(playerMap.isEmpty()) return;
            playerMap.values().stream().filter(corePlayer -> corePlayer.getPlayer() != null && corePlayer.getPlayer().isOnline()).forEach(corePlayer -> {
                corePlayer.sendActionbar();
                corePlayer.regeneration();
                updateScoreboard(corePlayer);
            });
        }, 0, 20);
    }
}
