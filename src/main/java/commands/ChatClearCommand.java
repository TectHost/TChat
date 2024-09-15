package commands;

import minealex.tchat.TChat;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ChatClearCommand implements CommandExecutor {
    private final TChat plugin;

    public ChatClearCommand(TChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        String prefix = plugin.getMessagesManager().getPrefix();
        if (sender.hasPermission("tchat.admin.chatclear")) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!player.hasPermission("tchat.bypass.chatclear")) {
                    for (int i = 0; i < plugin.getConfigManager().getMessagesChatClear(); i++) {
                        player.sendMessage("");
                    }
                }
            }

            String chatClearMessage = plugin.getMessagesManager().getChatClearMessage();
            String translatedChatClearMessage = plugin.getTranslateColors().translateColors(null, prefix + chatClearMessage);
            Bukkit.broadcastMessage(translatedChatClearMessage);
        } else {
            String message = plugin.getMessagesManager().getNoPermission();
            String translatedMessage = plugin.getTranslateColors().translateColors(null, prefix + message);
            sender.sendMessage(translatedMessage);
        }
        return true;
    }
}
