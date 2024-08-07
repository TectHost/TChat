package blocked;

import config.BannedWordsManager;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;
import utils.TranslateHexColorCodes;

import java.util.List;

import static utils.TranslateHexColorCodes.translateHexColorCodes;

public class BannedWords implements Listener {

    private final BannedWordsManager bannedWordsManager;

    public BannedWords(BannedWordsManager bannedWordsManager, TranslateHexColorCodes translateHexColorCodes) {
        this.bannedWordsManager = bannedWordsManager;
    }

    @EventHandler
    public void playerBannedWords(AsyncPlayerChatEvent event) {
        if (event.isCancelled()) return;

        Player player = event.getPlayer();
        if (!player.hasPermission(bannedWordsManager.getBypassPermission()) || !player.hasPermission("tchat.admin")) {
            String message = event.getMessage().toLowerCase();
            List<String> bannedWords = bannedWordsManager.getBannedWords();

            for (String word : bannedWords) {
                if (message.contains(word.toLowerCase())) {
                    if ("BLOCK".equalsIgnoreCase(bannedWordsManager.getType())) {
                        List<String> blockedMessages = bannedWordsManager.getBlockedMessages();
                        for (String blockedMessage : blockedMessages) {
                            blockedMessage = translateAndReplace(blockedMessage, word);
                            event.getPlayer().sendMessage(blockedMessage);
                        }
                        event.setCancelled(true);

                        if (bannedWordsManager.getTitleEnabled()) {
                            String title = translateAndReplace(bannedWordsManager.getTitle(), word);
                            String subtitle = translateAndReplace(bannedWordsManager.getSubTitle(), word);
                            sendTitle(event.getPlayer(), title, subtitle);
                        }

                        if (bannedWordsManager.getActionBarEnabled()) {
                            String actionBar = translateAndReplace(bannedWordsManager.getActionBar(), word);
                            sendActionBar(event.getPlayer(), actionBar);
                        }

                        if (bannedWordsManager.getSoundEnabled()) {
                            playSound(event.getPlayer(), bannedWordsManager.getSound());
                        }

                        if (bannedWordsManager.isParticlesEnabled()) {
                            showParticles(event.getPlayer());
                        }

                        return;
                    } else if ("CENSOR".equalsIgnoreCase(bannedWordsManager.getType())) {
                        String censoredMessage = censorWord(word, message);
                        event.setMessage(censoredMessage);

                        if (bannedWordsManager.getTitleEnabled()) {
                            String title = translateAndReplace(bannedWordsManager.getTitle(), word);
                            String subtitle = translateAndReplace(bannedWordsManager.getSubTitle(), word);
                            sendTitle(event.getPlayer(), title, subtitle);
                        }

                        if (bannedWordsManager.getActionBarEnabled()) {
                            String actionBar = translateAndReplace(bannedWordsManager.getActionBar(), word);
                            sendActionBar(event.getPlayer(), actionBar);
                        }

                        if (bannedWordsManager.getSoundEnabled()) {
                            playSound(event.getPlayer(), bannedWordsManager.getSound());
                        }

                        if (bannedWordsManager.isParticlesEnabled()) {
                            showParticles(event.getPlayer());
                        }

                        return;
                    }
                }
            }
        }
    }

    private String censorWord(String word, String message) {
        String censoredWord = "*".repeat(word.length());
        return message.replaceAll("(?i)\\b" + word + "\\b", censoredWord);
    }

    private String translateAndReplace(String text, String word) {
        String translatedText = translateHexColorCodes("&#", "", text);
        return ChatColor.translateAlternateColorCodes('&', translatedText.replace("{word}", word));
    }

    private void sendTitle(Player player, String title, String subtitle) {
        new BukkitRunnable() {
            @Override
            public void run() {
                player.sendTitle(title, subtitle, 10, 70, 20);
            }
        }.runTask(bannedWordsManager.getPlugin());
    }

    private void sendActionBar(Player player, String message) {
        new BukkitRunnable() {
            @Override
            public void run() {
                player.spigot().sendMessage(net.md_5.bungee.api.ChatMessageType.ACTION_BAR, net.md_5.bungee.api.chat.TextComponent.fromLegacyText(message));
            }
        }.runTask(bannedWordsManager.getPlugin());
    }

    private void showParticles(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                player.getWorld().spawnParticle(bannedWordsManager.getParticle(), player.getLocation(), bannedWordsManager.getParticles());
            }
        }.runTask(bannedWordsManager.getPlugin());
    }

    private void playSound(Player player, String soundName) {
        Sound sound = Sound.valueOf(soundName.toUpperCase());
        player.playSound(player.getLocation(), sound, 1.0F, 1.0F);
    }
}
