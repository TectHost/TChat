package utils;

import minealex.tchat.TChat;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import config.CommandProgrammerManager;
import org.jetbrains.annotations.NotNull;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandProgrammerSender {

    private final TChat plugin;
    private final CommandProgrammerManager manager;
    private final Map<String, LocalDateTime> lastExecutionTimes = new HashMap<>();

    public CommandProgrammerSender(TChat plugin, CommandProgrammerManager manager) {
        this.plugin = plugin;
        this.manager = manager;
        startScheduler();
    }

    private void startScheduler() {
        if (plugin.getCommandProgrammerManager().isEnabled()) {return;}

        new BukkitRunnable() {
            @Override
            public void run() {
                checkAndExecuteCommands();
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    private void checkAndExecuteCommands() {
        LocalDateTime now = LocalDateTime.now();
        boolean commandExecuted = false;

        for (CommandProgrammerManager.CommandSchedule schedule : manager.getHourlyCommands()) {
            if (schedule.getMinute() == now.getMinute() && checkPlayers(schedule.getPlayers())) {
                LocalDateTime lastExecution = lastExecutionTimes.get("hourly");
                if (lastExecution == null || !lastExecution.toLocalDate().equals(now.toLocalDate()) ||
                        lastExecution.getHour() != now.getHour()) {
                    executeCommands(schedule.getCommands());
                    lastExecutionTimes.put("hourly", now);
                    commandExecuted = true;
                }
            }
        }

        for (CommandProgrammerManager.CommandSchedule schedule : manager.getDailyCommands()) {
            if (schedule.getHour() == now.getHour() && schedule.getMinute() == now.getMinute() &&
                    now.getDayOfMonth() == schedule.getDay() && checkPlayers(schedule.getPlayers())) {
                LocalDateTime lastExecution = lastExecutionTimes.get("daily");
                if (lastExecution == null || !lastExecution.toLocalDate().equals(now.toLocalDate())) {
                    executeCommands(schedule.getCommands());
                    lastExecutionTimes.put("daily", now);
                    commandExecuted = true;
                }
            }
        }

        for (CommandProgrammerManager.CommandSchedule schedule : manager.getWeeklyCommands()) {
            if (schedule.getHour() == now.getHour() && schedule.getMinute() == now.getMinute() &&
                    now.getDayOfWeek() == DayOfWeek.of(schedule.getDay()) && checkPlayers(schedule.getPlayers())) {
                LocalDateTime lastExecution = lastExecutionTimes.get("weekly");
                if (lastExecution == null || !lastExecution.toLocalDate().equals(now.toLocalDate()) ||
                        lastExecution.getDayOfWeek() != now.getDayOfWeek()) {
                    executeCommands(schedule.getCommands());
                    lastExecutionTimes.put("weekly", now);
                    commandExecuted = true;
                }
            }
        }

        for (CommandProgrammerManager.CommandSchedule schedule : manager.getMonthlyCommands()) {
            if (schedule.getHour() == now.getHour() && schedule.getMinute() == now.getMinute() &&
                    now.getDayOfMonth() == schedule.getDay() && now.getMonth() == Month.of(schedule.getMonth()) &&
                    checkPlayers(schedule.getPlayers())) {
                LocalDateTime lastExecution = lastExecutionTimes.get("monthly");
                if (lastExecution == null || !lastExecution.toLocalDate().equals(now.toLocalDate()) ||
                        lastExecution.getMonth() != now.getMonth() ||
                        lastExecution.getDayOfMonth() != now.getDayOfMonth()) {
                    executeCommands(schedule.getCommands());
                    lastExecutionTimes.put("monthly", now);
                    commandExecuted = true;
                }
            }
        }

        for (CommandProgrammerManager.CommandSchedule schedule : manager.getYearlyCommands()) {
            if (schedule.getHour() == now.getHour() && schedule.getMinute() == now.getMinute() &&
                    now.getDayOfMonth() == schedule.getDay() && now.getMonth() == Month.of(schedule.getMonth()) &&
                    checkPlayers(schedule.getPlayers())) {
                LocalDateTime lastExecution = lastExecutionTimes.get("yearly");
                if (lastExecution == null || !lastExecution.toLocalDate().equals(now.toLocalDate()) ||
                        lastExecution.getMonth() != now.getMonth() ||
                        lastExecution.getDayOfMonth() != now.getDayOfMonth()) {
                    executeCommands(schedule.getCommands());
                    lastExecutionTimes.put("yearly", now);
                    commandExecuted = true;
                }
            }
        }

        if (commandExecuted) {
            Bukkit.getLogger().info("Commands executed at " + now);
        }
    }

    private boolean checkPlayers(int requiredPlayers) {
        return requiredPlayers <= Bukkit.getOnlinePlayers().size();
    }

    private void executeCommands(@NotNull List<String> commands) {
        for (String command : commands) {
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
        }
    }
}
