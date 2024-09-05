package commands;

import config.SaveManager;
import minealex.tchat.TChat;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class MuteCommand implements CommandExecutor {

    private final TChat plugin;
    private final SaveManager saveManager;

    public MuteCommand(@NotNull TChat plugin) {
        this.plugin = plugin;
        this.saveManager = plugin.getSaveManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        String prefix = plugin.getMessagesManager().getPrefix();
        if (!(sender.hasPermission("tchat.admin.mute"))) {
            String m = plugin.getMessagesManager().getNoPermission();
            sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + m));
            return true;
        }

        if (args.length < 1) {
            String m = plugin.getMessagesManager().getMuteUsage();
            sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + m));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            String m = plugin.getMessagesManager().getPlayerNotFound();
            sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + m));
            return true;
        }

        UUID targetId = target.getUniqueId();
        if (args.length == 1) {
            saveManager.mutePlayer(targetId, -1);
            String m = plugin.getMessagesManager().getMutePermanent().replace("%player%", target.getName());
            sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + m));
        } else {
            try {
                long duration = parseDuration(args[1]);
                saveManager.mutePlayer(targetId, duration);
                String m = plugin.getMessagesManager().getMuteTemp().replace("%player%", target.getName()).replace("%time%", args[1]);
                sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + m));
            } catch (IllegalArgumentException e) {
                String m = plugin.getMessagesManager().getMuteInvalidDuration();
                sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + m));
            }
        }

        return true;
    }

    private long parseDuration(@NotNull String duration) throws IllegalArgumentException {
        char timeUnit = duration.charAt(duration.length() - 1);
        long timeValue = Long.parseLong(duration.substring(0, duration.length() - 1));

        String p = plugin.getMessagesManager().getPrefix();
        String m = plugin.getMessagesManager().getMuteInvalidUnit();

        return switch (timeUnit) {
            case 's' -> TimeUnit.SECONDS.toMillis(timeValue);
            case 'm' -> TimeUnit.MINUTES.toMillis(timeValue);
            case 'h' -> TimeUnit.HOURS.toMillis(timeValue);
            case 'd' -> TimeUnit.DAYS.toMillis(timeValue);
            case 'w' -> TimeUnit.DAYS.toMillis(timeValue * 7);
            case 'M' -> TimeUnit.DAYS.toMillis(timeValue * 30);
            default -> throw new IllegalArgumentException(plugin.getTranslateColors().translateColors(null, p + m));
        };
    }
}
