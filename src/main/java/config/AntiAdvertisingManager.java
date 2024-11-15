package config;

import minealex.tchat.TChat;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AntiAdvertisingManager {

    private final ConfigFile configFile;

    private boolean enabled;
    private List<String> whitelist;

    private boolean ipv4Enabled;
    private String ipv4Match;
    private ActionConfig ipv4Actions;

    private boolean domainEnabled;
    private String domainMatch;
    private ActionConfig domainActions;

    private boolean linksEnabled;
    private String linksMatch;
    private ActionConfig linksActions;

    public AntiAdvertisingManager(TChat plugin) {
        this.configFile = new ConfigFile("anti_advertising.yml", "modules", plugin);
        this.configFile.registerConfig();
        loadConfig();
    }

    public void loadConfig() {
        FileConfiguration config = configFile.getConfig();

        enabled = config.getBoolean("advertising.enabled", true);
        whitelist = config.getStringList("advertising.whitelist");

        ipv4Enabled = config.getBoolean("advertising.ipv4.enabled", false);
        ipv4Match = config.getString("advertising.ipv4.match");
        ipv4Actions = loadActionConfig(config, "advertising.ipv4.actions");

        domainEnabled = config.getBoolean("advertising.domain.enabled", false);
        domainMatch = config.getString("advertising.domain.match");
        domainActions = loadActionConfig(config, "advertising.domain.actions");

        linksEnabled = config.getBoolean("advertising.links.enabled", false);
        linksMatch = config.getString("advertising.links.match");
        linksActions = loadActionConfig(config, "advertising.links.actions");
    }

    private @NotNull ActionConfig loadActionConfig(@NotNull FileConfiguration config, String basePath) {
        ActionConfig actionConfig = new ActionConfig();

        actionConfig.messageEnabled = config.getBoolean(basePath + ".message.enabled", false);
        if (actionConfig.messageEnabled) {
            actionConfig.message = config.getStringList(basePath + ".message.message");
        }

        actionConfig.titleEnabled = config.getBoolean(basePath + ".title.enabled", false);
        if (actionConfig.titleEnabled) {
            actionConfig.title = config.getString(basePath + ".title.title", "");
            actionConfig.subtitle = config.getString(basePath + ".title.subtitle", "");
            actionConfig.titleFadeIn = config.getInt(basePath + ".title.fade-in", 10);
            actionConfig.titleStay = config.getInt(basePath + ".title.stay", 70);
            actionConfig.titleFadeOut = config.getInt(basePath + ".title.fade-out", 20);
        }

        actionConfig.actionBarEnabled = config.getBoolean(basePath + ".actionbar.enabled", false);
        if (actionConfig.actionBarEnabled) {
            actionConfig.actionBar = config.getString(basePath + ".actionbar.bar", "");
        }

        actionConfig.soundEnabled = config.getBoolean(basePath + ".sound.enabled", false);
        if (actionConfig.soundEnabled) {
            actionConfig.sound = config.getString(basePath + ".sound.sound", "ENTITY_VILLAGER_NO");
            actionConfig.soundVolume = (float) config.getDouble(basePath + ".sound.volume", 1.0);
            actionConfig.soundPitch = (float) config.getDouble(basePath + ".sound.pitch", 1.0);
        }

        actionConfig.particlesEnabled = config.getBoolean(basePath + ".particles.enabled", false);
        if (actionConfig.particlesEnabled) {
            actionConfig.particleType = config.getString(basePath + ".particles.type", "VILLAGER_ANGRY");
            actionConfig.particles = config.getInt(basePath + ".particles.particles", 30);
        }

        return actionConfig;
    }

    public void reloadConfig() {
        configFile.reloadConfig();
        loadConfig();
    }

    public boolean isEnabled() { return enabled; }
    public List<String> getWhitelist() { return whitelist; }

    public boolean isIpv4Enabled() { return ipv4Enabled; }
    public String getIpv4Match() { return ipv4Match; }
    public ActionConfig getIpv4Actions() { return ipv4Actions; }

    public boolean isDomainEnabled() { return domainEnabled; }
    public String getDomainMatch() { return domainMatch; }
    public ActionConfig getDomainActions() { return domainActions; }

    public boolean isLinksEnabled() { return linksEnabled; }
    public String getLinksMatch() { return linksMatch; }
    public ActionConfig getLinksActions() { return linksActions; }

    public static class ActionConfig {
        public boolean messageEnabled;
        public List<String> message;

        public boolean titleEnabled;
        public String title;
        public String subtitle;
        public int titleFadeIn;
        public int titleStay;
        public int titleFadeOut;

        public boolean actionBarEnabled;
        public String actionBar;

        public boolean soundEnabled;
        public String sound;
        public float soundVolume;
        public float soundPitch;

        public boolean particlesEnabled;
        public String particleType;
        public int particles;
    }
}
