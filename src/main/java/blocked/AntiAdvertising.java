package blocked;

import config.AntiAdvertisingManager;
import minealex.tchat.TChat;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AntiAdvertising {
    private final TChat plugin;
    private final AntiAdvertisingManager antiAdvertisingManager;

    public AntiAdvertising(@NotNull TChat plugin) {
        this.plugin = plugin;
        this.antiAdvertisingManager = plugin.getAntiAdvertisingManager();
    }

    public void checkAdvertising(@NotNull AsyncPlayerChatEvent event) {
        if (event.isCancelled()) return;

        Player player = event.getPlayer();
        String message = event.getMessage();

        if (antiAdvertisingManager.isIpv4Enabled()) {
            checkAndHandle(event, player, message, antiAdvertisingManager.getIpv4Match(), antiAdvertisingManager.getIpv4Actions());
        }

        if (antiAdvertisingManager.isDomainEnabled()) {
            checkAndHandle(event, player, message, antiAdvertisingManager.getDomainMatch(), antiAdvertisingManager.getDomainActions());
        }

        if (antiAdvertisingManager.isLinksEnabled()) {
            checkAndHandle(event, player, message, antiAdvertisingManager.getLinksMatch(), antiAdvertisingManager.getLinksActions());
        }
    }

    public void checkAdvertisingCommand(@NotNull PlayerCommandPreprocessEvent event, Player player, String command) {
        if (event.isCancelled()) return;

        if (antiAdvertisingManager.isIpv4Enabled()) {
            checkAndHandleCommand(event, player, command, antiAdvertisingManager.getIpv4Match(), antiAdvertisingManager.getIpv4Actions());
        }

        if (antiAdvertisingManager.isDomainEnabled()) {
            checkAndHandleCommand(event, player, command, antiAdvertisingManager.getDomainMatch(), antiAdvertisingManager.getDomainActions());
        }

        if (antiAdvertisingManager.isLinksEnabled()) {
            checkAndHandleCommand(event, player, command, antiAdvertisingManager.getLinksMatch(), antiAdvertisingManager.getLinksActions());
        }
    }

    private void checkAndHandleCommand(PlayerCommandPreprocessEvent event, @NotNull Player player, String command, String match, AntiAdvertisingManager.ActionConfig config) {
        if (!player.hasPermission("tchat.bypass.advertising") || !player.hasPermission("tchat.admin")) {
            if (command.matches(match) && isWhitelisted(command, antiAdvertisingManager.getWhitelist())) {
                event.setCancelled(true);
                handleActions(player, command, config);
            }
        }
    }

    private boolean isWhitelisted(String message, @NotNull List<String> whitelist) {
        for (String whitelistItem : whitelist) {
            if (message.contains(whitelistItem)) {
                return false;
            }
        }
        return true;
    }

    public void checkAndHandle(AsyncPlayerChatEvent event, @NotNull Player player, String message, String match, AntiAdvertisingManager.ActionConfig config) {
        if (!player.hasPermission("tchat.bypass.advertising") || !player.hasPermission("tchat.admin")) {
            if (message.matches(match)) {
                if (isWhitelisted(message, antiAdvertisingManager.getWhitelist())) {
                    event.setCancelled(true);
                    handleActions(player, message, config);
                }
            }
        }
    }

    private void handleActions(Player player, String message, @NotNull AntiAdvertisingManager.ActionConfig config) {
        if (config.messageEnabled) {
            config.message.forEach(msg -> {
                String messageToSend = plugin.getTranslateColors().translateColors(player, msg);
                player.sendMessage(messageToSend);
            });
        }
        if (config.titleEnabled) {
            String title = plugin.getTranslateColors().translateColors(player, config.title);
            String subtitle = plugin.getTranslateColors().translateColors(player, config.subtitle);
            player.sendTitle(title, subtitle, config.titleFadeIn, config.titleStay, config.titleFadeOut);
        }
        if (config.actionBarEnabled) {
            String actionBarMessage = plugin.getTranslateColors().translateColors(player, config.actionBar);
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(actionBarMessage));
        }
        if (config.soundEnabled) {
            Sound sound = Sound.valueOf(config.sound.toUpperCase());
            player.playSound(player.getLocation(), sound, config.soundVolume, config.soundPitch);
        }
        if (config.particlesEnabled) {
            Particle particle = Particle.valueOf(config.particleType.toUpperCase());
            player.getWorld().spawnParticle(particle, player.getLocation(), config.particles);
        }
        if (plugin.getLoggerConfigManager().isAntiAdvertisingLogEnabled()) {
            plugin.getLogsManager().logAntiAdvertising(player.getName(), message);
        }
    }
}
