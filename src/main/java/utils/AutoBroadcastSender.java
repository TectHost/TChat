package utils;

import minealex.tchat.TChat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import config.AutoBroadcastManager;

public class AutoBroadcastSender {
    private final TChat plugin;
    private final AutoBroadcastManager autoBroadcastManager;
    private int currentBroadcastIndex;

    public AutoBroadcastSender(TChat plugin) {
        this.plugin = plugin;
        this.autoBroadcastManager = plugin.getAutoBroadcastManager();
        this.currentBroadcastIndex = 0;
        startBroadcastTask();
    }

    private void startBroadcastTask() {
        new BukkitRunnable() {
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
                                player.sendMessage(translatedMessage);
                            }
                        }
                    }

                    currentBroadcastIndex = (currentBroadcastIndex + 1) % broadcasts.length;
                }
            }
        }.runTaskTimer(plugin, 0, autoBroadcastManager.getTime() * 20L);
    }
}
