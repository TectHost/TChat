package utils;

import minealex.tchat.TChat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import config.AutoBroadcastManager;

public class AutoBroadcastSender {
    private final TChat plugin;
    private final AutoBroadcastManager autoBroadcastManager;
    private int currentBroadcastIndex;
    private BukkitTask broadcastTask;

    public AutoBroadcastSender(TChat plugin) {
        this.plugin = plugin;
        this.autoBroadcastManager = plugin.getAutoBroadcastManager();
        this.currentBroadcastIndex = 0;
        startBroadcastTask();
    }

    public void startBroadcastTask() {
        if (broadcastTask != null && !broadcastTask.isCancelled()) {
            return;
        }

        broadcastTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (autoBroadcastManager.isEnabled()) {
                    AutoBroadcastManager.Broadcast[] broadcasts = autoBroadcastManager.getBroadcasts().values().toArray(new AutoBroadcastManager.Broadcast[0]);

                    if (broadcasts.length == 0) {
                        plugin.getLogger().info("No broadcasts available.");
                        return;
                    }

                    AutoBroadcastManager.Broadcast currentBroadcast = broadcasts[currentBroadcastIndex];

                    if (currentBroadcast.isEnabled()) {
                        String channelName = currentBroadcast.getChannel();

                        if (currentBroadcast.getChannel().equalsIgnoreCase("none")) {
                            sendMessageToPlayers(Bukkit.getOnlinePlayers(), currentBroadcast);
                        } else {
                            int messageMode = plugin.getChannelsConfigManager().getChannel(channelName).getMessageMode();

                            switch (messageMode) {
                                case 0:
                                    sendMessageToPlayers(Bukkit.getOnlinePlayers(), currentBroadcast);
                                    break;

                                case 1:
                                    for (Player player : Bukkit.getOnlinePlayers()) {
                                        String permission = plugin.getChannelsConfigManager().getChannel(channelName).getPermission();
                                        if (player.hasPermission(permission)) {
                                            sendMessageToPlayer(player, currentBroadcast);
                                        }
                                    }
                                    break;

                                case 2:
                                    for (Player player : plugin.getChannelsManager().getPlayersInChannel(channelName)) {
                                        sendMessageToPlayer(player, currentBroadcast);
                                    }
                                    break;

                                case 3:
                                    break;

                                default:
                                    plugin.getLogger().warning("Unknown Message Mode: " + messageMode);
                                    break;
                            }
                        }
                    }

                    currentBroadcastIndex = (currentBroadcastIndex + 1) % broadcasts.length;
                }
            }
        }.runTaskTimer(plugin, 0, autoBroadcastManager.getTime() * 20L);
    }

    public void stopBroadcastTask() {
        if (broadcastTask != null && !broadcastTask.isCancelled()) {
            broadcastTask.cancel();
        }
    }

    public void restartBroadcasts() {
        stopBroadcastTask();
        currentBroadcastIndex = 0;
        startBroadcastTask();
    }

    private void sendMessageToPlayers(Iterable<? extends Player> players, AutoBroadcastManager.Broadcast broadcast) {
        for (Player player : players) {
            sendMessageToPlayer(player, broadcast);
        }
    }

    private void sendMessageToPlayer(Player player, AutoBroadcastManager.Broadcast broadcast) {
        String broadcastPermission = broadcast.getPermission();

        if (broadcastPermission != null && !broadcastPermission.equalsIgnoreCase("none") && !broadcastPermission.isEmpty()) {
            if (!player.hasPermission(broadcastPermission)) {
                return;
            }
        }

        for (String line : broadcast.getMessage()) {
            String translatedMessage = plugin.getTranslateColors().translateColors(player, line);
            if (translatedMessage.contains("%center%")) {
                translatedMessage = translatedMessage.replace("%center%", "");
                translatedMessage = centerText(translatedMessage);
            }
            player.sendMessage(translatedMessage);
        }

        if (broadcast.isTitleEnabled()) {
            player.sendTitle(
                    plugin.getTranslateColors().translateColors(player, broadcast.getTitle()),
                    plugin.getTranslateColors().translateColors(player, broadcast.getSubtitle()),
                    10, 70, 20
            );
        }

        if (broadcast.isActionbarEnabled()) {
            player.spigot().sendMessage(
                    net.md_5.bungee.api.ChatMessageType.ACTION_BAR,
                    net.md_5.bungee.api.chat.TextComponent.fromLegacyText(plugin.getTranslateColors().translateColors(player, broadcast.getActionbar()))
            );
        }

        if (broadcast.isSoundEnabled()) {
            try {
                Sound sound = Sound.valueOf(broadcast.getSound().toUpperCase());
                player.playSound(player.getLocation(), sound, 1.0F, 1.0F);
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Invalid sound type: " + broadcast.getSound());
            }
        }

        if (broadcast.isParticlesEnabled()) {
            try {
                Particle particle = Particle.valueOf(broadcast.getParticle().toUpperCase());
                player.getWorld().spawnParticle(particle, player.getLocation(), broadcast.getParticleCount());
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Invalid particle type: " + broadcast.getParticle());
            }
        }
    }

    private String centerText(String message) {
        final int maxLength = 56;
        String strippedMessage = ChatColor.stripColor(message);
        int length = strippedMessage.length();

        if (length >= maxLength) {
            return message;
        }

        int spaces = (maxLength - length) / 2;
        StringBuilder centeredMessage = new StringBuilder();

        centeredMessage.append(" ".repeat(spaces));
        centeredMessage.append(message);

        while (centeredMessage.length() < maxLength) {
            centeredMessage.append(" ");
        }

        return centeredMessage.toString();
    }
}
