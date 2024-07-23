package config;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class AdvertisingConfig {
    private final boolean enabled;
    private String match;
    private boolean messageEnabled;
    private List<String> message;
    private boolean titleEnabled;
    private String title;
    private String subtitle;
    private boolean actionBarEnabled;
    private String actionBar;
    private boolean soundEnabled;
    private String sound;
    private boolean particlesEnabled;
    private String particleType;
    private int particles;
    private float soundPitch;
    private float soundVolume;
    private int titleFadeIn;
    private int titleStay;
    private int titleFadeOut;

    public AdvertisingConfig(FileConfiguration config, String basePath) {
        this.enabled = config.getBoolean(basePath + ".enabled");
        if (enabled) {
            this.match = config.getString(basePath + ".match");
            this.messageEnabled = config.getBoolean(basePath + ".actions.message.enabled");
            if (messageEnabled) {
                this.message = config.getStringList(basePath + ".actions.message.message");
            }
            this.titleEnabled = config.getBoolean(basePath + ".actions.title.enabled");
            if (titleEnabled) {
                this.title = config.getString(basePath + ".actions.title.title");
                this.subtitle = config.getString(basePath + ".actions.title.subtitle");
                this.titleFadeIn = config.getInt(basePath + ".actions.title.fade-in");
                this.titleStay = config.getInt(basePath + ".actions.title.stay");
                this.titleFadeOut = config.getInt(basePath + ".actions.title.fade-out");
            }
            this.actionBarEnabled = config.getBoolean(basePath + ".actions.actionbar.enabled");
            if (actionBarEnabled) {
                this.actionBar = config.getString(basePath + ".actions.actionbar.bar");
            }
            this.soundEnabled = config.getBoolean(basePath + ".actions.sound.enabled");
            if (soundEnabled) {
                this.sound = config.getString(basePath + ".actions.sound.sound");
                this.soundPitch = (float) config.getDouble(basePath + ".actions.sound.pitch");
                this.soundVolume = (float) config.getDouble(basePath + ".actions.sound.volume");
            }
            this.particlesEnabled = config.getBoolean(basePath + ".actions.particles.enabled");
            if (particlesEnabled) {
                this.particleType = config.getString(basePath + ".actions.particles.type");
                this.particles = config.getInt(basePath + ".actions.particles.particles");
            }
        }
    }

    public boolean isEnabled() { return enabled; }
    public String getMatch() { return match; }
    public boolean isMessageEnabled() { return messageEnabled; }
    public List<String> getMessage() { return message; }
    public boolean isTitleEnabled() { return titleEnabled; }
    public String getTitle() { return title; }
    public String getSubtitle() { return subtitle; }
    public boolean isActionBarEnabled() { return actionBarEnabled; }
    public String getActionBar() { return actionBar; }
    public boolean isSoundEnabled() { return soundEnabled; }
    public String getSound() { return sound; }
    public boolean isParticlesEnabled() { return particlesEnabled; }
    public String getParticleType() { return particleType; }
    public int getParticles() { return particles; }
    public float getSoundPitch() { return soundPitch; }
    public float getSoundVolume() { return soundVolume; }
    public int getTitleFadeIn() { return titleFadeIn; }
    public int getTitleStay() { return titleStay; }
    public int getTitleFadeOut() { return titleFadeOut; }
}
