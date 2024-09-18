package utils;

import minealex.tchat.TChat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Poll {
    private static Poll currentPoll;

    private final TChat plugin;
    private final String title;
    private final long endTime;
    private final Map<Player, String> votes;
    private final String[] options;
    private final int minPlayers;
    private final int maxPlayers;

    public Poll(TChat plugin, String title, int duration, String[] options, int minPlayers, int maxPlayers) {
        this.plugin = plugin;
        this.title = title;
        this.endTime = System.currentTimeMillis() + (duration * 1000L);
        this.votes = new HashMap<>();
        this.options = options;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
    }


    public String getTitle() {
        return title;
    }

    public boolean hasEnded() {
        return System.currentTimeMillis() > endTime;
    }

    public void vote(Player player, String option) {
        if (!hasEnded() && isValidOption(option)) {
            votes.put(player, option);

            if (votes.size() >= maxPlayers) {
                finalizePoll();
            }
        }
    }

    public String[] getOptions() {
        return options;
    }

    public Map<String, Integer> getResults() {
        Map<String, Integer> results = new HashMap<>();
        for (String option : options) {
            results.put(option, 0);
        }
        for (String vote : votes.values()) {
            results.put(vote, results.get(vote) + 1);
        }
        return results;
    }

    public boolean isValidOption(String option) {
        for (String o : options) {
            if (o.equalsIgnoreCase(option)) {
                return true;
            }
        }
        return false;
    }

    public void finalizePoll() {
        if (votes.size() < minPlayers) { return; }

        if (!hasEnded()) { return; }

        Map<String, Integer> results = getResults();

        String optionLine = plugin.getMessagesManager().getOptionLine();
        String progressBarFormat = plugin.getMessagesManager().getProgressBar();

        String endTitle = plugin.getMessagesManager().getEndTitle();
        String endTextTitle = plugin.getMessagesManager().getEndTextTitle();

        endTitle = endTitle != null ? endTitle : "";
        endTextTitle = endTextTitle != null ? endTextTitle : "";

        StringBuilder resultsMessage = new StringBuilder(plugin.getTranslateColors().translateColors(null, endTitle));
        resultsMessage.append(plugin.getTranslateColors().translateColors(null, endTextTitle)).append(getTitle()).append("\n");

        int totalVotes = results.values().stream().mapToInt(Integer::intValue).sum();
        final int BAR_LENGTH = plugin.getConfigManager().getPollBar();

        for (Map.Entry<String, Integer> entry : results.entrySet()) {
            String option = entry.getKey();
            int voteCount = entry.getValue();
            double percentage = totalVotes > 0 ? (voteCount * 100.0 / totalVotes) : 0;
            int filledBlocks = (int) (percentage / 100.0 * BAR_LENGTH);
            int emptyBlocks = BAR_LENGTH - filledBlocks;

            String progressBar = ChatColor.translateAlternateColorCodes('&',
                    progressBarFormat.replace("%filled%", String.valueOf(plugin.getConfigManager().getPollFill()).repeat(filledBlocks))
                            .replace("%empty%", String.valueOf(plugin.getConfigManager().getPollEmpty()).repeat(emptyBlocks)));

            String formattedOptionLine = ChatColor.translateAlternateColorCodes('&',
                    optionLine.replace("%option%", option)
                            .replace("%votes%", String.valueOf(voteCount))
                            .replace("%.2f%%", String.format("%.2f", percentage)));

            resultsMessage.append(formattedOptionLine)
                    .append("\n")
                    .append(progressBar)
                    .append("\n");
        }

        String messageToSend = plugin.getTranslateColors().translateColors(null, resultsMessage.toString());
        Bukkit.broadcastMessage(Objects.requireNonNullElse(messageToSend, resultsMessage.toString()).trim());
        Poll.setCurrentPoll(null);
    }

    public static Poll getCurrentPoll() {
        return currentPoll;
    }

    public static void setCurrentPoll(Poll poll) {
        currentPoll = poll;
    }
}
