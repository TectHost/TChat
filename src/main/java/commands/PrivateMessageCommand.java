package commands;

import minealex.tchat.TChat;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PrivateMessageCommand implements CommandExecutor {

    private final Map<UUID, UUID> lastMessaged = new HashMap<>();
    private final TChat plugin;

    public PrivateMessageCommand(TChat plugin) {
        this.plugin = plugin;
    }

    public Map<UUID, UUID> getLastMessaged() {
        return lastMessaged;
    }

    public void setLastMessaged(UUID sender, UUID receiver) {
        lastMessaged.put(sender, receiver);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        Player senderPlayer = (Player) sender;
        String prefix = plugin.getMessagesManager().getPrefix();

        if (senderPlayer.hasPermission(plugin.getConfigManager().getMsgPermission()) || senderPlayer.hasPermission("tchat.admin")) {
            if (args.length < 2) {
                String message = plugin.getMessagesManager().getUsageMsg();
                sender.sendMessage(plugin.getTranslateColors().translateColors(senderPlayer, prefix + message));
                return true;
            }

            Player targetPlayer = Bukkit.getPlayer(args[0]);

            if (targetPlayer == null) {
                String message = plugin.getMessagesManager().getPlayerNotFound();
                sender.sendMessage(plugin.getTranslateColors().translateColors(senderPlayer, prefix + message));
                return true;
            }

            StringBuilder message = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                message.append(args[i]).append(" ");
            }

            String senderMessage = plugin.getConfigManager().getMsgFormatSender();
            senderMessage = senderMessage.replace("%sender%", sender.getName()).replace("%recipient%", targetPlayer.getName()).replace("%message%", message.toString().trim());
            senderPlayer.sendMessage(plugin.getTranslateColors().translateColors(senderPlayer, senderMessage));
            String formattedMessage = plugin.getConfigManager().getMsgFormatReceiver();
            formattedMessage = formattedMessage.replace("%sender%", sender.getName()).replace("%recipient%", targetPlayer.getName()).replace("%message%", message.toString().trim());
            targetPlayer.sendMessage(plugin.getTranslateColors().translateColors(targetPlayer, formattedMessage));

            setLastMessaged(senderPlayer.getUniqueId(), targetPlayer.getUniqueId());
        } else {
            String message = plugin.getMessagesManager().getNoPermission();
            senderPlayer.sendMessage(plugin.getTranslateColors().translateColors(senderPlayer, prefix + message));
        }

        return true;
    }
}
