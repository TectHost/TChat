package config;

import minealex.tchat.TChat;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;

public class CommandTimerManager {

    private final ConfigFile commandTimerFile;
    private boolean enabled;
    private int time;
    private final Map<String, CommandConfig> commands = new HashMap<>();

    public CommandTimerManager(TChat plugin) {
        this.commandTimerFile = new ConfigFile("command_timer.yml", null, plugin);
        this.commandTimerFile.registerConfig();
        loadConfig();
    }

    public void loadConfig() {
        FileConfiguration config = commandTimerFile.getConfig();

        // Load general options
        enabled = config.getBoolean("options.enabled");
        time = config.getInt("options.time");

        // Load commands
        commands.clear();
        Set<String> commandKeys = Objects.requireNonNull(config.getConfigurationSection("commands")).getKeys(false);
        for (String key : commandKeys) {
            boolean commandEnabled = config.getBoolean("commands." + key + ".enabled");
            List<String> commandActions = config.getStringList("commands." + key + ".commands");
            commands.put(key, new CommandConfig(commandEnabled, commandActions));
        }
    }

    public void reloadConfig() {
        commandTimerFile.reloadConfig();
        loadConfig();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public int getTime() {
        return time;
    }

    public Map<String, CommandConfig> getCommandConfig() {
        return commands;
    }

    public boolean addCommandTimer(String name, int time, List<String> commandList) {
        FileConfiguration config = commandTimerFile.getConfig();

        if (config.getConfigurationSection("commands." + name) != null) {
            return false;
        }

        config.set("commands." + name + ".enabled", true);
        config.set("commands." + name + ".time", time);
        config.set("commands." + name + ".commands", commandList);

        commandTimerFile.saveConfig();

        loadConfig();

        return true;
    }

    public boolean removeCommandTimer(String name) {
        FileConfiguration config = commandTimerFile.getConfig();

        if (config.getConfigurationSection("commands." + name) == null) {
            return false;
        }

        config.set("commands." + name, null);

        commandTimerFile.saveConfig();

        loadConfig();

        return true;
    }

    public static class CommandConfig {
        private final boolean enabled;
        private final List<String> commands;

        public CommandConfig(boolean enabled, List<String> commands) {
            this.enabled = enabled;
            this.commands = commands;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public List<String> getCommands() {
            return commands;
        }
    }
}
