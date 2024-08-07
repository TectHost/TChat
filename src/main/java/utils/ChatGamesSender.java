package utils;

import minealex.tchat.TChat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import config.ChatGamesManager;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ChatGamesSender {

    private final TChat plugin;
    private int currentGameIndex = 0;
    private ChatGamesManager.Game currentGame;
    private BukkitRunnable countdownTask;
    private final Set<String> winners;
    private boolean gameActive;

    public ChatGamesSender(TChat plugin) {
        this.plugin = plugin;
        this.winners = new HashSet<>();
        startNextGame();
    }

    private void startNextGame() {
        String prefix = plugin.getMessagesManager().getPrefix();

        var games = plugin.getChatGamesManager().getGames();
        if (games.isEmpty()) {
            String message = plugin.getMessagesManager().getNoGames();
            sendToAllPlayers(prefix + message);
            return;
        }

        do {
            currentGame = games.get(currentGameIndex);
            currentGameIndex = (currentGameIndex + 1) % games.size();
        } while (!currentGame.isEnabled() && currentGameIndex != 0);

        if (!currentGame.isEnabled()) {
            String message = plugin.getMessagesManager().getNoEnabledGames();
            sendToAllPlayers(prefix + message);
            return;
        }

        assert winners != null;
        winners.clear();
        gameActive = true;

        if (currentGame.getMessages() != null && !currentGame.getMessages().isEmpty()) {
            for (String message : currentGame.getMessages()) {
                if (message.contains("%center%")) {
                    message = message.replace("%center%", "");
                    message = centerText(message);
                }
                sendToAllPlayers(message);
            }
        } else {
            String message = plugin.getMessagesManager().getNoMessages();
            sendToAllPlayers(prefix + message);
        }

        countdownTask = new BukkitRunnable() {
            @Override
            public void run() {
                String message = plugin.getMessagesManager().getTimeFinished();
                sendToAllPlayers(prefix + message);
                endGame();
            }
        };
        countdownTask.runTaskLater(plugin, currentGame.getOptions().getEndTime() * 20L);
    }

    private void endGame() {
        if (countdownTask != null) { countdownTask.cancel(); }

        gameActive = false;

        BukkitRunnable endGameTask = new BukkitRunnable() {
            @Override
            public void run() {
                startNextGame();
            }
        };
        endGameTask.runTaskLater(plugin, currentGame.getOptions().getTime() * 20L);
    }

    public void checkPlayerResponse(Player player, String message) {
        if (currentGame == null || !gameActive) { return; }
        if (winners.contains(player.getName())) { return; }

        if (currentGame.getKeywords() != null && currentGame.getKeywords().contains(message.toLowerCase())) {
            String message1 = plugin.getMessagesManager().getGameWin();
            message1 = message1.replace("%player%", player.getName());
            String prefix = plugin.getMessagesManager().getPrefix();
            sendToAllPlayers(prefix + message1);
            winners.add(player.getName());
            UUID playerId = player.getUniqueId();
            int wins = plugin.getSaveManager().getChatGamesWins(playerId);
            wins++;
            plugin.getSaveManager().setChatGamesWins(playerId, wins);
            executeRewards(player);
            endGame();
        }
    }

    private String centerText(String message) {
        final int maxLength = 56;
        String strippedMessage = ChatColor.stripColor(message);
        int length = strippedMessage.length();

        if (length >= maxLength) {
            return message;
        }

        int spaces = (maxLength - length) / 2;
        StringBuilder centeredMessage = new StringBuilder();

        centeredMessage.append(" ".repeat(spaces));

        centeredMessage.append(message);

        while (centeredMessage.length() < maxLength) {
            centeredMessage.append(" ");
        }

        return centeredMessage.toString();
    }

    private void executeRewards(Player player) {
        if (currentGame.getRewards() != null) {
            for (String reward : currentGame.getRewards()) {
                final String command = reward.replace("%winner%", player.getName());
                Bukkit.getScheduler().runTask(plugin, () -> {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                });
            }
        }
    }

    private void sendToAllPlayers(String message) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(plugin.getTranslateColors().translateColors(player, message));
        }
    }
}
