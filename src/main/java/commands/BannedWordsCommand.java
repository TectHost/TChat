package commands;

import config.BannedWordsManager;
import minealex.tchat.TChat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BannedWordsCommand implements CommandExecutor {

    private final BannedWordsManager bannedWordsManager;
    private final TChat plugin;

    public BannedWordsCommand(TChat plugin, BannedWordsManager bannedWordsManager) {
        this.plugin = plugin;
        this.bannedWordsManager = bannedWordsManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        String prefix = plugin.getMessagesManager().getPrefix();
        if (!(sender instanceof Player player)) {
            String message = plugin.getMessagesManager().getNoPlayer();
            sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + message));
            return true;
        }

        if (!player.hasPermission("tchat.admin.command.bannedwords")) {
            String message = plugin.getMessagesManager().getNoPermission();
            player.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
            return true;
        }

        if (args.length == 0) {
            String message = plugin.getMessagesManager().getUsageBannedWords();
            player.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
            return true;
        }

        String action = args[0].toLowerCase();

        switch (action) {
            case "list":
                List<String> bannedWords = bannedWordsManager.getBannedWords();
                if (bannedWords.isEmpty()) {
                    String message = plugin.getMessagesManager().getBannedWordsNone();
                    player.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
                } else {
                    String message = plugin.getMessagesManager().getBannedWordsList();
                    message = message.replace("%words%", String.join(", ", bannedWords));
                    player.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
                }
                break;

            case "add":
                if (args.length < 2) {
                    String message = plugin.getMessagesManager().getUsageBannedWordsAdd();
                    player.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
                    return true;
                }
                String wordToAdd = args[1].toLowerCase();
                if (!bannedWordsManager.getBannedWords().contains(wordToAdd)) {
                    bannedWordsManager.getBannedWords().add(wordToAdd);
                    bannedWordsManager.saveBannedWords();
                    String message = plugin.getMessagesManager().getBannedWordsAdd().replace("%word%", wordToAdd);
                    player.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
                } else {
                    String message = plugin.getMessagesManager().getBannedWordsAlready().replace("%word%", wordToAdd);
                    player.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
                }
                break;

            case "remove":
                if (args.length < 2) {
                    String message = plugin.getMessagesManager().getUsageBannedWordsRemove();
                    player.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
                    return true;
                }
                String wordToRemove = args[1].toLowerCase();
                if (bannedWordsManager.getBannedWords().contains(wordToRemove)) {
                    bannedWordsManager.getBannedWords().remove(wordToRemove);
                    bannedWordsManager.saveBannedWords();
                    String message = plugin.getMessagesManager().getBannedWordsRemoved().replace("%word%", wordToRemove);
                    player.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
                } else {
                    String message = plugin.getMessagesManager().getBannedWordsUnknown().replace("%word%", wordToRemove);
                    player.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
                }
                break;

            default:
                String message = plugin.getMessagesManager().getUsageBannedWords();
                player.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
                break;
        }

        return true;
    }
}
