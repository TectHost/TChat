package listeners;

import blocked.AntiAdvertising;
import config.AdvertisingConfig;
import minealex.tchat.TChat;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SignListener implements Listener {
    private final TChat plugin;
    private final AntiAdvertising antiAdvertising;

    public SignListener(TChat plugin) {
        this.plugin = plugin;
        this.antiAdvertising = plugin.getAntiAdvertising();
    }

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
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

            for (AdvertisingConfig config : getAllAdvertisingConfigs()) {
                if (config.isEnabled() && lines[i].matches(config.getMatch())) {
                    if (!player.hasPermission(plugin.getConfigManager().getAdvertisingBypass()) || !player.hasPermission("tchat.admin")) {
                        event.setCancelled(true);
                        handleSignAdvertising(player, lines[i], config);
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
    public void onPlayerInteract(PlayerInteractEvent event) {
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

            for (AdvertisingConfig config : getAllAdvertisingConfigs()) {
                if (config.isEnabled() && line.matches(config.getMatch())) {
                    if (!player.hasPermission(plugin.getConfigManager().getAdvertisingBypass()) || !player.hasPermission("tchat.admin")) {
                        handleSignAdvertising(player, line, config);
                    }
                    return;
                }
            }
        }
    }

    private void handleSignAdvertising(Player player, String line, AdvertisingConfig config) {
        antiAdvertising.checkAndHandle(null, player, line, config);
    }

    private List<AdvertisingConfig> getAllAdvertisingConfigs() {
        List<AdvertisingConfig> configs = new ArrayList<>();
        configs.add(plugin.getConfigManager().getIpv4Config());
        configs.add(plugin.getConfigManager().getDomainConfig());
        configs.add(plugin.getConfigManager().getLinksConfig());
        return configs;
    }
}
