package config;

import minealex.tchat.TChat;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class BroadcastPremadeManager {

    private final ConfigFile configFile;
    private final Map<String, Broadcast> broadcasts = new HashMap<>();

    public BroadcastPremadeManager(TChat plugin) {
        this.configFile = new ConfigFile("broadcast.yml", "premades", plugin);
        this.configFile.registerConfig();
        loadConfig();
    }

    public void loadConfig() {
        FileConfiguration config = configFile.getConfig();
        broadcasts.clear();

        if (config.isConfigurationSection("broadcasts")) {
            for (String key : Objects.requireNonNull(config.getConfigurationSection("broadcasts")).getKeys(false)) {
                String path = "broadcasts." + key;
                boolean messageEnabled = config.getBoolean(path + ".message.enabled", false);
                List<String> message = config.getStringList(path + ".message.message");

                boolean titleEnabled = config.getBoolean(path + ".message.title.enabled", false);
                String title = config.getString(path + ".message.title.main");
                String subtitle = config.getString(path + ".message.title.subtitle");
                int fadeIn = config.getInt(path + ".message.title.fade-in", 10);
                int stay = config.getInt(path + ".message.title.stay", 70);
                int fadeOut = config.getInt(path + ".message.title.fade-out", 20);

                boolean soundEnabled = config.getBoolean(path + ".message.sound.enabled", false);
                String sound = config.getString(path + ".message.sound.sound");
                float volume = (float) config.getDouble(path + ".message.sound.volume", 1.0);
                float pitch = (float) config.getDouble(path + ".message.sound.pitch", 1.0);

                boolean actionBarEnabled = config.getBoolean(path + ".message.action-bar.enabled", false);
                String actionBar = config.getString(path + ".message.action-bar.message");

                boolean particlesEnabled = config.getBoolean(path + ".message.particles.enabled", false);
                String particleType = config.getString(path + ".message.particles.type");
                int amount = config.getInt(path + ".message.particles.amount", 10);
                double offsetX = config.getDouble(path + ".message.particles.offset-x", 0.5);
                double offsetY = config.getDouble(path + ".message.particles.offset-y", 0.5);
                double offsetZ = config.getDouble(path + ".message.particles.offset-z", 0.5);

                Broadcast broadcast = new Broadcast(messageEnabled, message, titleEnabled, title, subtitle, fadeIn, stay, fadeOut,
                        soundEnabled, sound, volume, pitch, actionBarEnabled, actionBar, particlesEnabled, particleType, amount, offsetX, offsetY, offsetZ);
                broadcasts.put(key, broadcast);
            }
        }
    }

    public void reloadConfig() {
        configFile.reloadConfig();
        loadConfig();
    }

    public Map<String, Broadcast> getBroadcasts() {
        return broadcasts;
    }

    public static class Broadcast {
        private final boolean messageEnabled;
        private final List<String> message;
        private final boolean titleEnabled;
        private final String title;
        private final String subtitle;
        private final int fadeIn;
        private final int stay;
        private final int fadeOut;
        private final boolean soundEnabled;
        private final String sound;
        private final float volume;
        private final float pitch;
        private final boolean actionBarEnabled;
        private final String actionBar;
        private final boolean particlesEnabled;
        private final String particleType;
        private final int particleAmount;
        private final double offsetX;
        private final double offsetY;
        private final double offsetZ;

        public Broadcast(boolean messageEnabled, List<String> message, boolean titleEnabled, String title, String subtitle,
                         int fadeIn, int stay, int fadeOut, boolean soundEnabled, String sound, float volume, float pitch,
                         boolean actionBarEnabled, String actionBar, boolean particlesEnabled, String particleType,
                         int particleAmount, double offsetX, double offsetY, double offsetZ) {
            this.messageEnabled = messageEnabled;
            this.message = message;
            this.titleEnabled = titleEnabled;
            this.title = title;
            this.subtitle = subtitle;
            this.fadeIn = fadeIn;
            this.stay = stay;
            this.fadeOut = fadeOut;
            this.soundEnabled = soundEnabled;
            this.sound = sound;
            this.volume = volume;
            this.pitch = pitch;
            this.actionBarEnabled = actionBarEnabled;
            this.actionBar = actionBar;
            this.particlesEnabled = particlesEnabled;
            this.particleType = particleType;
            this.particleAmount = particleAmount;
            this.offsetX = offsetX;
            this.offsetY = offsetY;
            this.offsetZ = offsetZ;
        }

        public boolean isMessageEnabled() { return messageEnabled; }
        public List<String> getMessage() { return message; }
        public boolean isTitleEnabled() { return titleEnabled; }
        public String getTitle() { return title; }
        public String getSubtitle() { return subtitle; }
        public int getFadeIn() { return fadeIn; }
        public int getStay() { return stay; }
        public int getFadeOut() { return fadeOut; }
        public boolean isSoundEnabled() { return soundEnabled; }
        public String getSound() { return sound; }
        public float getVolume() { return volume; }
        public float getPitch() { return pitch; }
        public boolean isActionBarEnabled() { return actionBarEnabled; }
        public String getActionBar() { return actionBar; }
        public boolean isParticlesEnabled() { return particlesEnabled; }
        public String getParticleType() { return particleType; }
        public int getParticleAmount() { return particleAmount; }
        public double getOffsetX() { return offsetX; }
        public double getOffsetY() { return offsetY; }
        public double getOffsetZ() { return offsetZ; }
    }
}
