package listeners;

import minealex.tchat.TChat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;

public class CapListener implements Listener {

    private final TChat plugin;

    public CapListener(TChat plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void playerAntiCap(@NotNull AsyncPlayerChatEvent event) {
        if (event.isCancelled()) { return; }

        Player player = event.getPlayer();

        if (!(player.hasPermission("tchat.bypass.anticap") || player.hasPermission("tchat.admin"))) {
            String message = event.getMessage();

            int totalChars = 0;
            int uppercaseChars = 0;

            for (char c : message.toCharArray()) {
                if (Character.isLetter(c)) {
                    totalChars++;
                    if (Character.isUpperCase(c)) {
                        uppercaseChars++;
                    }
                }
            }

            double percentUppercase = totalChars > 0 ? (double) uppercaseChars / totalChars : 0;

            double antiCapPercent = plugin.getAntiCapManager().getAntiCapPercent();

            if (percentUppercase > antiCapPercent) {
                String antiCapMode = plugin.getAntiCapManager().getAntiCapMode();

                switch (antiCapMode) {
                    case "ToLowerCase":
                        event.setMessage(message.toLowerCase());
                        break;

                    case "BLOCK":
                        event.setCancelled(true);
                        if (plugin.getAntiCapManager().isAntiCapMessageEnabled()) {
                            String error = plugin.getMessagesManager().getAntiCapMessage();
                            String prefix = plugin.getMessagesManager().getPrefix();
                            event.getPlayer().sendMessage(plugin.getTranslateColors().translateColors(player, prefix + error));
                        }
                        // If not, Anti-cap message is not enabled
                        break;

                    case "CENSOR":
                        StringBuilder newMessage = new StringBuilder();
                        for (char c : message.toCharArray()) {
                            if (Character.isUpperCase(c)) {
                                newMessage.append('*');
                            } else {
                                newMessage.append(c);
                            }
                        }
                        event.setMessage(newMessage.toString());
                        break;

                    default:
                        plugin.getLogger().warning("Unknown anti-cap mode: " + antiCapMode);
                        break;
                }
            }
        }
    }
}
