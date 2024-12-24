package de.jan.rpg.core;

import de.jan.rpg.core.command.CoreCommand;
import de.jan.rpg.core.database.DataBase;
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

    private APIImpl coreAPI;
    private DataBase dataBase;

    @Override
    public void onEnable() {
        // Plugin startup logic
        double start = System.currentTimeMillis();
        instance = this;
        coreAPI = new APIImpl();
        dataBase = new DataBase();

        registerListener(Bukkit.getPluginManager());
        registerCommands();
        double requiredTime = System.currentTimeMillis() - start;
        LOGGER.info("Core has been successfully loaded in {}ms", requiredTime);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        dataBase.disconnect();
    }

    private void registerListener(PluginManager pluginManager) {
        pluginManager.registerEvents(new PlayerConnectionEvent(coreAPI), this);
    }

    private void registerCommands() {
        Objects.requireNonNull(getCommand("core")).setExecutor(new CoreCommand(coreAPI));
    }
}
