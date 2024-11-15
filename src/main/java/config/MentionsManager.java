package config;

import minealex.tchat.TChat;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MentionsManager {

    private final ConfigFile configFile;
    private Map<String, GroupConfig> groupsConfig;
    private Map<String, EventConfig> personalEvents;
    private Map<String, EventConfig> globalEvents;

    public MentionsManager(TChat plugin) {
        this.configFile = new ConfigFile("mentions.yml", "modules", plugin);
        this.configFile.registerConfig();
        loadConfig();
    }

    public void loadConfig() {
        FileConfiguration config = configFile.getConfig();

        ConfigurationSection groupsSection = config.getConfigurationSection("groups");

        if (groupsSection != null) {
            groupsConfig = new HashMap<>();
            personalEvents = new HashMap<>();
            globalEvents = new HashMap<>();

            for (String groupName : groupsSection.getKeys(false)) {
                ConfigurationSection groupSection = groupsSection.getConfigurationSection(groupName);

                if (groupSection != null) {
                    GroupConfig groupConfig = new GroupConfig();
                    groupConfig.setCharacter(groupSection.getString("character", "@"));
                    groupConfig.setColor(groupSection.getString("color", "&f"));

                    ConfigurationSection personalSection = groupSection.getConfigurationSection("personal");
                    if (personalSection != null) {
                        EventConfig personalConfig = new EventConfig();
                        loadEventConfig(personalSection, personalConfig);
                        personalEvents.put(groupName, personalConfig);
                        groupConfig.setPersonalConfig(personalConfig);
                    }

                    ConfigurationSection globalSection = groupSection.getConfigurationSection("global");
                    if (globalSection != null) {
                        EventConfig globalConfig = new EventConfig();
                        loadEventConfig(globalSection, globalConfig);
                        globalEvents.put(groupName, globalConfig);
                        groupConfig.setGlobalConfig(globalConfig);
                    }

                    groupsConfig.put(groupName, groupConfig);
                }
            }
        }
    }

    private void loadEventConfig(ConfigurationSection eventSection, EventConfig eventConfig) {
        if (eventSection != null) {
            eventConfig.setMessageEnabled(eventSection.getBoolean("message.enabled", false));
            eventConfig.setMessage(eventSection.getStringList("message.text"));

            ConfigurationSection titleSection = eventSection.getConfigurationSection("title");
            if (titleSection != null) {
                eventConfig.setTitleEnabled(titleSection.getBoolean("enabled", false));
                eventConfig.setTitle(titleSection.getString("main", ""));
                eventConfig.setSubtitleEnabled(titleSection.getBoolean("subtitle.enabled", false));
                eventConfig.setSubtitle(titleSection.getString("subtitle.text", ""));
            }

            ConfigurationSection soundSection = eventSection.getConfigurationSection("sound");
            if (soundSection != null) {
                eventConfig.setSoundEnabled(soundSection.getBoolean("enabled", false));
                eventConfig.setSound(soundSection.getString("type", ""));
            }

            ConfigurationSection particlesSection = eventSection.getConfigurationSection("particles");
            if (particlesSection != null) {
                eventConfig.setParticlesEnabled(particlesSection.getBoolean("enabled", false));
                eventConfig.setParticle(particlesSection.getString("type", ""));
            }

            ConfigurationSection actionbarSection = eventSection.getConfigurationSection("actionbar");
            if (actionbarSection != null) {
                eventConfig.setActionbarEnabled(actionbarSection.getBoolean("enabled", false));
                eventConfig.setActionbarMessage(actionbarSection.getString("text", ""));
            }
        }
    }

    public void reloadConfig() {
        configFile.reloadConfig();
        loadConfig();
    }

    public GroupConfig getGroupConfig(String group) {
        return groupsConfig.getOrDefault(group, new GroupConfig());
    }

    public EventConfig getPersonalEventConfig(String group) {
        return personalEvents.getOrDefault(group, new EventConfig());
    }

    public EventConfig getGlobalEventConfig(String group) {
        return globalEvents.getOrDefault(group, new EventConfig());
    }

    public static class GroupConfig {
        private String character;
        private String color;
        private EventConfig personalConfig;
        private EventConfig globalConfig;

        public String getCharacter() {
            return character;
        }

        public void setCharacter(String character) {
            this.character = character;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public EventConfig getPersonalConfig() {
            return personalConfig;
        }

        public void setPersonalConfig(EventConfig personalConfig) {
            this.personalConfig = personalConfig;
        }

        public EventConfig getGlobalConfig() {
            return globalConfig;
        }

        public void setGlobalConfig(EventConfig globalConfig) {
            this.globalConfig = globalConfig;
        }
    }

    public static class EventConfig {
        private boolean messageEnabled;
        private List<String> message;
        private boolean titleEnabled;
        private String title;
        private boolean subtitleEnabled;
        private String subtitle;
        private boolean soundEnabled;
        private String sound;
        private boolean particlesEnabled;
        private String particle;
        private boolean actionbarEnabled;
        private String actionbarMessage;

        public boolean isMessageEnabled() {
            return messageEnabled;
        }

        public void setMessageEnabled(boolean messageEnabled) {
            this.messageEnabled = messageEnabled;
        }

        public List<String> getMessage() {
            return message;
        }

        public void setMessage(List<String> message) {
            this.message = message;
        }

        public boolean isTitleEnabled() {
            return titleEnabled;
        }

        public void setTitleEnabled(boolean titleEnabled) {
            this.titleEnabled = titleEnabled;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public boolean isSubtitleEnabled() {
            return subtitleEnabled;
        }

        public void setSubtitleEnabled(boolean subtitleEnabled) {
            this.subtitleEnabled = subtitleEnabled;
        }

        public String getSubtitle() {
            return subtitle;
        }

        public void setSubtitle(String subtitle) {
            this.subtitle = subtitle;
        }

        public boolean isSoundEnabled() {
            return soundEnabled;
        }

        public void setSoundEnabled(boolean soundEnabled) {
            this.soundEnabled = soundEnabled;
        }

        public String getSound() {
            return sound;
        }

        public void setSound(String sound) {
            this.sound = sound;
        }

        public boolean isParticlesEnabled() {
            return particlesEnabled;
        }

        public void setParticlesEnabled(boolean particlesEnabled) {
            this.particlesEnabled = particlesEnabled;
        }

        public String getParticle() {
            return particle;
        }

        public void setParticle(String particle) {
            this.particle = particle;
        }

        public boolean isActionbarEnabled() {
            return actionbarEnabled;
        }

        public void setActionbarEnabled(boolean actionbarEnabled) {
            this.actionbarEnabled = actionbarEnabled;
        }

        public String getActionbarMessage() {
            return actionbarMessage;
        }

        public void setActionbarMessage(String actionbarMessage) {
            this.actionbarMessage = actionbarMessage;
        }
    }
}
