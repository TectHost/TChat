package utils;

import minealex.tchat.TChat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import config.ChatGamesManager;

public class ChatGamesSender {

    private final TChat plugin;
    private int currentGameIndex = 0;
    private ChatGamesManager.Game currentGame;
    private BukkitRunnable countdownTask;
    private BukkitRunnable endGameTask;

    public ChatGamesSender(TChat plugin) {
        this.plugin = plugin;
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

        if (currentGame.getMessages() != null && !currentGame.getMessages().isEmpty()) {
            for (String message : currentGame.getMessages()) {
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
        if (countdownTask != null) {
            countdownTask.cancel();
        }

        endGameTask = new BukkitRunnable() {
            @Override
            public void run() {
                startNextGame();
            }
        };
        endGameTask.runTaskLater(plugin, currentGame.getOptions().getTime() * 20L);
    }

    public void checkPlayerResponse(Player player, String message) {
        if (currentGame == null) {
            return;
        }

        if (currentGame.getKeywords() != null && currentGame.getKeywords().contains(message.toLowerCase())) {
            String message1 = plugin.getMessagesManager().getGameWin();
            message1 = message1.replace("%player%", player.getName());
            String prefix = plugin.getMessagesManager().getPrefix();
            sendToAllPlayers(prefix + message1);
            executeRewards(player);
            endGame();
        }
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
