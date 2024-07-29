package config;

import minealex.tchat.TChat;
import org.bukkit.Particle;
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
            List<String> messages = config.getStringList("broadcasts." + key + ".message");

            // Actions
            boolean titleEnabled = config.getBoolean("broadcasts." + key + ".actions.title.enabled");
            String title = config.getString("broadcasts." + key + ".actions.title.title");
            String subtitle = config.getString("broadcasts." + key + ".actions.title.subtitle");
            boolean soundEnabled = config.getBoolean("broadcasts." + key + ".actions.sound.enabled");
            String sound = config.getString("broadcasts." + key + ".actions.sound.sound");
            boolean particlesEnabled = config.getBoolean("broadcasts." + key + ".actions.particles.enabled");
            String particle = config.getString("broadcasts." + key + ".actions.particles.particle");
            int particleCount = config.getInt("broadcasts." + key + ".actions.particles.particles");
            boolean actionbarEnabled = config.getBoolean("broadcasts." + key + ".actions.actionbar.enabled");
            String actionbar = config.getString("broadcasts." + key + ".actions.actionbar.bar");

            Broadcast broadcast = new Broadcast(
                    broadcastEnabled, messages, titleEnabled, title, subtitle,
                    soundEnabled, sound, particlesEnabled, particle, particleCount,
                    actionbarEnabled, actionbar
            );
            broadcasts.put(key, broadcast);
        }
    }

    public void reloadConfig(){
        autoBroadcastFile.reloadConfig();
        loadConfig();
    }

    public void removeBroadcast(String key) {
        if (broadcasts.containsKey(key)) {
            broadcasts.remove(key);

            FileConfiguration config = autoBroadcastFile.getConfig();
            config.set("broadcasts." + key, null);

            autoBroadcastFile.saveConfig();
            loadConfig();
        }
    }

    public void addBroadcast(String key, boolean enabled, List<String> messages, boolean titleEnabled, String title, String subtitle,
                             boolean soundEnabled, String sound, boolean particlesEnabled, String particle, int particleCount,
                             boolean actionbarEnabled, String actionbar) {
        Broadcast broadcast = new Broadcast(
                enabled, messages, titleEnabled, title, subtitle,
                soundEnabled, sound, particlesEnabled, particle, particleCount,
                actionbarEnabled, actionbar
        );
        broadcasts.put(key, broadcast);

        FileConfiguration config = autoBroadcastFile.getConfig();
        config.set("broadcasts." + key + ".enabled", enabled);
        config.set("broadcasts." + key + ".message", messages);

        config.set("broadcasts." + key + ".actions.title.enabled", titleEnabled);
        config.set("broadcasts." + key + ".actions.title.title", title);
        config.set("broadcasts." + key + ".actions.title.subtitle", subtitle);
        config.set("broadcasts." + key + ".actions.sound.enabled", soundEnabled);
        config.set("broadcasts." + key + ".actions.sound.sound", sound);
        config.set("broadcasts." + key + ".actions.particles.enabled", particlesEnabled);
        config.set("broadcasts." + key + ".actions.particles.particle", particle);
        config.set("broadcasts." + key + ".actions.particles.particles", particleCount);
        config.set("broadcasts." + key + ".actions.actionbar.enabled", actionbarEnabled);
        config.set("broadcasts." + key + ".actions.actionbar.bar", actionbar);

        autoBroadcastFile.saveConfig();
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
        private final boolean titleEnabled;
        private final String title;
        private final String subtitle;
        private final boolean soundEnabled;
        private final String sound;
        private final boolean particlesEnabled;
        private final String particle;
        private final int particleCount;
        private final boolean actionbarEnabled;
        private final String actionbar;

        public Broadcast(boolean enabled, List<String> message,
                         boolean titleEnabled, String title, String subtitle,
                         boolean soundEnabled, String sound,
                         boolean particlesEnabled, String particle, int particleCount,
                         boolean actionbarEnabled, String actionbar) {
            this.enabled = enabled;
            this.message = message;
            this.titleEnabled = titleEnabled;
            this.title = title;
            this.subtitle = subtitle;
            this.soundEnabled = soundEnabled;
            this.sound = sound;
            this.particlesEnabled = particlesEnabled;
            this.particle = particle;
            this.particleCount = particleCount;
            this.actionbarEnabled = actionbarEnabled;
            this.actionbar = actionbar;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public List<String> getMessage() {
            return message;
        }

        public boolean isTitleEnabled() {
            return titleEnabled;
        }

        public String getTitle() {
            return title;
        }

        public String getSubtitle() {
            return subtitle;
        }

        public boolean isSoundEnabled() {
            return soundEnabled;
        }

        public String getSound() {
            return sound;
        }

        public boolean isParticlesEnabled() {
            return particlesEnabled;
        }

        public String getParticle() { return particle; }

        public int getParticleCount() {
            return particleCount;
        }

        public boolean isActionbarEnabled() {
            return actionbarEnabled;
        }

        public String getActionbar() {
            return actionbar;
        }
    }
}
