package listeners;

import config.JoinManager;
import minealex.tchat.TChat;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class PlayerJoinListener implements Listener {

    private final TChat plugin;
    private final JoinManager joinManager;
    private final Set<Player> unverifiedPlayers = new HashSet<>();

    public PlayerJoinListener(@NotNull TChat plugin) {
        this.plugin = plugin;
        this.joinManager = plugin.getJoinManager();
    }

    @EventHandler
    public void onPlayerJoin(@NotNull PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String username = player.getName();

        if (!unverifiedPlayers.contains(player) && plugin.getConfigManager().isAntiBotEnabled() &&
                !player.hasPermission("tchat.bypass.antibot") &&
                !player.hasPermission("tchat.admin")) {
            unverifiedPlayers.add(player);
            if (plugin.getAntiBotConfigManager().isAntibotJoin()) {
                String prefix = plugin.getMessagesManager().getPrefix();
                String message = plugin.getMessagesManager().getAntibotJoin();
                player.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
            }
        }

        if (plugin.getConfigManager().isJoinsEnabled()) {
            joinPersonalActions(player, event);
            joinGlobalActions(player);
        }

        if (plugin.getConfigManager().isDiscordEnabled()) { plugin.getDiscordHook().sendJoinMessage(username); }
    }

    private void joinGlobalActions(Player player) {
        String groupName = plugin.getGroupManager().getGroup(player);

        JoinManager.GroupConfig groupConfig = joinManager.getAllGroupConfigs().get(groupName);
        if (groupConfig != null) {
            JoinManager.EventConfig globalConfig = groupConfig.getGlobalConfig();

            if (globalConfig.isMessageEnabled()) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (p.equals(player)) continue;
                    for (String line : globalConfig.getMessage()) {
                        p.sendMessage(plugin.getTranslateColors().translateColors(player, line));
                    }
                }
            }

            if (globalConfig.isTitleEnabled()) {
                String title = plugin.getTranslateColors().translateColors(player, globalConfig.getTitle());
                String subtitle = plugin.getTranslateColors().translateColors(player, globalConfig.getSubtitle());
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (p.equals(player)) continue;
                    p.sendTitle(title, subtitle, 10, 70, 20);
                }
            }

            if (globalConfig.isSoundEnabled()) {
                org.bukkit.Sound sound = org.bukkit.Sound.valueOf(globalConfig.getSound().toUpperCase());
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (p.equals(player)) continue;
                    p.playSound(p.getLocation(), sound, globalConfig.getVolume(), globalConfig.getPitch());
                }
            }

            if (globalConfig.isParticlesEnabled()) {
                org.bukkit.Particle particle = org.bukkit.Particle.valueOf(globalConfig.getParticle().toUpperCase());
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (p.equals(player)) continue;
                    p.getWorld().spawnParticle(particle, p.getLocation(), 30);
                }
            }

            if (globalConfig.isActionbarEnabled()) {
                String actionBar = plugin.getTranslateColors().translateColors(player, globalConfig.getActionbarMessage());
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (p.equals(player)) continue;
                    p.spigot().sendMessage(net.md_5.bungee.api.ChatMessageType.ACTION_BAR, net.md_5.bungee.api.chat.TextComponent.fromLegacyText(actionBar));
                }
            }
        }
    }

    private void joinPersonalActions(Player player, PlayerJoinEvent event) {
        String groupName = plugin.getGroupManager().getGroup(player);

        JoinManager.GroupConfig groupConfig = joinManager.getAllGroupConfigs().get(groupName);
        if (groupConfig != null) {
            JoinManager.EventConfig personalConfig = groupConfig.getPersonalConfig();

            if (personalConfig.isMessageEnabled()) {
                for (String line : personalConfig.getMessage()) {
                    player.sendMessage(plugin.getTranslateColors().translateColors(player, line));
                }
            }

            if (personalConfig.isTitleEnabled()) {
                player.sendTitle(
                        plugin.getTranslateColors().translateColors(player, personalConfig.getTitle()),
                        plugin.getTranslateColors().translateColors(player, personalConfig.getSubtitle()),
                        10, 70, 20);
            }

            if (personalConfig.isSoundEnabled()) {
                player.playSound(player.getLocation(), Sound.valueOf(personalConfig.getSound()), personalConfig.getVolume(), personalConfig.getPitch());
            }

            if (personalConfig.isParticlesEnabled()) {
                player.getWorld().spawnParticle(
                        org.bukkit.Particle.valueOf(personalConfig.getParticle()),
                        player.getLocation(), 30);
            }

            if (personalConfig.isActionbarEnabled()) {
                String actionBar = plugin.getTranslateColors().translateColors(player, personalConfig.getActionbarMessage());
                player.spigot().sendMessage(net.md_5.bungee.api.ChatMessageType.ACTION_BAR, net.md_5.bungee.api.chat.TextComponent.fromLegacyText(actionBar));
            }

            if (personalConfig.isCancelJoinMessage()) {
                event.setJoinMessage(null);
            }
        }
    }

    public boolean isUnverified(Player player) {
        return unverifiedPlayers.contains(player);
    }

    public void removeUnverifiedPlayer(Player player) {
        unverifiedPlayers.remove(player);
    }
}
