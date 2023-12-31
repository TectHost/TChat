package minealex.tchat.blocked;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import minealex.tchat.TChat;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

@SuppressWarnings("unused")
public class BannedCommands implements CommandExecutor {
    private Set<String> bannedCommands;
    private TChat plugin;
    private String blockedMessage;
    private boolean titleEnabled;
    private String title;
    private String subtitle;
    private boolean soundEnabled;
    private String sound;

    public BannedCommands(TChat plugin) {
        this.plugin = plugin;
        this.bannedCommands = new HashSet<>();
        loadBannedCommands();
        loadBlockedMessage();
        loadTitleOptions();
    }

    private void loadBannedCommands() {
        File configFile = new File(plugin.getDataFolder(), "banned_commands.json");

        if (!configFile.exists()) {
            plugin.saveResource("banned_commands.json", false);
        }

        try {
            JsonObject jsonObject = (JsonObject) new JsonParser().parse(new FileReader(configFile));
            JsonArray commandsArray = jsonObject.getAsJsonArray("bannedCommands");

            for (JsonElement element : commandsArray) {
                String command = element.getAsString();
                bannedCommands.add(command.toLowerCase());
            }

        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, "Error loading banned_commands.json, the default list will be used.", e);
        } catch (JsonSyntaxException e) {
            plugin.getLogger().log(Level.WARNING, "Error parsing banned_commands.json, the default list will be used.", e);
        }
    }
    
    public String getBlockedMessage() {
        return blockedMessage;
    }

    private void loadBlockedMessage() {
        File configFile = new File(plugin.getDataFolder(), "banned_commands.json");

        if (!configFile.exists()) {
            plugin.saveResource("banned_commands.json", false);
        }

        try {
            JsonObject jsonObject = (JsonObject) new JsonParser().parse(new FileReader(configFile));
            String blockedMessage = jsonObject.get("blockedMessage").getAsString();
            this.blockedMessage = ChatColor.translateAlternateColorCodes('&', blockedMessage);
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, "Error loading blockedMessage from banned_commands.json, using default message.", e);
            this.blockedMessage = "&cYou are not allowed to use that command.";
        } catch (JsonSyntaxException e) {
            plugin.getLogger().log(Level.WARNING, "Error parsing blockedMessage from banned_commands.json, using default message.", e);
            this.blockedMessage = "&cYou are not allowed to use that command.";
        }
    }

    public void sendBlockedMessage(CommandSender sender) {
        sender.sendMessage(blockedMessage);
    }
    
    public boolean isCommandBanned(String command) {
        return bannedCommands.contains(command.toLowerCase());
    }
    
    private void loadTitleOptions() {
        File configFile = new File(plugin.getDataFolder(), "banned_commands.json");

        if (!configFile.exists()) {
            plugin.saveResource("banned_commands.json", false);
        }

        try {
            JsonObject jsonObject = (JsonObject) new JsonParser().parse(new FileReader(configFile));
            this.titleEnabled = jsonObject.get("titleEnabled").getAsBoolean();
            this.title = ChatColor.translateAlternateColorCodes('&', jsonObject.get("title").getAsString());
            this.subtitle = ChatColor.translateAlternateColorCodes('&', jsonObject.get("subtitle").getAsString());
            this.soundEnabled = jsonObject.get("soundEnabled").getAsBoolean();
            this.sound = jsonObject.get("sound").getAsString();
        } catch (IOException | JsonSyntaxException e) {
            plugin.getLogger().log(Level.WARNING, "Error loading title options from banned_commands.json, using default values.", e);
            this.titleEnabled = true;
            this.title = "&cBanned Command";
            this.subtitle = "&7You are not allowed to use this command.";
            this.soundEnabled = true;
            this.sound = "entity.ender_dragon.growl";
        }
    }

    // Add these methods to your BannedCommands class
    public boolean isSoundEnabled() {
        return soundEnabled;
    }
    
    public String getSound() {
        return sound;
    }

    public void playSound(Player player) {
        if (soundEnabled) {
            player.playSound(player.getLocation(), sound, 1.0F, 1.0F);
        }
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

    @SuppressWarnings("deprecation")
    public void sendTitle(Player player) {
        if (titleEnabled) {
            player.sendTitle(title, subtitle);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("checkcommand")) {
            if (!sender.hasPermission("tchat.checkcommand")) {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
                return true;
            }

            if (args.length != 1) {
                sender.sendMessage(ChatColor.RED + "Usage: /checkcommand <command>");
                return true;
            }

            String commandToCheck = args[0].toLowerCase();
            if (isCommandBanned(commandToCheck)) {
                sender.sendMessage(ChatColor.RED + "The command '" + commandToCheck + "' is banned.");
                sendTitle((Player) sender);
                playSound((Player) sender);
            } else {
                sender.sendMessage(ChatColor.GREEN + "The command '" + commandToCheck + "' is allowed.");
            }
            return true;
        }
        return false;
    }
    
    public boolean canBypassCommandBlocker(Player player) {
        return player.hasPermission("tchat.bypass.commandblocker");
    }
}
