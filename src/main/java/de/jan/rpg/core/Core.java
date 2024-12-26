package de.jan.rpg.core;

import de.jan.rpg.core.command.CoreCommand;
import de.jan.rpg.core.database.CoreDataBase;
import de.jan.rpg.core.event.PlayerConnectionEvent;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

@Getter
public final class Core extends JavaPlugin {

    public static final Logger LOGGER = LoggerFactory.getLogger("Core");
    public static Core instance;

    public static boolean offlineMode;

    private APIImpl coreAPI;
    private CoreDataBase coreDataBase;

    @Override
    public void onEnable() {
        // Plugin startup logic
        long start = System.currentTimeMillis();
        instance = this;
        coreDataBase = new CoreDataBase();
        coreAPI = new APIImpl(this);
        coreAPI.getCorePlayerManager().loadAllCorePlayer();
        registerListener(Bukkit.getPluginManager());
        registerCommands();
        long requiredTime = System.currentTimeMillis() - start;
        LOGGER.info("Core has been successfully loaded in {}ms", requiredTime);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        coreDataBase.refresh(coreAPI.getCorePlayerManager());
        coreDataBase.disconnect();
    }

    private void registerListener(PluginManager pluginManager) {
        pluginManager.registerEvents(new PlayerConnectionEvent(coreAPI), this);
    }

    private void registerCommands() {
        Objects.requireNonNull(getCommand("core")).setExecutor(new CoreCommand(coreAPI));
    }
}
