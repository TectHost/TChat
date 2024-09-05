package commands;

import config.LogsManager;
import minealex.tchat.TChat;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LogsCommand implements CommandExecutor, TabCompleter {

    private final LogsManager logsManager;
    private final TChat plugin;

    public LogsCommand(@NotNull TChat plugin) {
        this.logsManager = plugin.getLogsManager();
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        String p = plugin.getMessagesManager().getPrefix();
        if (args.length == 0) {
            String m = plugin.getMessagesManager().getUsageLogs();
            sender.sendMessage(plugin.getTranslateColors().translateColors(null, p + m));
            return false;
        }

        if (!sender.hasPermission("tchat.admin.command.logs")) {
            String m = plugin.getMessagesManager().getNoPermission();
            sender.sendMessage(plugin.getTranslateColors().translateColors(null, p + m));
            return false;
        }

        String logType = args[0].toLowerCase();
        String playerName = args.length > 1 ? args[1] : "";
        String filter = args.length > 2 ? args[2] : "";

        File logFile = getLogFile(logType);
        if (logFile == null) {
            String m = plugin.getMessagesManager().getLogsInvalid();
            sender.sendMessage(plugin.getTranslateColors().translateColors(null, p + m));
            return false;
        }

        List<String> results = searchLogs(logFile, playerName, filter);
        if (results.isEmpty()) {
            String m = plugin.getMessagesManager().getLogsNoRegister();
            sender.sendMessage(plugin.getTranslateColors().translateColors(null, p + m));
        } else {
            String m = plugin.getMessagesManager().getLogsHeader();
            sender.sendMessage(plugin.getTranslateColors().translateColors(null, p + m));
            for (String result : results) {
                sender.sendMessage(result);
            }
        }
        return true;
    }

    private @Nullable File getLogFile(@NotNull String logType) {
        return switch (logType) {
            case "chat" -> logsManager.getChatLogFile();
            case "bannedwords" -> logsManager.getBannedWordsFile();
            case "commands" -> logsManager.getCommandLogFile();
            case "bannedcommands" -> logsManager.getBannedCommandsFile();
            case "ignore" -> logsManager.getIgnoreFile();
            case "antiadver" -> logsManager.getAntiAdverFile();
            case "deaths" -> logsManager.getDeathFile();
            default -> null;
        };
    }

    private @NotNull List<String> searchLogs(File logFile, String playerName, String filter) {
        List<String> results = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (playerName.isEmpty() || line.contains(playerName)) {
                    if (filter.isEmpty() || line.contains(filter)) {
                        results.add(line);
                    }
                }
            }
        } catch (IOException e) {
            Bukkit.getLogger().severe("Error reading the log file: " + e.getMessage());
        }
        return results;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String @NotNull [] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.add("chat");
            completions.add("bannedwords");
            completions.add("commands");
            completions.add("bannedcommands");
            completions.add("ignore");
            completions.add("antiadver");
            completions.add("deaths");
        } else if (args.length == 2) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                completions.add(player.getName());
            }
        }
        return completions;
    }
}
