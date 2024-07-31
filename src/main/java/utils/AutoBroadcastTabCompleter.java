package utils;

import config.AutoBroadcastManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class AutoBroadcastTabCompleter implements TabCompleter {
    private final AutoBroadcastManager autoBroadcastManager;

    public AutoBroadcastTabCompleter(AutoBroadcastManager autoBroadcastManager) {
        this.autoBroadcastManager = autoBroadcastManager;
    }

    @Override
    public @Nullable List<String> onTabComplete(CommandSender sender, @NotNull Command command, @NotNull String label, @Nullable String[] args) {
        if (!sender.hasPermission("tchat.admin.autobroadcast")) { return null; }

        if (args.length == 1) {
            return getStartingSubcommands();
        } else if (args.length == 2 && Objects.requireNonNull(args[0]).equalsIgnoreCase("remove")) {
            return getBroadcastKeys();
        } else if (args.length == 2 && args[0].equalsIgnoreCase("add")) {
            return Arrays.asList("broadcastName1", "broadcastName2");
        }

        return null;
    }

    private List<String> getStartingSubcommands() {
        return Arrays.asList("start", "stop", "restart", "remove", "add");
    }

    private List<String> getBroadcastKeys() {
        return new ArrayList<>(autoBroadcastManager.getBroadcasts().keySet());
    }
}