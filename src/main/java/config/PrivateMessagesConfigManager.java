package config;

import minealex.tchat.TChat;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PrivateMessagesConfigManager {

    private final TChat plugin;
    private final ConfigFile chatBotFile;

    private String msgFormatSender;
    private String msgFormatReceiver;

    private String replyFormatSender;
    private String replyFormatReceiver;

    private Map<String, HoverConfig> pmHoverConfigs;
    private Map<String, HoverConfig> replyHoverConfigs;

    public PrivateMessagesConfigManager(TChat plugin){
        this.plugin = plugin;
        this.chatBotFile = new ConfigFile("msg.yml", "modules", plugin);
        this.chatBotFile.registerConfig();
        loadConfig();
    }

    public void loadConfig(){
        FileConfiguration config = chatBotFile.getConfig();

        msgFormatSender = config.getString("pm.msg.formats.sender");
        msgFormatReceiver = config.getString("pm.msg.formats.receiver");

        if (plugin.getConfigManager().isReplyEnabled()) {
            replyFormatSender = config.getString("pm.reply.formats.sender");
            replyFormatReceiver = config.getString("pm.reply.formats.receiver");
        }

        pmHoverConfigs = new HashMap<>();

        ConfigurationSection hoverSection = config.getConfigurationSection("pm.msg.hover");
        if (hoverSection != null) {
            for (String group : hoverSection.getKeys(false)) {
                List<String> senderTexts = hoverSection.getStringList(group + ".sender");
                String senderAction = hoverSection.getString(group + ".sender-action");
                List<String> receiverTexts = hoverSection.getStringList(group + ".receiver");
                String receiverAction = hoverSection.getString(group + ".receiver-action");

                HoverConfig hoverConfig = new HoverConfig(senderTexts, senderAction, receiverTexts, receiverAction);
                pmHoverConfigs.put(group, hoverConfig);
            }
        } else {
            plugin.getLogger().warning("'pm.msg.hover' not found in config.yml");
        }

        replyHoverConfigs = new HashMap<>();

        ConfigurationSection replyHoverSection = config.getConfigurationSection("pm.reply.hover");
        if (replyHoverSection != null) {
            for (String group : replyHoverSection.getKeys(false)) {
                List<String> senderTexts = replyHoverSection.getStringList(group + ".sender");
                String senderAction = replyHoverSection.getString(group + ".sender-action");
                List<String> receiverTexts = replyHoverSection.getStringList(group + ".receiver");
                String receiverAction = replyHoverSection.getString(group + ".receiver-action");

                HoverConfig hoverConfig = new HoverConfig(senderTexts, senderAction, receiverTexts, receiverAction);
                replyHoverConfigs.put(group, hoverConfig);
            }
        } else {
            plugin.getLogger().warning("'pm.reply.hover' not found in config.yml");
        }
    }

    public void reloadConfig(){
        chatBotFile.reloadConfig();
        loadConfig();
    }

    public HoverConfig getPmHoverConfig(String group) {
        return pmHoverConfigs.get(group);
    }

    public HoverConfig getReplyHoverConfig(String group) {
        return replyHoverConfigs.get(group);
    }

    public String getReplyFormatReceiver() { return replyFormatReceiver; }
    public String getReplyFormatSender() { return replyFormatSender; }
    public String getMsgFormatReceiver() { return msgFormatReceiver; }
    public String getMsgFormatSender() { return msgFormatSender; }

    public static class HoverConfig {
        private final List<String> senderTexts;
        private final String senderAction;
        private final List<String> receiverTexts;
        private final String receiverAction;

        public HoverConfig(List<String> senderTexts, String senderAction, List<String> receiverTexts, String receiverAction) {
            this.senderTexts = senderTexts;
            this.senderAction = senderAction;
            this.receiverTexts = receiverTexts;
            this.receiverAction = receiverAction;
        }

        public List<String> getSenderTexts() {
            return senderTexts;
        }

        public String getSenderAction() {
            return senderAction;
        }

        public List<String> getReceiverTexts() {
            return receiverTexts;
        }

        public String getReceiverAction() {
            return receiverAction;
        }
    }
}
