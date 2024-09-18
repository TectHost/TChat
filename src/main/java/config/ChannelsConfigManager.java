package config;

import minealex.tchat.TChat;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ChannelsConfigManager {

    private final ConfigFile channelsFile;
    private final Map<String, Channel> channels = new HashMap<>();

    public ChannelsConfigManager(TChat plugin){
        this.channelsFile = new ConfigFile("channels.yml", null, plugin);
        this.channelsFile.registerConfig();
        loadConfig();
    }

    public void loadConfig(){
        FileConfiguration config = channelsFile.getConfig();

        channels.clear();
        for (String channelName : Objects.requireNonNull(config.getConfigurationSection("channels")).getKeys(false)) {
            String path = "channels." + channelName + ".";
            boolean enabled = config.getBoolean(path + "enabled");
            String permission = config.getString(path + "permission");
            boolean formatEnabled = config.getBoolean(path + "format.enabled");
            String format = config.getString(path + "format.format");
            int messageMode = config.getInt(path + "message-mode");
            int announceMode = config.getInt(path + "announce-mode");
            int pLimit = config.getInt(path + ".channel-limit", 0);
            boolean discordEnabled = config.getBoolean(path + "discord.enabled");
            String discordHook = config.getString(path + "discord.hook", "");
            boolean cooldownEnabled = config.getBoolean(path + "cooldown.enabled");
            int cooldown = config.getInt(path + "cooldown.cooldown");

            channels.put(channelName, new Channel(enabled, permission, format, formatEnabled, messageMode, announceMode, pLimit, discordEnabled, discordHook, cooldownEnabled, cooldown));
        }
    }

    public void reloadConfig(){
        channelsFile.reloadConfig();
        loadConfig();
    }

    public Map<String, Channel> getChannels() { return channels; }

    public Channel getChannel(String name) {
        return channels.get(name);
    }

    public record Channel(boolean enabled, String permission, String format, boolean formatEnabled, int messageMode,
                          int announceMode, int pLimit, boolean discordEnabled, String discordHook, boolean cooldownEnabled,
                          int cooldown) {
    }
}
