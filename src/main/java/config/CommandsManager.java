package config;

import minealex.tchat.TChat;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;

public class CommandsManager {

    private final TChat plugin;
    private final ConfigFile configFile;
    private final Map<String, Command> commands;

    public CommandsManager(TChat plugin) {
        this.plugin = plugin;
        this.configFile = new ConfigFile("commands.yml", null, plugin);
        this.configFile.registerConfig();
        this.commands = new HashMap<>();
        loadConfig();
    }

    public void loadConfig() {
        FileConfiguration config = configFile.getConfig();
        commands.clear();

        if (config.contains("commands")) {
            Set<String> commandKeys = Objects.requireNonNull(config.getConfigurationSection("commands")).getKeys(false);
            for (String key : commandKeys) {
                boolean permissionRequired = config.getBoolean("commands." + key + ".permission-required", false);
                List<String> actions = config.getStringList("commands." + key + ".actions");
                int cooldown = config.getInt("commands." + key + ".cooldown", 0);
                boolean args = config.getBoolean("commands." + key + ".args");
                commands.put(key, new Command(permissionRequired, actions, cooldown, args));
            }
        }
    }

    public void reloadConfig() {
        configFile.reloadConfig();
        loadConfig();
    }

    public Map<String, Command> getCommands() {
        return commands;
    }

    public TChat getPlugin() {
        return plugin;
    }

    public static class Command {
        private final boolean permissionRequired;
        private final List<String> actions;
        private final int cooldown;
        private final boolean args;

        public Command(boolean permissionRequired, List<String> actions, int cooldown, boolean args) {
            this.permissionRequired = permissionRequired;
            this.actions = actions;
            this.cooldown = cooldown;
            this.args = args;
        }

        public boolean isPermissionRequired() {
            return permissionRequired;
        }

        public List<String> getActions() {
            return actions;
        }

        public int getCooldown() {
            return cooldown;
        }

        public boolean isArgs() {
            return args;
        }
    }
}