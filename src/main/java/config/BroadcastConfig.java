package config;

import minealex.tchat.TChat;
import org.bukkit.configuration.file.FileConfiguration;

public class BroadcastConfig {

    private final ConfigFile configFile;

    private boolean broadcastEnabled;
    private String broadcastFormat;

    private boolean warningEnabled;
    private String warningFormat;

    private boolean announcementEnabled;
    private String announcementFormat;

    public BroadcastConfig(TChat plugin){
        this.configFile = new ConfigFile("broadcast.yml", "modules", plugin);
        this.configFile.registerConfig();
        loadConfig();
    }

    public void loadConfig(){
        FileConfiguration config = configFile.getConfig();

        broadcastEnabled = config.getBoolean("broadcast.broadcast.enabled");
        if (broadcastEnabled) {
            broadcastFormat = config.getString("broadcast.broadcast.format");
        }

        warningEnabled = config.getBoolean("broadcast.warning.enabled");
        if (warningEnabled) {
            warningFormat = config.getString("broadcast.warning.format");
        }

        announcementEnabled = config.getBoolean("broadcast.announcement.enabled");
        if (announcementEnabled) {
            announcementFormat = config.getString("broadcast.announcement.format");
        }
    }

    public void reloadConfig(){
        configFile.reloadConfig();
        loadConfig();
    }

    public String getAnnouncementFormat() { return announcementFormat; }
    public boolean isAnnouncementEnabled() { return announcementEnabled; }

    public boolean isWarningEnabled() { return warningEnabled; }
    public String getWarningFormat() { return warningFormat; }

    public String getBroadcastFormat() { return broadcastFormat; }
    public boolean isBroadcastEnabled() { return broadcastEnabled; }
}
