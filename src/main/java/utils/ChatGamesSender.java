package utils;

import minealex.tchat.TChat;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import config.ChatGamesManager;
import org.jetbrains.annotations.NotNull;

import java.util.*;

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

    public void startNextGame() {
        if (gameActive) { return;}

        String prefix = plugin.getMessagesManager().getPrefix();
        var games = plugin.getChatGamesManager().getGames();

        if (games.isEmpty()) { return; }

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

        showStartEffects();

        if (currentGame.getMessages() != null && !currentGame.getMessages().isEmpty()) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                sendMessagesWithEffects(player, currentGame.getMessages(), currentGame.isHoverEnabled(), currentGame.getHover(), currentGame.isActionEnabled(), currentGame.getAction());
            }
        } else {
            String message = plugin.getMessagesManager().getNoMessages();
            sendToAllPlayers(prefix + message);
        }

        for (String keyword : currentGame.getKeywords()) {
            if (keyword.startsWith("[BREAK]")) {
                startBlockBreakGame(keyword);
                break;
            }
        }

        countdownTask = new BukkitRunnable() {
            @Override
            public void run() {
                sendToAllPlayers(prefix + plugin.getMessagesManager().getTimeFinished());
                endGame();
            }
        };
        countdownTask.runTaskLater(plugin, currentGame.getOptions().getEndTime() * 20L);
    }

    private void endGame() {
        stopGame();
        showEndEffects();

        BukkitRunnable endGameTask = new BukkitRunnable() {
            @Override
            public void run() {
                startNextGame();
            }
        };
        endGameTask.runTaskLater(plugin, currentGame.getOptions().getTime() * 20L);
    }

    private void showStartEffects() {
        showEffects(currentGame.getStartEffects(), null);
    }

    private void showEndEffects() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (winners.contains(player.getName())) {
                showEffects(currentGame.getWinnerEffects(), player);
            } else {
                showEffects(currentGame.getEndEffects(), player);
            }
        }
    }

    private void showEffects(ChatGamesManager.@NotNull Effects effects, Player winner) {
        TranslateColors translateColors = plugin.getTranslateColors();
        if (translateColors == null) { return; }

        if (effects.getTitle().isEnabled()) {
            String titleText = translateColors.translateColors(winner, effects.getTitle().getText());
            String subtitleText = translateColors.translateColors(winner, effects.getTitle().getSubtitle());

            if (titleText.contains("%winner%") && winner != null) {
                titleText = titleText.replace("%winner%", winner.getName());
            }
            if (subtitleText.contains("%winner%") && winner != null) {
                subtitleText = subtitleText.replace("%winner%", winner.getName());
            }

            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendTitle(
                        titleText,
                        subtitleText,
                        effects.getTitle().getFadeIn(),
                        effects.getTitle().getStay(),
                        effects.getTitle().getFadeOut()
                );
            }
        }

        if (effects.getSound().isEnabled()) {
            try {
                Sound sound = Sound.valueOf(effects.getSound().getName().toUpperCase());
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.playSound(player.getLocation(), sound, effects.getSound().getVolume(), effects.getSound().getPitch());
                }
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Invalid sound name: " + effects.getSound().getName());
            }
        }

        if (effects.getParticle().isEnabled()) {
            try {
                Particle particle = Particle.valueOf(effects.getParticle().getName().toUpperCase());
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.getWorld().spawnParticle(
                            particle,
                            player.getLocation(),
                            effects.getParticle().getCount(),
                            0.5, 0.5, 0.5,
                            effects.getParticle().getSpeed()
                    );
                }
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Invalid particle name: " + effects.getParticle().getName());
            }
        }

        if (effects.getActionBar().isEnabled()) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                String bar = effects.getActionBar().getText();
                player.spigot().sendMessage(net.md_5.bungee.api.ChatMessageType.ACTION_BAR, net.md_5.bungee.api.chat.TextComponent.fromLegacyText(plugin.getTranslateColors().translateColors(player, bar)));
            }
        }
    }

    public void checkPlayerResponse(Player player, String message) {
        if (currentGame == null || !gameActive) { return; }
        if (winners.contains(player.getName())) { return; }

        List<String> keywords = currentGame.getKeywords();

        for (String keyword : keywords) {
            if (keyword.startsWith("[WRITE]")) {
                String cleanedKeyword = keyword.substring(7).trim();
                if (cleanedKeyword.equalsIgnoreCase(message)) {
                    notifyWinner(player);
                    return;
                }
            }
        }
    }

    public void notifyWinner(@NotNull Player player) {
        String winMessage = plugin.getMessagesManager().getGameWin();
        winMessage = winMessage.replace("%player%", player.getName());
        String prefix = plugin.getMessagesManager().getPrefix();
        sendToAllPlayers(prefix + winMessage);
        winners.add(player.getName());

        UUID playerId = player.getUniqueId();
        int wins = plugin.getSaveManager().getChatGamesWins(playerId);
        wins++;
        plugin.getSaveManager().setChatGamesWins(playerId, wins);
        executeRewards(player);

        showEffects(currentGame.getWinnerEffects(), player);
        showEffects(currentGame.getEndEffects(), null);

        endGame();
    }

    private void startBlockBreakGame(@NotNull String keyword) {
        String[] parts = keyword.split(" ");
        if (parts.length == 3) {
            Material blockType = Material.matchMaterial(parts[1].toUpperCase());
            int requiredAmount;

            try {
                requiredAmount = Integer.parseInt(parts[2]);
            } catch (NumberFormatException e) {
                plugin.getLogger().warning("Invalid amount specified in [BREAK] command.");
                return;
            }

            if (blockType != null) {
                new BlockBreakGame(plugin, blockType, requiredAmount);
            } else {
                plugin.getLogger().warning("Invalid block type specified in [BREAK] command.");
            }
        } else {
            plugin.getLogger().warning("Invalid format for [BREAK] command. Expected: [BREAK] BLOCK_TYPE AMOUNT");
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

    public void stopGame() {
        if (!gameActive) {
            return;
        }

        if (countdownTask != null) {
            countdownTask.cancel();
            countdownTask = null;
        }

        gameActive = false;
    }

    public void restartGame() {
        stopGame();
        currentGameIndex = 0;
        startNextGame();
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

    private void applyHoverAndAction(TextComponent textComponent, boolean hoverEnabled, List<String> hoverLines, boolean actionEnabled, String action, String playerName) {
        if (hoverEnabled) {
            List<String> translatedHoverLines = new ArrayList<>();
            for (String line : hoverLines) {
                String translatedLine = plugin.getTranslateColors().translateColors(null, line);
                translatedHoverLines.add(translatedLine);
            }

            String hoverText = String.join("\n", translatedHoverLines);
            textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hoverText).create()));
        }
        if (actionEnabled) {
            applyClickAction(textComponent, action, playerName);
        }
    }

    private @NotNull TextComponent processMessage(@NotNull String message, boolean hoverEnabled, List<String> hoverLines, boolean actionEnabled, String action, String playerName) {
        if (message.contains("%center%")) {
            message = message.replace("%center%", "");
            message = centerText(message);
        }

        TextComponent textComponent = new TextComponent(message);
        applyHoverAndAction(textComponent, hoverEnabled, hoverLines, actionEnabled, action, playerName);
        return textComponent;
    }

    private void sendMessagesWithEffects(Player player, @NotNull List<String> messages, boolean hoverEnabled, List<String> hoverLines, boolean actionEnabled, String action) {
        for (String message : messages) {
            message = plugin.getTranslateColors().translateColors(player, message);
            TextComponent textComponent = processMessage(message, hoverEnabled, hoverLines, actionEnabled, action, player.getName());
            player.spigot().sendMessage(textComponent);
        }
    }

    private void applyClickAction(TextComponent component, @NotNull String clickAction, String playerName) {
        String replacedAction = clickAction.replace("%player%", playerName);

        if (replacedAction.startsWith("[EXECUTE]")) {
            String command = replacedAction.substring("[EXECUTE] ".length());
            component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
        } else if (replacedAction.startsWith("[OPEN]")) {
            String url = replacedAction.substring("[OPEN] ".length());
            component.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url));
        } else if (replacedAction.startsWith("[SUGGEST]")) {
            String command = replacedAction.substring("[SUGGEST] ".length());
            component.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command));
        }
    }

    public boolean isGameActive() { return gameActive; }
}
