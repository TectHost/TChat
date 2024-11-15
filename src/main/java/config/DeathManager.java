package config;

import minealex.tchat.TChat;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DeathManager {

    private final ConfigFile configFile;
    private final Map<String, DeathMessage> deathMessages = new HashMap<>();

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
        this.configFile = new ConfigFile("death.yml", "modules", plugin);
        this.configFile.registerConfig();
        loadConfig();
    }

    public void loadConfig(){
        FileConfiguration config = configFile.getConfig();

        if (config.contains("death-messages")) {
            ConfigurationSection messagesSection = config.getConfigurationSection("death-messages");
            for (String key : Objects.requireNonNull(messagesSection).getKeys(false)) {
                boolean enabled = messagesSection.getBoolean(key + ".enabled", false);
                String message = messagesSection.getString(key + ".message");
                if (message != null && enabled) {
                    deathMessages.put(key, new DeathMessage(enabled, message));
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
        DeathMessage deathMessage = deathMessages.get(key);
        return (deathMessage != null && deathMessage.enabled())
                ? deathMessage.message()
                : null;
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

    public record DeathMessage(boolean enabled, String message) {
    }
}
