package listeners;

import config.ConfigManager;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import utils.TranslateHexColorCodes;

public class ChatFormatListener implements Listener {

    private final ConfigManager configManager;
    private final TranslateHexColorCodes translateHexColorCodes;

    public ChatFormatListener(ConfigManager configManager, TranslateHexColorCodes translateHexColorCodes) {
        this.configManager = configManager;
        this.translateHexColorCodes = translateHexColorCodes;
    }

    public void playerFormat(AsyncPlayerChatEvent event) {
        String format = configManager.getFormat();

        String message = format.replace("%player%", event.getPlayer().getName())
                .replace("%message%", event.getMessage());

        Player player = event.getPlayer();
        message = PlaceholderAPI.setPlaceholders(player, message);

        message = TranslateHexColorCodes.translateHexColorCodes("&#", "", message);

        message = ChatColor.translateAlternateColorCodes('&', message);

        event.setFormat(message);
    }
}
