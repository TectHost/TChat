package commands;

import config.ChannelsConfigManager;
import minealex.tchat.TChat;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import utils.ChannelsManager;

import java.util.Arrays;

public class ChannelCommand implements CommandExecutor {

    private final TChat plugin;
    private final ChannelsManager channelsManager;
    private final ChannelsConfigManager channelsConfigManager;

    public ChannelCommand(@NotNull TChat plugin) {
        this.plugin = plugin;
        this.channelsManager = plugin.getChannelsManager();
        this.channelsConfigManager = plugin.getChannelsConfigManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        String prefix = plugin.getMessagesManager().getPrefix();

        if (!(sender instanceof Player player)) {
            sendMessage(sender, plugin.getMessagesManager().getNoPlayer(), prefix);
            return true;
        }

        if (args.length == 0) {
            sendMessage(sender, plugin.getMessagesManager().getUsageChannel(), prefix);
            return true;
        }

        String action = args[0];
        switch (action.toLowerCase()) {
            case "join":
                handleJoin(player, args, prefix);
                break;
            case "leave":
                handleLeave(player, args, prefix);
                break;
            case "send":
                handleSend(player, args, prefix);
                break;
            default:
                sendMessage(sender, plugin.getMessagesManager().getUsageChannel(), prefix);
        }

        return true;
    }

    private void handleJoin(Player player, String[] args, String prefix) {
        if (hasPermission(player, "tchat.channel.command.join")) {
            sendMessage(player, plugin.getMessagesManager().getNoPermission(), prefix);
            return;
        }

        if (args.length < 2) {
            sendMessage(player, plugin.getMessagesManager().getUsageJoinChannel(), prefix);
            return;
        }

        String channelName = args[1];
        Player targetPlayer = player;

        if (args.length >= 3) {
            Player specifiedPlayer = Bukkit.getPlayer(args[2]);
            if (specifiedPlayer == null) {
                player.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + plugin.getMessagesManager().getPlayerNotFound().replace("%player%", args[2])));
                return;
            }
            targetPlayer = specifiedPlayer;
        }

        joinChannel(targetPlayer, channelName, prefix);
    }

    private void handleLeave(Player player, String[] args, String prefix) {
        if (hasPermission(player, "tchat.channel.command.leave")) {
            sendMessage(player, plugin.getMessagesManager().getNoPermission(), prefix);
            return;
        }

        if (args.length < 2) {
            sendMessage(player, plugin.getMessagesManager().getUsageLeaveChannel(), prefix);
            return;
        }

        String channelName = args[1];
        leaveChannel(player, channelName, prefix);
    }

    private void handleSend(Player player, String[] args, String prefix) {
        if (hasPermission(player, "tchat.channel.command.send")) {
            sendMessage(player, plugin.getMessagesManager().getNoPermission(), prefix);
            return;
        }

        if (args.length < 3) {
            sendMessage(player, plugin.getMessagesManager().getUsageSendChannel(), prefix);
            return;
        }

        String channelName = args[1];
        String message = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
        sendMessageToChannel(player, channelName, message, prefix);
    }

    private void joinChannel(Player player, String channelName, String prefix) {
        ChannelsConfigManager.Channel channel = channelsConfigManager.getChannel(channelName);

        if (channel == null) {
            sendMessage(player, plugin.getMessagesManager().getChannelNotExist().replace("%channel%", channelName), prefix);
            return;
        }

        if (hasPermission(player, channel.getPermission())) {
            sendMessage(player, plugin.getMessagesManager().getChannelNoPermissionJoin().replace("%channel%", channelName), prefix);
            return;
        }

        String currentChannel = channelsManager.getPlayerChannel(player);

        if (channelName.equalsIgnoreCase(currentChannel)) {
            sendMessage(player, plugin.getMessagesManager().getChannelAlready().replace("%channel%", channelName), prefix);
            return;
        }

        if (currentChannel != null) {
            channelsManager.removePlayerFromChannel(player);
            sendMessage(player, plugin.getMessagesManager().getChannelLeft().replace("%channel%", currentChannel), prefix);
        }

        channelsManager.setPlayerChannel(player, channelName);
        sendMessage(player, plugin.getMessagesManager().getChannelJoin().replace("%channel%", channelName), prefix);

        announceChannelChange(player, channelName, plugin.getMessagesManager().getChannelJoinAnnounce(), prefix);
    }

    private void leaveChannel(Player player, @NotNull String channelName, String prefix) {
        String currentChannel = channelsManager.getPlayerChannel(player);

        if (!channelName.equalsIgnoreCase(currentChannel)) {
            sendMessage(player, plugin.getMessagesManager().getNoChannel().replace("%channel%", channelName), prefix);
            return;
        }

        ChannelsConfigManager.Channel channel = channelsConfigManager.getChannel(channelName);

        if (channel != null && hasPermission(player, "tchat.channel.command.leave")) {
            sendMessage(player, plugin.getMessagesManager().getNoPermissionChannelLeft().replace("%channel%", channelName), prefix);
            return;
        }

        channelsManager.removePlayerFromChannel(player);
        sendMessage(player, plugin.getMessagesManager().getChannelLeft().replace("%channel%", currentChannel), prefix);

        if (channel != null) {
            announceChannelChange(player, channelName, plugin.getMessagesManager().getChannelLeftAnnounce(), prefix);
        }
    }

    private void sendMessageToChannel(Player player, String channelName, String message, String prefix) {
        ChannelsConfigManager.Channel channel = channelsConfigManager.getChannel(channelName);

        if (channel == null) {
            sendMessage(player, plugin.getMessagesManager().getChannelNotExist().replace("%channel%", channelName), prefix);
            return;
        }

        String format = channel.isFormatEnabled() ? channel.getFormat() : "%player%";

        String formattedMessage = format
                .replace("%player%", player.getName())
                .replace("%channel%", channelName);

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (channel.isEnabled()) {
                String recipientChannel = plugin.getChannelsManager().getPlayerChannel(p);
                boolean hasPermissionForChannel = p.hasPermission(channel.getPermission()) || p.hasPermission("tchat.admin") || p.hasPermission("tchat.channel.all");
                boolean isInRecipientChannel = recipientChannel != null && recipientChannel.equals(channelName);

                int messageMode = channel.getMessageMode();
                if (messageMode == 0 || (messageMode == 1 && hasPermissionForChannel) || (messageMode == 2 && isInRecipientChannel)) {
                    p.sendMessage(plugin.getTranslateColors().translateColors(player, formattedMessage + message));
                }
            }
        }
    }

    private void announceChannelChange(Player player, String channelName, String messageTemplate, String prefix) {
        ChannelsConfigManager.Channel channel = channelsConfigManager.getChannel(channelName);

        if (channel == null) return;

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.equals(player)) continue;

            String recipientChannel = channelsManager.getPlayerChannel(p);
            boolean hasPermissionForChannel = p.hasPermission(channel.getPermission()) ||
                    p.hasPermission("tchat.admin") ||
                    p.hasPermission("tchat.channel.all");
            boolean isInRecipientChannel = recipientChannel != null && recipientChannel.equals(channelName);

            int announceMode = channel.getAnnounceMode();
            if (shouldAnnounce(announceMode, hasPermissionForChannel, isInRecipientChannel)) {
                sendMessage(p, messageTemplate.replace("%player%", player.getName()).replace("%channel%", channelName), prefix);
            }
        }
    }

    private boolean shouldAnnounce(int announceMode, boolean hasPermissionForChannel, boolean isInRecipientChannel) {
        return announceMode == 0 || (announceMode == 1 && hasPermissionForChannel) || (announceMode == 2 && isInRecipientChannel);
    }

    private boolean hasPermission(@NotNull Player player, String permission) {
        return !player.hasPermission(permission) && !player.hasPermission("tchat.admin") && !player.hasPermission("tchat.channel.all");
    }

    private void sendMessage(@NotNull CommandSender sender, String message, String prefix) {
        sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + message));
    }
}
