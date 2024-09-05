package commands;

import minealex.tchat.TChat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class MuteChatCommand implements CommandExecutor {
    private final TChat plugin;

    private static boolean chatMuted = false;

    public MuteChatCommand(TChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        String prefix = plugin.getMessagesManager().getPrefix();
        if (sender.hasPermission(plugin.getConfigManager().getMuteChatPermission())) {
            chatMuted = !chatMuted;

            String message;
            if (sender instanceof Player player) {
                message = chatMuted
                        ? plugin.getTranslateColors().translateColors(player, prefix + plugin.getMessagesManager().getChatMute())
                        : plugin.getTranslateColors().translateColors(player, prefix + plugin.getMessagesManager().getChatUnmute());
            } else {
                message = chatMuted
                        ? plugin.getTranslateColors().translateColors(null, prefix + plugin.getMessagesManager().getChatMute())
                        : plugin.getTranslateColors().translateColors(null, prefix + plugin.getMessagesManager().getChatUnmute());
            }

            sender.getServer().broadcastMessage(message);

        } else {
            String message = plugin.getMessagesManager().getNoPermission();
            sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + message));
        }
        return true;
    }

    public static boolean isChatMuted() {
        return chatMuted;
    }
}
