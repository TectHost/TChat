package blocked;

import config.AdvertisingConfig;
import minealex.tchat.TChat;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.jetbrains.annotations.NotNull;

public class AntiAdvertising {
    private final TChat plugin;

    public AntiAdvertising(TChat plugin) {
        this.plugin = plugin;
    }

    public void checkAdvertising(@NotNull AsyncPlayerChatEvent event) {
        if (event.isCancelled()) return;

        Player player = event.getPlayer();
        String message = event.getMessage();

        checkAndHandle(event, player, message, plugin.getConfigManager().getIpv4Config());
        checkAndHandle(event, player, message, plugin.getConfigManager().getDomainConfig());
        checkAndHandle(event, player, message, plugin.getConfigManager().getLinksConfig());
    }

    public void checkAdvertisingCommand(@NotNull PlayerCommandPreprocessEvent event, Player player, String command) {
        if (event.isCancelled()) return;

        checkAndHandleCommand(event, player, command, plugin.getConfigManager().getIpv4Config());
        checkAndHandleCommand(event, player, command, plugin.getConfigManager().getDomainConfig());
        checkAndHandleCommand(event, player, command, plugin.getConfigManager().getLinksConfig());
    }

    private void checkAndHandleCommand(PlayerCommandPreprocessEvent event, @NotNull Player player, String command, AdvertisingConfig config) {
        if (!player.hasPermission(plugin.getConfigManager().getAdvertisingBypass()) || !player.hasPermission("tchat.admin")) {
            if (config.isEnabled() && command.matches(config.getMatch()) && isWhitelisted(command, config)) {
                event.setCancelled(true);
                handleActions(player, command, config);
            }
        }
    }

    private boolean isWhitelisted(String message, @NotNull AdvertisingConfig config) {
        if (config.getWhitelist() != null) {
            for (String whitelistItem : config.getWhitelist()) {
                if (message.contains(whitelistItem)) {
                    return false;
                }
            }
        }
        return true;
    }

    public void checkAndHandle(AsyncPlayerChatEvent event, @NotNull Player player, String message, AdvertisingConfig config) {
        if (!player.hasPermission(plugin.getConfigManager().getAdvertisingBypass()) || !player.hasPermission("tchat.admin")) {
            if (config.isEnabled()) {
                if (message.matches(config.getMatch())) {
                    if (isWhitelisted(message, config)) {
                        if (event != null) {
                            event.setCancelled(true);
                        }
                        handleActions(player, message, config);
                    }
                }
            }
        }
    }


    private void handleActions(Player player, String message, @NotNull AdvertisingConfig config) {
        if (config.isMessageEnabled()) {
            config.getMessage().forEach(msg -> {
                String messageToSend = plugin.getTranslateColors().translateColors(player, msg);
                player.sendMessage(messageToSend);
            });
        }
        if (config.isTitleEnabled()) {
            String title = plugin.getTranslateColors().translateColors(player, config.getTitle());
            String subtitle = plugin.getTranslateColors().translateColors(player, config.getSubtitle());
            player.sendTitle(title, subtitle, config.getTitleFadeIn(), config.getTitleStay(), config.getTitleFadeOut());
        }
        if (config.isActionBarEnabled()) {
            String actionBarMessage = plugin.getTranslateColors().translateColors(player, config.getActionBar());
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(actionBarMessage));
        }
        if (config.isSoundEnabled()) {
            Sound sound = Sound.valueOf(config.getSound().toUpperCase());
            player.playSound(player.getLocation(), sound, config.getSoundVolume(), config.getSoundPitch());
        }
        if (config.isParticlesEnabled()) {
            Particle particle = Particle.valueOf(config.getParticleType().toUpperCase());
            player.getWorld().spawnParticle(particle, player.getLocation(), config.getParticles());
        }
        if (plugin.getConfigManager().isAntiAdvertisingLogEnabled()) {
            plugin.getLogsManager().logAntiAdvertising(player.getName(), message);
        }
    }
}
