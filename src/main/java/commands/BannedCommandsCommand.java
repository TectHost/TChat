package commands;

import config.BannedCommandsManager;
import minealex.tchat.TChat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BannedCommandsCommand implements CommandExecutor {

    private final BannedCommandsManager bannedCommandsManager;
    private final TChat plugin;

    public BannedCommandsCommand(BannedCommandsManager bannedCommandsManager, TChat plugin) {
        this.bannedCommandsManager = bannedCommandsManager;
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        String prefix = plugin.getMessagesManager().getPrefix();
        if (!sender.hasPermission("tchat.admin") && !sender.hasPermission("tchat.admin.bannedcommands")) {
            String message = plugin.getMessagesManager().getNoPermission();
            sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + message));
            return true;
        }

        if (args.length == 0) {
            String message = plugin.getMessagesManager().getUsageBannedCommands();
            sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + message));
            return true;
        }

        String subCommand = args[0];

        switch (subCommand.toLowerCase()) {
            case "list":
                handleListCommand(sender, args);
                break;
            case "add":
                handleAddCommand(sender, args);
                break;
            case "remove":
                handleRemoveCommand(sender, args);
                break;
            default:
                String message = plugin.getMessagesManager().getUsageBannedCommands();
                sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + message));
                break;
        }

        return true;
    }

    private void handleListCommand(CommandSender sender, String[] args) {
        String prefix = plugin.getMessagesManager().getPrefix();
        String message = plugin.getMessagesManager().getUsageBannedCommandsList();
        if (args.length < 2) {
            sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + message));
            return;
        }

        String listType = args[1];

        switch (listType.toLowerCase()) {
            case "command":
                listBlockedCommands(sender);
                break;
            case "tab":
                listNoTabCompleteCommands(sender);
                break;
            default:
                sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + message));
                break;
        }
    }

    private void listBlockedCommands(CommandSender sender) {
        String prefix = plugin.getMessagesManager().getPrefix();
        FileConfiguration config = bannedCommandsManager.getConfigFile().getConfig();
        if (config.getStringList("bannedCommands").isEmpty()) {
            String message = plugin.getMessagesManager().getBannedCommandsNoCommandsBlocked();
            sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + message));
            return;
        }

        String message = plugin.getMessagesManager().getBannedCommandsBlockedList();
        sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + message));
        for (String command : config.getStringList("bannedCommands")) {
            sender.sendMessage("- " + command);
        }
    }

    private void listNoTabCompleteCommands(CommandSender sender) {
        String prefix = plugin.getMessagesManager().getPrefix();
        FileConfiguration config = bannedCommandsManager.getConfigFile().getConfig();
        if (config.getStringList("tab.noTabCompleteCommands").isEmpty()) {
            String message = plugin.getMessagesManager().getBannedCommandsNoTabBlocked();
            sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + message));
            return;
        }

        String message = plugin.getMessagesManager().getBannedCommandsBlockedTabList();
        sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + message));
        for (String command : config.getStringList("tab.noTabCompleteCommands")) {
            sender.sendMessage("- " + command);
        }
    }

    private void handleAddCommand(CommandSender sender, String[] args) {
        String prefix = plugin.getMessagesManager().getPrefix();
        String message = plugin.getMessagesManager().getUsageBannedCommandsAdd();
        if (args.length < 3) {
            sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + message));
            return;
        }

        String addType = args[1];
        String commandToAdd = args[2];

        switch (addType.toLowerCase()) {
            case "command":
                addBlockedCommand(sender, commandToAdd);
                break;
            case "tab":
                addNoTabCompleteCommand(sender, commandToAdd);
                break;
            default:
                sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + message));
                break;
        }
    }

    private void addBlockedCommand(CommandSender sender, String command) {
        FileConfiguration config = bannedCommandsManager.getConfigFile().getConfig();
        List<String> commands = config.getStringList("bannedCommands");
        String prefix = plugin.getMessagesManager().getPrefix();

        if (commands.contains(command)) {
            String message = plugin.getMessagesManager().getBannedCommandsAlready();
            sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + message));
            return;
        }

        commands.add(command);
        config.set("bannedCommands", commands);
        bannedCommandsManager.saveConfig();
        String message = plugin.getMessagesManager().getBannedCommandsAdded();
        sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + message));
    }

    private void addNoTabCompleteCommand(CommandSender sender, String command) {
        FileConfiguration config = bannedCommandsManager.getConfigFile().getConfig();
        List<String> commands = config.getStringList("tab.noTabCompleteCommands");
        String prefix = plugin.getMessagesManager().getPrefix();

        if (commands.contains(command)) {
            String message = plugin.getMessagesManager().getBannedCommandsAlreadyTab();
            sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + message));
            return;
        }

        commands.add(command);
        config.set("tab.noTabCompleteCommands", commands);
        bannedCommandsManager.saveConfig();
        String message = plugin.getMessagesManager().getBannedCommandsAddedTab();
        sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + message));
    }

    private void handleRemoveCommand(CommandSender sender, String[] args) {
        String prefix = plugin.getMessagesManager().getPrefix();
        if (args.length < 3) {
            String message = plugin.getMessagesManager().getUsageBannedCommandsRemove();
            sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + message));
            return;
        }

        String removeType = args[1];
        String commandToRemove = args[2];

        switch (removeType.toLowerCase()) {
            case "command":
                removeBlockedCommand(sender, commandToRemove);
                break;
            case "tab":
                removeNoTabCompleteCommand(sender, commandToRemove);
                break;
            default:
                String message = plugin.getMessagesManager().getUsageBannedCommandsRemove();
                sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + message));
                break;
        }
    }

    private void removeBlockedCommand(CommandSender sender, String command) {
        FileConfiguration config = bannedCommandsManager.getConfigFile().getConfig();
        List<String> commands = config.getStringList("bannedCommands");
        String prefix = plugin.getMessagesManager().getPrefix();

        if (!commands.contains(command)) {
            String message = plugin.getMessagesManager().getBannedCommandsNotBlocked();
            sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + message));
            return;
        }

        commands.remove(command);
        config.set("bannedCommands", commands);
        bannedCommandsManager.saveConfig();
        String message = plugin.getMessagesManager().getBannedCommandsRemoved();
        sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + message));
    }

    private void removeNoTabCompleteCommand(CommandSender sender, String command) {
        FileConfiguration config = bannedCommandsManager.getConfigFile().getConfig();
        List<String> commands = config.getStringList("tab.noTabCompleteCommands");
        String prefix = plugin.getMessagesManager().getPrefix();

        if (!commands.contains(command)) {
            String message = plugin.getMessagesManager().getBannedCommandsNotBlockedTab();
            sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + message));
            return;
        }

        commands.remove(command);
        config.set("tab.noTabCompleteCommands", commands);
        bannedCommandsManager.saveConfig();
        String message = plugin.getMessagesManager().getBannedCommandsRemovedTab();
        sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + message));
    }
}
