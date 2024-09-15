package utils;

import minealex.tchat.TChat;
import config.CommandTimerManager;
import config.CommandTimerManager.CommandConfig;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CommandTimerSender {

    private final TChat plugin;
    private final CommandTimerManager commandTimerManager;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final List<String> commandKeys = new ArrayList<>();
    private int currentIndex = 0;

    public CommandTimerSender(@NotNull TChat plugin) {
        this.plugin = plugin;
        this.commandTimerManager = plugin.getCommandTimerManager();
        initializeCommands();
    }

    private void initializeCommands() {
        Map<String, CommandConfig> commandConfigMap = commandTimerManager.getCommandConfig();
        commandKeys.addAll(commandConfigMap.keySet());

        long interval = commandTimerManager.getTime();
        scheduler.scheduleAtFixedRate(this::executeNextCommand, 0, interval, TimeUnit.SECONDS);
    }

    private void executeNextCommand() {
        if (commandKeys.isEmpty()) {
            return;
        }

        String currentKey = commandKeys.get(currentIndex);
        CommandConfig config = commandTimerManager.getCommandConfig().get(currentKey);

        if (config != null && config.isEnabled()) {
            for (String command : config.getCommands()) {
                executeCommand(command);
            }
        }

        currentIndex = (currentIndex + 1) % commandKeys.size();
    }

    private void executeCommand(String command) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            if (command.startsWith("[CONSOLE]")) {
                String consoleCommand = command.substring("[CONSOLE]".length()).trim();
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), consoleCommand);
            } else if (command.startsWith("[PLAYER]")) {
                String playerCommand = command.substring("[PLAYER]".length()).trim();
                Bukkit.getOnlinePlayers().stream().findFirst().ifPresent(player -> player.performCommand(playerCommand));
            }
        });
    }

    public void stop() {
        scheduler.shutdown();
    }
}
