package config;

import minealex.tchat.TChat;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;

public class AutoBroadcastManager {

    private final ConfigFile autoBroadcastFile;
    private int time;
    private boolean enabled;
    private final Map<String, Broadcast> broadcasts = new HashMap<>();

    public AutoBroadcastManager(TChat plugin){
        this.autoBroadcastFile = new ConfigFile("autobroadcast.yml", null, plugin);
        this.autoBroadcastFile.registerConfig();
        loadConfig();
    }

    public void loadConfig(){
        FileConfiguration config = autoBroadcastFile.getConfig();

        // General options
        time = config.getInt("options.time");
        enabled = config.getBoolean("options.enabled");

        // Load broadcasts
        broadcasts.clear();
        for (String key : Objects.requireNonNull(config.getConfigurationSection("broadcasts")).getKeys(false)) {
            boolean broadcastEnabled = config.getBoolean("broadcasts." + key + ".enabled");
            List<String> message = config.getStringList("broadcasts." + key + ".message");
            broadcasts.put(key, new Broadcast(broadcastEnabled, message));
        }
    }

    public void reloadConfig(){
        autoBroadcastFile.reloadConfig();
        loadConfig();
    }

    public int getTime() {
        return time;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Map<String, Broadcast> getBroadcasts() {
        return broadcasts;
    }

    public static class Broadcast {
        private final boolean enabled;
        private final List<String> message;

        public Broadcast(boolean enabled, List<String> message) {
            this.enabled = enabled;
            this.message = message;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public List<String> getMessage() {
            return message;
        }
    }
}
