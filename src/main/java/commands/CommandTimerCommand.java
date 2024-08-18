package commands;

import minealex.tchat.TChat;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class CommandTimerCommand implements CommandExecutor {

    private final TChat plugin;

    public CommandTimerCommand(TChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        String prefix = plugin.getMessagesManager().getPrefix();
        if (!(sender instanceof Player player)) {
            String message = plugin.getMessagesManager().getNoPlayer();
            sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + message));
            return true;
        }

        if (args.length < 2) {
            String message = plugin.getMessagesManager().getUsageCommandTimer();
            player.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
            return true;
        }

        if (args[0].equalsIgnoreCase("create")) {
            return handleCreateCommand(player, args);
        } else if (args[0].equalsIgnoreCase("remove")) {
            return handleRemoveCommand(player, args);
        } else {
            String message = plugin.getMessagesManager().getUsageCommandTimer();
            player.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
            return true;
        }
    }

    private boolean handleCreateCommand(Player player, String[] args) {
        String prefix = plugin.getMessagesManager().getPrefix();
        if (args.length < 4) {
            String message = plugin.getMessagesManager().getUsageCommandTimerAdd();
            player.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
            return true;
        }

        String name = args[1];

        int time;
        try {
            time = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            String message = plugin.getMessagesManager().getCommandTimerInvalidNumer();
            player.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
            return true;
        }

        String[] commands = String.join(" ", Arrays.copyOfRange(args, 3, args.length)).split("\\|");
        List<String> commandList = Arrays.stream(commands).map(String::trim).toList();

        if (plugin.getCommandTimerManager().addCommandTimer(name, time, commandList)) {
            String message = plugin.getMessagesManager().getCommandTimerAdded();
            message = message.replace("%timer%", name);
            player.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
        } else {
            String message = plugin.getMessagesManager().getCommandTimerAlreadyAdded();
            player.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
        }

        return true;
    }

    private boolean handleRemoveCommand(Player player, String[] args) {
        String prefix = plugin.getMessagesManager().getPrefix();
        if (args.length < 2) {
            String message = plugin.getMessagesManager().getUsageCommandTimerRemove();
            player.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
            return true;
        }

        String name = args[1];

        if (plugin.getCommandTimerManager().removeCommandTimer(name)) {
            String message = plugin.getMessagesManager().getCommandTimerRemoved();
            message = message.replace("%timer%", name);
            player.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
        } else {
            String message = plugin.getMessagesManager().getCommandTimerNotExist();
            player.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
        }

        return true;
    }
}
