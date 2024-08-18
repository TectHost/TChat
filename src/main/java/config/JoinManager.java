package config;

import minealex.tchat.TChat;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;

public class JoinManager {

    private final TChat plugin;
    private final ConfigFile configFile;
    private final Map<String, GroupConfig> groupConfigs;

    public JoinManager(TChat plugin) {
        this.plugin = plugin;
        this.configFile = new ConfigFile("joins.yml", null, plugin);
        this.configFile.registerConfig();
        this.groupConfigs = new HashMap<>();
        loadConfig();
    }

    public void loadConfig() {
        FileConfiguration config = configFile.getConfig();
        ConfigurationSection groupsSection = config.getConfigurationSection("groups");

        if (groupsSection != null) {
            for (String groupName : groupsSection.getKeys(false)) {
                ConfigurationSection groupSection = groupsSection.getConfigurationSection(groupName);

                if (groupSection != null) {
                    GroupConfig groupConfig = new GroupConfig();

                    loadEventConfig(groupSection.getConfigurationSection("join.personal"), groupConfig.getPersonalConfig());
                    loadEventConfig(groupSection.getConfigurationSection("join.global"), groupConfig.getGlobalConfig());
                    loadEventConfig(groupSection.getConfigurationSection("quit"), groupConfig.getQuitConfig());

                    groupConfigs.put(groupName, groupConfig);
                }
            }
        }
    }

    private void loadEventConfig(ConfigurationSection eventSection, EventConfig eventConfig) {
        if (eventSection != null) {
            eventConfig.setCancelLeftMessage(eventSection.getBoolean("cancel-quit-message", false));
            eventConfig.setCancelJoinMessage(eventSection.getBoolean("cancel-join-message", false));
            eventConfig.setMessageEnabled(eventSection.getBoolean("message-enabled", false));
            eventConfig.setMessage(eventSection.getStringList("message"));

            ConfigurationSection titleSection = eventSection.getConfigurationSection("title");
            if (titleSection != null) {
                eventConfig.setTitleEnabled(titleSection.getBoolean("enabled", false));
                eventConfig.setTitle(titleSection.getString("title", ""));
                eventConfig.setSubtitle(titleSection.getString("subtitle", ""));
            }

            ConfigurationSection soundSection = eventSection.getConfigurationSection("sound");
            if (soundSection != null) {
                eventConfig.setSoundEnabled(soundSection.getBoolean("enabled", false));
                eventConfig.setSound(soundSection.getString("sound", ""));
            }

            ConfigurationSection particlesSection = eventSection.getConfigurationSection("particles");
            if (particlesSection != null) {
                eventConfig.setParticlesEnabled(particlesSection.getBoolean("enabled", false));
                eventConfig.setParticle(particlesSection.getString("particle", ""));
            }

            ConfigurationSection actionbarSection = eventSection.getConfigurationSection("actionbar");
            if (actionbarSection != null) {
                eventConfig.setActionbarEnabled(actionbarSection.getBoolean("enabled", false));
                eventConfig.setActionbarMessage(actionbarSection.getString("actionbar", ""));
            }
        }
    }

    public void reloadConfig() {
        configFile.reloadConfig();
        loadConfig();
    }

    public EventConfig getPersonalConfig(String groupName) {
        return groupConfigs.containsKey(groupName) ? groupConfigs.get(groupName).getPersonalConfig() : new EventConfig();
    }

    public EventConfig getGlobalConfig(String groupName) {
        return groupConfigs.containsKey(groupName) ? groupConfigs.get(groupName).getGlobalConfig() : new EventConfig();
    }

    public EventConfig getQuitConfig(String groupName) {
        return groupConfigs.containsKey(groupName) ? groupConfigs.get(groupName).getQuitConfig() : new EventConfig();
    }

    public Map<String, GroupConfig> getAllGroupConfigs() {
        return groupConfigs;
    }

    public static class GroupConfig {
        private final EventConfig personalConfig;
        private final EventConfig globalConfig;
        private final EventConfig quitConfig;

        public GroupConfig() {
            this.personalConfig = new EventConfig();
            this.globalConfig = new EventConfig();
            this.quitConfig = new EventConfig();
        }

        public EventConfig getPersonalConfig() {
            return personalConfig;
        }

        public EventConfig getGlobalConfig() {
            return globalConfig;
        }

        public EventConfig getQuitConfig() {
            return quitConfig;
        }
    }

    public static class EventConfig {
        private boolean messageEnabled;
        private java.util.List<String> message;
        private boolean titleEnabled;
        private String title;
        private String subtitle;
        private boolean soundEnabled;
        private String sound;
        private boolean particlesEnabled;
        private String particle;
        private boolean actionbarEnabled;
        private String actionbarMessage;
        private boolean cancelJoinMessage;
        private boolean cancelLeftMessage;

        public boolean isCancelJoinMessage() {
            return cancelJoinMessage;
        }

        public void setCancelJoinMessage(boolean cancelJoinMessage) {
            this.cancelJoinMessage = cancelJoinMessage;
        }

        public boolean isCancelLeftMessage() {
            return cancelLeftMessage;
        }

        public void setCancelLeftMessage(boolean cancelLeftMessage) {
            this.cancelLeftMessage = cancelLeftMessage;
        }

        public boolean isMessageEnabled() {
            return messageEnabled;
        }

        public void setMessageEnabled(boolean messageEnabled) {
            this.messageEnabled = messageEnabled;
        }

        public java.util.List<String> getMessage() {
            return message;
        }

        public void setMessage(java.util.List<String> message) {
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