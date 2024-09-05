package blocked;

import config.BannedWordsManager;
import minealex.tchat.TChat;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BannedWords implements Listener {

    private final TChat plugin;
    private final BannedWordsManager bannedWordsManager;

    public BannedWords(TChat plugin, BannedWordsManager bannedWordsManager) {
        this.plugin = plugin;
        this.bannedWordsManager = bannedWordsManager;
    }

    @EventHandler
    public void playerBannedWords(@NotNull AsyncPlayerChatEvent event) {
        if (event.isCancelled()) return;

        Player player = event.getPlayer();
        if (!player.hasPermission(bannedWordsManager.getBypassPermission()) && !player.hasPermission("tchat.admin")) {
            String message = event.getMessage();
            List<String> bannedWords = bannedWordsManager.getBannedWords();
            boolean isBlocked = false;

            for (String word : bannedWords) {
                if (message.toLowerCase().contains(word.toLowerCase())) {

                    if (plugin.getConfigManager().isLogBannedWordsEnabled()) {
                        plugin.getLogsManager().logBannedWords(player.getName(), word);
                    }

                    if ("BLOCK".equalsIgnoreCase(bannedWordsManager.getType())) {
                        List<String> blockedMessages = bannedWordsManager.getBlockedMessages();
                        for (String blockedMessage : blockedMessages) {
                            blockedMessage = translateAndReplace(player, blockedMessage, word);
                            event.getPlayer().sendMessage(blockedMessage);
                        }
                        isBlocked = true;
                    } else if ("CENSOR".equalsIgnoreCase(bannedWordsManager.getType())) {
                        message = censorWord(word, message);
                    }

                    if (bannedWordsManager.getTitleEnabled()) {
                        String title = translateAndReplace(player, bannedWordsManager.getTitle(), word);
                        String subtitle = translateAndReplace(player, bannedWordsManager.getSubTitle(), word);
                        player.sendTitle(title, subtitle, 10, 70, 20);
                    }

                    if (bannedWordsManager.getActionBarEnabled()) {
                        String actionBar = translateAndReplace(player, bannedWordsManager.getActionBar(), word);
                        player.spigot().sendMessage(net.md_5.bungee.api.ChatMessageType.ACTION_BAR, net.md_5.bungee.api.chat.TextComponent.fromLegacyText(actionBar));
                    }

                    if (bannedWordsManager.getSoundEnabled()) {
                        String soundName = bannedWordsManager.getSound();
                        Sound sound = Sound.valueOf(soundName.toUpperCase());
                        player.playSound(player.getLocation(), sound, 1.0F, 1.0F);
                    }

                    if (bannedWordsManager.isParticlesEnabled()) {
                        player.getWorld().spawnParticle(bannedWordsManager.getParticle(), player.getLocation(), bannedWordsManager.getParticles());
                    }
                }
            }

            if (isBlocked) {
                event.setCancelled(true);
            } else {
                event.setMessage(message);
            }
        }
    }

    private @NotNull String censorWord(@NotNull String word, @NotNull String message) {
        String regex = word.replaceAll("\\.", "$0[^a-zA-Z]*");
        String censoredWord = "*".repeat(word.length());

        return message.replaceAll("(?i)" + regex, censoredWord);
    }

    private @NotNull String translateAndReplace(Player player, String text, String word) {
        String translatedText = plugin.getTranslateColors().translateColors(player, text);
        return ChatColor.translateAlternateColorCodes('&', translatedText.replace("{word}", word));
    }
}
