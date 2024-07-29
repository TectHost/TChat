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
                        return;
                    }

                    AutoBroadcastManager.Broadcast currentBroadcast = broadcasts[currentBroadcastIndex];

                    if (currentBroadcast.isEnabled()) {
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            for (String line : currentBroadcast.getMessage()) {
                                String translatedMessage = plugin.getTranslateColors().translateColors(player, line);
                                if (translatedMessage.contains("%center%")) {
                                    translatedMessage = translatedMessage.replace("%center%", "");
                                    translatedMessage = centerText(translatedMessage);
                                }
                                player.sendMessage(translatedMessage);
                            }

                            if (currentBroadcast.isTitleEnabled()) {
                                player.sendTitle(plugin.getTranslateColors().translateColors(player, currentBroadcast.getTitle()), plugin.getTranslateColors().translateColors(player, currentBroadcast.getSubtitle()), 10, 70, 20);
                            }

                            if (currentBroadcast.isActionbarEnabled()) {
                                player.spigot().sendMessage(net.md_5.bungee.api.ChatMessageType.ACTION_BAR, net.md_5.bungee.api.chat.TextComponent.fromLegacyText(plugin.getTranslateColors().translateColors(player, currentBroadcast.getActionbar())));
                            }

                            if (currentBroadcast.isSoundEnabled()) {
                                Sound sound = Sound.valueOf(currentBroadcast.getSound().toUpperCase());
                                player.playSound(player.getLocation(), sound, 1.0F, 1.0F);
                            }

                            if (currentBroadcast.isParticlesEnabled()) {
                                try {
                                    Particle particle = Particle.valueOf(currentBroadcast.getParticle().toUpperCase());
                                    player.getWorld().spawnParticle(particle, player.getLocation(), currentBroadcast.getParticleCount());
                                } catch (IllegalArgumentException e) {
                                    plugin.getLogger().warning("Invalid particle type: " + currentBroadcast.getParticle());
                                }
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
