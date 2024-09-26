package listeners;

import config.JoinManager;
import minealex.tchat.TChat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerLeftListener implements Listener {

    private final TChat plugin;
    private final JoinManager joinManager;

    public PlayerLeftListener(@NotNull TChat plugin) {
        this.plugin = plugin;
        this.joinManager = plugin.getJoinManager();
    }

    @EventHandler
    public void onPlayerLeft(@NotNull PlayerQuitEvent event) {
        Player player = event.getPlayer();
        String username = player.getName();

        plugin.getDiscordHook().sendLeftMessage(username);

        playerQuitActions(player, event);
    }

    public void playerQuitActions(Player player, PlayerQuitEvent event) {
        String groupName = plugin.getGroupManager().getGroup(player);

        JoinManager.GroupConfig groupConfig = joinManager.getAllGroupConfigs().get(groupName);
        if (groupConfig != null) {
            JoinManager.EventConfig quitConfig = groupConfig.getQuitConfig();

            if (quitConfig.isMessageEnabled()) {
                for (String line : quitConfig.getMessage()) {
                    String formattedMessage = plugin.getTranslateColors().translateColors(player, line);
                    Bukkit.broadcastMessage(formattedMessage);
                }
            }

            if (quitConfig.isTitleEnabled()) {
                String title = plugin.getTranslateColors().translateColors(player, quitConfig.getTitle());
                String subtitle = plugin.getTranslateColors().translateColors(player, quitConfig.getSubtitle());
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    onlinePlayer.sendTitle(title, subtitle, 10, 70, 20);
                }
            }

            if (quitConfig.isSoundEnabled()) {
                String soundName = quitConfig.getSound();
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    onlinePlayer.playSound(onlinePlayer.getLocation(), soundName, quitConfig.getVolume(), quitConfig.getPitch());
                }
            }

            if (quitConfig.isParticlesEnabled()) {
                org.bukkit.Particle particleType = org.bukkit.Particle.valueOf(quitConfig.getParticle());
                player.getWorld().spawnParticle(particleType, player.getLocation(), 30);
            }

            if (quitConfig.isActionbarEnabled()) {
                String actionBarMessage = plugin.getTranslateColors().translateColors(player, quitConfig.getActionbarMessage()).replace("{player}", player.getName());
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    onlinePlayer.spigot().sendMessage(net.md_5.bungee.api.ChatMessageType.ACTION_BAR, net.md_5.bungee.api.chat.TextComponent.fromLegacyText(actionBarMessage));
                }
            }

            if (quitConfig.isCancelLeftMessage()) {
                event.setQuitMessage(null);
            }
        }
    }
}
