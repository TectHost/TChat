package commands;

import config.ConfigManager;
import config.PrivateMessagesConfigManager;
import minealex.tchat.TChat;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import utils.TranslateColors;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
        TranslateColors translateColors = plugin.getTranslateColors();

        if (senderPlayer.hasPermission("tchat.reply") || senderPlayer.hasPermission("tchat.admin")) {
            if (args.length < 1) {
                String message = plugin.getMessagesManager().getUsageReply();
                sender.sendMessage(translateColors.translateColors(senderPlayer, prefix + message));
                return true;
            }

            UUID lastMessagedUUID = privateMessageCommand.getLastMessaged().get(senderPlayer.getUniqueId());

            if (lastMessagedUUID == null) {
                String message = plugin.getMessagesManager().getNoReply();
                sender.sendMessage(translateColors.translateColors(senderPlayer, prefix + message));
                return true;
            }

            Player targetPlayer = Bukkit.getPlayer(lastMessagedUUID);

            if (targetPlayer == null) {
                String message = plugin.getMessagesManager().getPlayerNotFound();
                sender.sendMessage(translateColors.translateColors(senderPlayer, prefix + message));
                return true;
            }

            StringBuilder messageBuilder = new StringBuilder();
            for (String arg : args) {
                messageBuilder.append(arg).append(" ");
            }

            String messageToSend = messageBuilder.toString().trim();

            String senderFormattedMessage = translateColors.translateColors(senderPlayer,
                    plugin.getPrivateMessagesConfigManager().getReplyFormatSender()
                            .replace("%sender%", sender.getName())
                            .replace("%recipient%", targetPlayer.getName())
                            .replace("%message%", messageToSend));

            String receiverFormattedMessage = translateColors.translateColors(targetPlayer,
                    plugin.getPrivateMessagesConfigManager().getReplyFormatReceiver()
                            .replace("%sender%", sender.getName())
                            .replace("%recipient%", targetPlayer.getName())
                            .replace("%message%", messageToSend));

            String senderGroup = plugin.getGroupManager().getGroupName(senderPlayer);
            String receiverGroup = plugin.getGroupManager().getGroupName(targetPlayer);

            PrivateMessagesConfigManager.HoverConfig senderHoverConfig = plugin.getPrivateMessagesConfigManager().getReplyHoverConfig(senderGroup);
            PrivateMessagesConfigManager.HoverConfig receiverHoverConfig = plugin.getPrivateMessagesConfigManager().getReplyHoverConfig(receiverGroup);

            List<String> hoverSenderTextList = senderHoverConfig != null
                    ? senderHoverConfig.getSenderTexts().stream()
                    .map(line -> translateColors.translateColors(senderPlayer, line))
                    .collect(Collectors.toList())
                    : List.of("No hover found (sender)");

            String hoverSenderText = String.join("\n", hoverSenderTextList);
            String senderAction = senderHoverConfig != null ? senderHoverConfig.getSenderAction() : null;

            TextComponent senderMessage = new TextComponent(new ComponentBuilder(senderFormattedMessage).create());
            senderMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hoverSenderText).create()));

            if (senderAction != null && !senderAction.isEmpty()) {
                applyClickEvent(senderMessage, senderAction);
            }

            List<String> hoverReceiverTextList = receiverHoverConfig != null
                    ? receiverHoverConfig.getReceiverTexts().stream()
                    .map(line -> translateColors.translateColors(targetPlayer, line))
                    .collect(Collectors.toList())
                    : List.of("No hover found (receiver)");

            String hoverReceiverText = String.join("\n", hoverReceiverTextList);
            String receiverAction = receiverHoverConfig != null ? receiverHoverConfig.getReceiverAction() : null;

            TextComponent receiverMessage = new TextComponent(new ComponentBuilder(receiverFormattedMessage).create());
            receiverMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hoverReceiverText).create()));

            if (receiverAction != null && !receiverAction.isEmpty()) {
                applyClickEvent(receiverMessage, receiverAction);
            }

            senderPlayer.spigot().sendMessage(senderMessage);
            targetPlayer.spigot().sendMessage(receiverMessage);

        } else {
            String message = plugin.getMessagesManager().getNoPermission();
            senderPlayer.sendMessage(translateColors.translateColors(senderPlayer, prefix + message));
        }

        return true;
    }

    private void applyClickEvent(TextComponent message, @NotNull String action) {
        if (action.startsWith("[SUGGEST]")) {
            String command = action.substring(10);
            message.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command));
        } else if (action.startsWith("[EXECUTE]")) {
            String command = action.substring(10);
            message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
        } else if (action.startsWith("[OPEN]")) {
            String url = action.substring(7);
            message.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url));
        }
    }
}
