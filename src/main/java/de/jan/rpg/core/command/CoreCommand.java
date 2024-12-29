package de.jan.rpg.core.command;

import de.jan.rpg.core.APIImpl;
import de.jan.rpg.core.Core;
import de.jan.rpg.core.command.sub.SubCommands;
import de.jan.rpg.core.player.CorePlayer;
import de.jan.rpg.core.player.CorePlayerManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CoreCommand implements TabExecutor {

    private final APIImpl api;
    private final CorePlayerManager corePlayerManager;
    private final Map<String, CoreCommands> subCommandMap;

    public CoreCommand(APIImpl api) {
        this.api = api;
        this.corePlayerManager = api.getCorePlayerManager();
        this.subCommandMap = new HashMap<>();
        registerSubCommands();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender instanceof ConsoleCommandSender) return false;
        Player player = (Player) sender;
        CorePlayer corePlayer = corePlayerManager.getCorePlayer(player.getUniqueId());
        if(!player.hasPermission("")) return false;

        if(args.length == 0) {
            sendInformation(corePlayer);
            return false;
        }
        String string = args[0].toLowerCase();
        CoreCommands coreCommand = subCommandMap.get(string);
        if(coreCommand == null) return false;
        coreCommand.onCommand(api, player, args);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender instanceof ConsoleCommandSender) return null;
        Player player = (Player) sender;
        if(!player.hasPermission("")) return null;
        if(args.length == 1) return subCommandMap.keySet().stream().toList();
        if(args.length == 2) {
            CoreCommands coreCommand = subCommandMap.get(args[0]);
            if(coreCommand.subCommands() != null) return coreCommand.subCommands();
        }
        return List.of();
    }

    private void registerSubCommands() {
        Arrays.stream(SubCommands.values()).forEach(subCommands -> subCommandMap.put(subCommands.getSubCommand(), subCommands.getCommands()));
    }

    private void sendInformation(CorePlayer corePlayer) {
        corePlayer.sendMessage(" ");
        corePlayer.sendMessage("<gradient:aqua:blue>CoreSystem");
        corePlayer.sendMessage(" ");
        corePlayer.sendMessage("  <dark_gray>● <gray>Version: " + Core.instance.getPluginMeta().getVersion());
        corePlayer.sendMessage("  <dark_gray>● <gray>OfflineMode: " + Core.offlineMode);
        corePlayer.sendMessage("  <dark_gray>● <gray>DataBaseConnection: " + api.getCoreDataBase().isConnected());
        corePlayer.sendMessage("  <dark_gray>● <gray>CachedCorePlayer: " + corePlayerManager.getPlayerMap().size());
        corePlayer.sendMessage("  <dark_gray>● <gray>translation: " + api.getCoreTranslation().getTranslationMap().values().size());
        corePlayer.sendMessage(" ");
        corePlayer.sendMessage("<gradient:aqua:blue>CoreSystem");
        corePlayer.sendMessage(" ");
    }
}
