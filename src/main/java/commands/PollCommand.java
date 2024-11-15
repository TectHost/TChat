package commands;

import minealex.tchat.TChat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import utils.Poll;

import java.util.Arrays;

public class PollCommand implements CommandExecutor {
    private final TChat plugin;

    public PollCommand(TChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {
        String prefix = plugin.getMessagesManager().getPrefix();
        if (args.length < 2) {
            String message = plugin.getMessagesManager().getUsagePoll();
            sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + message));
            return false;
        }

        String subCommand = args[0].toLowerCase();
        switch (subCommand) {
            case "create" -> {
                return handleCreateCommand(sender, args);
            }
            case "vote" -> {
                return handleVoteCommand(sender, args);
            }
            case "finish" -> {
                return handleFinishCommand(sender);
            }
            default -> {
                String message = plugin.getMessagesManager().getUsagePoll();
                sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + message));
                return false;
            }
        }
    }

    private boolean handleFinishCommand(@NotNull CommandSender sender) {
        String prefix = plugin.getMessagesManager().getPrefix();

        if (sender.hasPermission("tchat.poll.finish") || sender.hasPermission("tchat.admin")) {
            Poll currentPoll = Poll.getCurrentPoll();

            if (currentPoll == null) {
                String message = plugin.getMessagesManager().getNoPoll();
                sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + message));
                return false;
            }

            currentPoll.finalizePoll();
            String message = plugin.getMessagesManager().getPollFinish();
            sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + message));
            return true;

        } else {
            String message = plugin.getMessagesManager().getNoPermission();
            sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + message));
            return false;
        }
    }

    private boolean handleCreateCommand(@NotNull CommandSender sender, String[] args) {
        String prefix = plugin.getMessagesManager().getPrefix();
        if (sender.hasPermission("tchat.poll.create") || sender.hasPermission("tchat.admin")) {
            if (!(sender instanceof Player player)) {
                String message = plugin.getMessagesManager().getNoPlayer();
                sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + message));
                return false;
            }

            if (args.length < 3) {
                String message = plugin.getMessagesManager().getUsagePollCreate();
                sender.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
                return false;
            }

            int duration;
            try {
                duration = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                String message = plugin.getMessagesManager().getDurationNumber();
                sender.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
                return false;
            }

            String combinedArgs = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
            String[] parts = combinedArgs.split("\\|", 4);

            if (parts.length < 2) {
                String message = plugin.getMessagesManager().getUsagePollCreate();
                sender.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
                return false;
            }

            String title = parts[0].trim();
            String[] options = parts[1].trim().split("\\s+");
            int minPlayer = Integer.parseInt(parts[2].trim());
            int maxPlayer = Integer.parseInt(parts[3].trim());

            if (options.length < 1) {
                String message = plugin.getMessagesManager().getOneOption();
                sender.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
                return false;
            }

            Poll newPoll = new Poll(plugin, title, duration, options, minPlayer, maxPlayer);
            Poll.setCurrentPoll(newPoll);

            String message = plugin.getMessagesManager().getPollCreate();
            message = message.replace("%title%", title);
            String durationString = String.valueOf(duration);
            message = message.replace("%seconds%", durationString);
            sender.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));

            String startTitle = plugin.getMessagesManager().getStartTitle();
            String startTextTitle = plugin.getMessagesManager().getStartText();

            startTitle = startTitle != null ? startTitle : "";
            startTextTitle = startTextTitle != null ? startTextTitle : "";

            StringBuilder resultsMessage = new StringBuilder(plugin.getTranslateColors().translateColors(null, startTitle));
            resultsMessage.append(plugin.getTranslateColors().translateColors(null, startTextTitle)).append(title).append("\n");

            final int BAR_LENGTH = plugin.getPollsConfig().getPollBar();
            for (String option : options) {
                resultsMessage.append(ChatColor.translateAlternateColorCodes('&',
                                plugin.getMessagesManager().getStartOptionLine()
                                        .replace("%option%", option)
                                        .replace("%votes%", "0")
                                        .replace("%.2f%%", "0.00")))
                        .append("\n")
                        .append(ChatColor.translateAlternateColorCodes('&',
                                plugin.getMessagesManager().getStartProgressBar()
                                        .replace("%filled%", String.valueOf('░').repeat(BAR_LENGTH))
                                        .replace("%empty%", "")))
                        .append("\n");
            }

            Bukkit.broadcastMessage(resultsMessage.toString().trim());
        } else {
            String message = plugin.getMessagesManager().getNoPermission();
            sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + message));
        }
        return true;
    }

    private boolean handleVoteCommand(@NotNull CommandSender sender, String[] args) {
        String prefix = plugin.getMessagesManager().getPrefix();
        if (sender.hasPermission("tchat.admin") || sender.hasPermission("tchat.poll.vote")) {
            if (!(sender instanceof Player player)) {
                String message = plugin.getMessagesManager().getNoPlayer();
                sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + message));
                return false;
            }

            if (args.length < 2) {
                String message = plugin.getMessagesManager().getUsagePollVote();
                sender.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
                return false;
            }

            Poll currentPoll = Poll.getCurrentPoll();
            if (currentPoll == null) {
                String message = plugin.getMessagesManager().getNoPoll();
                sender.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
                return false;
            }

            String option = args[1];
            if (!currentPoll.isValidOption(option)) {
                String message = plugin.getMessagesManager().getInvalidOptionPoll();
                message = message.replace("%options%", String.join(", ", currentPoll.getOptions()));
                sender.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
                return false;
            }

            currentPoll.vote(player, option);
            String message = plugin.getMessagesManager().getVotePoll();
            message = message.replace("%option%", option);
            sender.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));

            String updateTitle = plugin.getMessagesManager().getUpdateTitle();
            String updateText = plugin.getMessagesManager().getUpdateText();

            updateTitle = updateTitle != null ? updateTitle : "";
            updateText = updateText != null ? updateText : "";

            StringBuilder resultsMessage = new StringBuilder(plugin.getTranslateColors().translateColors(null, updateTitle));
            resultsMessage.append(plugin.getTranslateColors().translateColors(null, updateText)).append(currentPoll.getTitle()).append("\n");

            int totalVotes = currentPoll.getResults().values().stream().mapToInt(Integer::intValue).sum();
            final int BAR_LENGTH = plugin.getPollsConfig().getPollBar();

            for (String opt : currentPoll.getOptions()) {
                int voteCount = currentPoll.getResults().getOrDefault(opt, 0);
                double percentage = totalVotes > 0 ? (voteCount * 100.0 / totalVotes) : 0;
                int filledBlocks = (int) (percentage / 100.0 * BAR_LENGTH);
                int emptyBlocks = BAR_LENGTH - filledBlocks;

                String progressBar = ChatColor.translateAlternateColorCodes('&',
                        plugin.getMessagesManager().getUpdateProgressBar()
                                .replace("%filled%", String.valueOf(plugin.getPollsConfig().getPollFill()).repeat(filledBlocks))
                                .replace("%empty%", String.valueOf(plugin.getPollsConfig().getPollEmpty()).repeat(emptyBlocks)));

                resultsMessage.append(ChatColor.translateAlternateColorCodes('&',
                                plugin.getMessagesManager().getUpdateOptionLine()
                                        .replace("%option%", opt)
                                        .replace("%votes%", String.valueOf(voteCount))
                                        .replace("%.2f%%", String.format("%.2f", percentage))))
                        .append("\n")
                        .append(progressBar)
                        .append("\n");
            }

            Bukkit.broadcastMessage(resultsMessage.toString().trim());
        } else {
            String message = plugin.getMessagesManager().getNoPermission();
            sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + message));
        }
        return true;
    }
}