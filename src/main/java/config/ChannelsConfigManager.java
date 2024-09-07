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
            boolean discordEnabled = config.getBoolean(path + "discord.enabled");
            String discordHook = config.getString(path + "discord.hook", "");
            boolean cooldownEnabled = config.getBoolean(path + "cooldown.enabled");
            int cooldown = config.getInt(path + "cooldown.cooldown");

            channels.put(channelName, new Channel(enabled, permission, format, formatEnabled, messageMode, announceMode, discordEnabled, discordHook, cooldownEnabled, cooldown));
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

    public static class Channel {
        private final boolean enabled;
        private final String permission;
        private final String format;
        private final boolean formatEnabled;
        private final int messageMode;
        private final int announceMode;
        private final boolean discordEnabled;
        private final String discordHook;
        private final boolean cooldownEnabled;
        private final int cooldown;

        public Channel(boolean enabled, String permission, String format, boolean formatEnabled, int messageMode, int announceMode, boolean discordEnabled, String discordHook, boolean cooldownEnabled, int cooldown) {
            this.enabled = enabled;
            this.permission = permission;
            this.format = format;
            this.formatEnabled = formatEnabled;
            this.messageMode = messageMode;
            this.announceMode = announceMode;
            this.discordEnabled = discordEnabled;
            this.discordHook = discordHook;
            this.cooldownEnabled = cooldownEnabled;
            this.cooldown = cooldown;
        }

        public boolean isFormatEnabled() { return formatEnabled; }
        public int getAnnounceMode() { return announceMode; }
        public boolean isEnabled() { return enabled; }
        public String getPermission() { return permission; }
        public String getFormat() { return format; }
        public int getMessageMode() { return messageMode; }
        public boolean isDiscordEnabled() { return discordEnabled; }
        public String getDiscordHook() { return discordHook; }
        public boolean isCooldownEnabled() {return cooldownEnabled;}
        public int getCooldown() {return cooldown;}
    }
}
