package listeners;

import config.AntiAdvertisingManager;
import minealex.tchat.TChat;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SignListener implements Listener {
    private final TChat plugin;
    private final AntiAdvertisingManager antiAdvertisingManager;

    public SignListener(@NotNull TChat plugin) {
        this.plugin = plugin;
        this.antiAdvertisingManager = plugin.getAntiAdvertisingManager();
    }

    @EventHandler
    public void onSignChange(@NotNull SignChangeEvent event) {
        if (event.isCancelled()) return;

        Player player = event.getPlayer();
        String[] lines = event.getLines();

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if (player.hasPermission("tchat.admin") || player.hasPermission("tchat.signcolor")) {
                lines[i] = plugin.getTranslateColors().translateColors(player, line);
            } else {
                lines[i] = ChatColor.stripColor(line);
            }

            for (String match : getAllMatchPatterns()) {
                if (antiAdvertisingManager.isEnabled() && line.matches(match)) {
                    if (!player.hasPermission("tchat.bypass.advertising") && !player.hasPermission("tchat.admin") && !isWhitelisted(line)) {
                        event.setCancelled(true);
                        handleSignAdvertising(player, line, match);
                    }
                    return;
                }
            }
        }
        for (int i = 0; i < lines.length; i++) {
            event.setLine(i, lines[i]);
        }
    }

    @EventHandler
    public void onPlayerInteract(@NotNull PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        if (event.getClickedBlock() == null || !(event.getClickedBlock().getState() instanceof Sign sign)) return;

        Player player = event.getPlayer();
        String[] lines = sign.getLines();

        for (String s : lines) {
            String line = s;
            if (!player.hasPermission("tchat.admin") && !player.hasPermission("tchat.signcolor")) {
                line = ChatColor.stripColor(line);
            } else {
                line = plugin.getTranslateColors().translateColors(player, line);
            }

            for (String match : getAllMatchPatterns()) {
                if (antiAdvertisingManager.isEnabled() && line.matches(match)) {
                    if (!player.hasPermission("tchat.bypass.advertising") && !player.hasPermission("tchat.admin") && !isWhitelisted(line)) {
                        handleSignAdvertising(player, line, match);
                    }
                    return;
                }
            }
        }
    }

    private void handleSignAdvertising(Player player, String line, String match) {
        AntiAdvertisingManager.ActionConfig actionConfig = getActionConfigForMatch(match);

        if (actionConfig != null) {
            handleSignActions(player, line, actionConfig);
        }
    }

    private void handleSignActions(Player player, String message, @NotNull AntiAdvertisingManager.ActionConfig config) {
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

    private @NotNull List<String> getAllMatchPatterns() {
        List<String> matches = new ArrayList<>();
        if (antiAdvertisingManager.isIpv4Enabled()) {
            matches.add(antiAdvertisingManager.getIpv4Match());
        }
        if (antiAdvertisingManager.isDomainEnabled()) {
            matches.add(antiAdvertisingManager.getDomainMatch());
        }
        if (antiAdvertisingManager.isLinksEnabled()) {
            matches.add(antiAdvertisingManager.getLinksMatch());
        }
        return matches;
    }

    private AntiAdvertisingManager.@Nullable ActionConfig getActionConfigForMatch(@NotNull String match) {
        if (match.equals(antiAdvertisingManager.getIpv4Match())) {
            return antiAdvertisingManager.getIpv4Actions();
        } else if (match.equals(antiAdvertisingManager.getDomainMatch())) {
            return antiAdvertisingManager.getDomainActions();
        } else if (match.equals(antiAdvertisingManager.getLinksMatch())) {
            return antiAdvertisingManager.getLinksActions();
        }
        return null;
    }

    private boolean isWhitelisted(String message) {
        for (String whitelistItem : antiAdvertisingManager.getWhitelist()) {
            if (message.contains(whitelistItem)) {
                return true;
            }
        }
        return false;
    }
}
