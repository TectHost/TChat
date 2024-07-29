package config;

import minealex.tchat.TChat;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DeathManager {

    private final ConfigFile configFile;
    private final Map<String, String> deathMessages = new HashMap<>();

    private boolean titleEnabled;
    private String title;
    private String subtitle;
    private boolean actionBarEnabled;
    private String actionBarText;
    private boolean soundEnabled;
    private String sound;
    private boolean particlesEnabled;
    private String particle;
    private int numberOfParticles;

    public DeathManager(TChat plugin){
        this.configFile = new ConfigFile("death.yml", null, plugin);
        this.configFile.registerConfig();
        loadConfig();
    }

    public void loadConfig(){
        FileConfiguration config = configFile.getConfig();

        if (config.contains("death-messages")) {
            for (String key : Objects.requireNonNull(config.getConfigurationSection("death-messages")).getKeys(false)) {
                String message = config.getString("death-messages." + key);
                if (message != null) {
                    deathMessages.put(key, message);
                }
            }
        }

        if (config.contains("actions")) {
            ConfigurationSection actionsConfig = config.getConfigurationSection("actions");

            assert actionsConfig != null;
            titleEnabled = actionsConfig.getBoolean("title.enabled");
            title = actionsConfig.getString("title.title");
            subtitle = actionsConfig.getString("title.subtitle");

            actionBarEnabled = actionsConfig.getBoolean("actionBar.enabled");
            actionBarText = actionsConfig.getString("actionBar.bar");

            soundEnabled = actionsConfig.getBoolean("sound.enabled");
            sound = actionsConfig.getString("sound.sound");

            particlesEnabled = actionsConfig.getBoolean("particles.enabled");
            particle = actionsConfig.getString("particles.particle");
            numberOfParticles = actionsConfig.getInt("particles.particles");
        }
    }

    public void reloadConfig(){
        configFile.reloadConfig();
        loadConfig();
    }

    public String getDeathMessage(String key) {
        return deathMessages.getOrDefault(key, "Message not found (DeathManager)");
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

    public boolean isActionBarEnabled() {
        return actionBarEnabled;
    }

    public String getActionBarText() {
        return actionBarText;
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

    public String getParticle() {
        return particle;
    }

    public int getNumberOfParticles() {
        return numberOfParticles;
    }
}
