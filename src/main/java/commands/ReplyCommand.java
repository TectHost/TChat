package commands;

import minealex.tchat.TChat;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class ReplyCommand implements CommandExecutor {

    private final PrivateMessageCommand privateMessageCommand;
    private final TChat plugin;

    public ReplyCommand(TChat plugin, PrivateMessageCommand privateMessageCommand) {
        this.plugin = plugin;
        this.privateMessageCommand = privateMessageCommand;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        Player senderPlayer = (Player) sender;
        String prefix = plugin.getMessagesManager().getPrefix();

        if (senderPlayer.hasPermission(plugin.getConfigManager().getReplyPermission()) || senderPlayer.hasPermission("tchat.admin")) {
            if (args.length < 1) {
                String message = plugin.getMessagesManager().getUsageReply();
                sender.sendMessage(plugin.getTranslateColors().translateColors(senderPlayer, prefix + message));
                return true;
            }

            UUID lastMessagedUUID = privateMessageCommand.getLastMessaged().get(senderPlayer.getUniqueId());

            if (lastMessagedUUID == null) {
                String message = plugin.getMessagesManager().getNoReply();
                sender.sendMessage(plugin.getTranslateColors().translateColors(senderPlayer, prefix + message));
                return true;
            }

            Player targetPlayer = Bukkit.getPlayer(lastMessagedUUID);

            if (targetPlayer == null) {
                String message = plugin.getMessagesManager().getPlayerNotFound();
                sender.sendMessage(plugin.getTranslateColors().translateColors(senderPlayer, prefix + message));
                return true;
            }

            StringBuilder message = new StringBuilder();
            for (String arg : args) {
                message.append(arg).append(" ");
            }

            String senderMessage = plugin.getConfigManager().getReplyFormatSender();
            senderMessage = senderMessage.replace("%sender%", sender.getName()).replace("%recipient%", targetPlayer.getName()).replace("%message%", message.toString().trim());
            senderPlayer.sendMessage(plugin.getTranslateColors().translateColors(senderPlayer, senderMessage));
            String formattedMessage = plugin.getConfigManager().getReplyFormatReceiver();
            formattedMessage = formattedMessage.replace("%sender%", sender.getName()).replace("%recipient%", targetPlayer.getName()).replace("%message%", message.toString().trim());
            targetPlayer.sendMessage(plugin.getTranslateColors().translateColors(targetPlayer, formattedMessage));
        } else {
            String message = plugin.getMessagesManager().getNoPermission();
            senderPlayer.sendMessage(plugin.getTranslateColors().translateColors(senderPlayer, prefix + message));
        }

        return true;
    }
}
