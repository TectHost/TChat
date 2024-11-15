package utils;

import minealex.tchat.TChat;
import org.bukkit.command.*;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public class RegisterCommands {

    private final TChat plugin;

    public RegisterCommands(TChat plugin) {
        this.plugin = plugin;
    }

    public void registerCommand(String commandName, CommandExecutor executor) {
        try {
            Field commandMapField = plugin.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            CommandMap commandMap = (CommandMap) commandMapField.get(plugin.getServer());

            Constructor<PluginCommand> constructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            constructor.setAccessible(true);
            PluginCommand command = constructor.newInstance(commandName, plugin);

            command.setExecutor(executor);
            commandMap.register(plugin.getName(), command);
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to register command '" + commandName + "'");
            e.printStackTrace();
        }
    }

    public void registerTabCompleter(String commandName, TabCompleter tabCompleter) {
        try {
            PluginCommand command = plugin.getCommand(commandName);
            if (command != null) {
                command.setTabCompleter(tabCompleter);
            } else {
                plugin.getLogger().warning("Command '" + commandName + "' not found. Unable to register tab completer.");
            }
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to register tab completer for '" + commandName + "'");
            e.printStackTrace();
        }
    }
}