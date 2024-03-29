package minealex.tchat.placeholders;

import java.io.File;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import minealex.tchat.TChat;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;

public class Placeholders extends PlaceholderExpansion {

    private TChat plugin;

    public Placeholders(TChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String getAuthor() {
        return "Mine_Alex";
    }

    @Override
    public String getIdentifier() {
        return "tchat";
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        if (player == null) {
            return "";
        }

        if (identifier.equals("suffix")) {
            minealex.tchat.TChat.ChatGroup chatGroup = null;

            // Verificar si el jugador tiene un grupo asignado
            for (Entry<String, minealex.tchat.TChat.ChatGroup> entry : plugin.getGroups().entrySet()) {
                String groupName = entry.getKey();

                if (player.hasPermission("tchat.group." + groupName)) {
                    chatGroup = entry.getValue();
                    break;
                }
            }

            // Si no se encontró un grupo, usar el sufijo predeterminado
            if (chatGroup == null) {
                chatGroup = plugin.getDefaultChatGroup();
            }

            if (chatGroup == null) {
                return "";
            } else {
                return chatGroup.getSuffix();
            }
        } else if (identifier.equals("prefix")) {
            minealex.tchat.TChat.ChatGroup chatGroup = null;

            // Verificar si el jugador tiene un grupo asignado
            for (Entry<String, minealex.tchat.TChat.ChatGroup> entry : plugin.getGroups().entrySet()) {
                String groupName = entry.getKey();

                if (player.hasPermission("tchat.group." + groupName)) {
                    chatGroup = entry.getValue();
                    break;
                }
            }

            // Si no se encontró un grupo, usar el prefijo predeterminado
            if (chatGroup == null) {
                chatGroup = plugin.getDefaultChatGroup();
            }

            if (chatGroup == null) {
                return "";
            } else {
                return chatGroup.getPrefix();
            }
        } else if (identifier.equals("chatcolor")) {
            return obtenerColorAsignado(player);
        } else if (identifier.equals("nickname")) {
            return obtenerNick(player);
        } else if (identifier.equals("ignored")) {
        return obtenerJugadoresIgnorados(player);
        } else if (identifier.equals("luckperms_prefix")) {
            return obtenerLuckPermsPrefix(player);
        } else if (identifier.equals("luckperms_suffix")) {
            return obtenerLuckPermsSuffix(player);
        } else if (identifier.equals("group")) {
            return obtenerNombreGrupo(player);
        } else if (identifier.equals("chatgames_wins")) {
            return String.valueOf(obtenerChatGamesWins(player));
        } else if (identifier.startsWith("chatgames_winstop")) {
            int topNumber;
            try {
                topNumber = Integer.parseInt(identifier.replace("chatgames_winstop", ""));
            } catch (NumberFormatException e) {
                return "Invalid top number";
            }

            Map<String, Integer> playersAndWins = obtenerJugadoresYVictorias();

            if (playersAndWins.isEmpty()) {
                return "No players in the top";
            }

            List<Entry<String, Integer>> sortedPlayers = playersAndWins.entrySet().stream()
                    .sorted(Entry.comparingByValue(Comparator.reverseOrder()))
                    .collect(Collectors.toList());

            if (topNumber > 0 && topNumber <= sortedPlayers.size()) {
                Entry<String, Integer> topPlayer = sortedPlayers.get(topNumber - 1);

                return topPlayer.getKey();
            } else {
                return "No players found";
            }
        } else if (identifier.equals("chatgames_top")) {
            return obtenerChatGamesTop(player);
        }
        else if (identifier.equals("chat_level")) {
            return obtenerNivelChat(player);
        } else if (identifier.equals("chat_xp")) {
            return obtenerXPChat(player);
        }

        return null;
    }

    private String obtenerColorAsignado(Player player) {
        player.getName();
        
        File savesFile = new File(plugin.getDataFolder(), "saves.yml");
        YamlConfiguration savesConfig = YamlConfiguration.loadConfiguration(savesFile);

        String uuid = player.getUniqueId().toString();
        
        String colorName = savesConfig.getString("players." + uuid + ".chatcolor");
        String format = savesConfig.getString("players." + uuid + ".format");

        if (colorName != null && format != null) {
            return ChatColor.valueOf(colorName) + ChatColor.translateAlternateColorCodes('&', format);
        }

        return ChatColor.RESET.toString();
    }
    
    private Map<String, Integer> obtenerJugadoresYVictorias() {
        Map<String, Integer> playersAndWins = new HashMap<>();

        File savesFile = new File(plugin.getDataFolder(), "saves.yml");
        YamlConfiguration savesConfig = YamlConfiguration.loadConfiguration(savesFile);
        ConfigurationSection playersConfigSection = savesConfig.getConfigurationSection("players");

        if (playersConfigSection != null) {
            for (String uuid : playersConfigSection.getKeys(false)) {
                int wins = savesConfig.getInt("players." + uuid + ".chatgames_wins", 0);
                String playerName = Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName();
                playersAndWins.put(playerName, wins);
            }
        }

        return playersAndWins;
    }
    
    private String obtenerNivelChat(Player player) {
        File savesFile = new File(plugin.getDataFolder(), "saves.yml");
        YamlConfiguration savesConfig = YamlConfiguration.loadConfiguration(savesFile);

        String uuid = player.getUniqueId().toString();
        int nivel = savesConfig.getInt("players." + uuid + ".level", 0);
        return String.valueOf(nivel);
    }

    private String obtenerXPChat(Player player) {
        File savesFile = new File(plugin.getDataFolder(), "saves.yml");
        YamlConfiguration savesConfig = YamlConfiguration.loadConfiguration(savesFile);

        String uuid = player.getUniqueId().toString();
        int xp = savesConfig.getInt("players." + uuid + ".experience", 0);
        return String.valueOf(xp);
    }
    
    private String obtenerChatGamesTop(Player player) {
        player.getUniqueId().toString();
        int playerWins = obtenerChatGamesWins(player);
        long topPlayersCount = obtenerJugadoresYVictorias().entrySet().stream()
                .filter(entry -> entry.getValue() > playerWins)
                .count();

        return String.valueOf(topPlayersCount + 1);
    }
    
    private String obtenerLuckPermsPrefix(Player player) {
        User user = LuckPermsProvider.get().getUserManager().getUser(player.getUniqueId());

        if (user != null) {
            String prefix = user.getCachedData().getMetaData().getPrefix();
            return prefix != null ? prefix : "";
        }

        return "";
    }
    
    private int obtenerChatGamesWins(Player player) {
        String uuid = player.getUniqueId().toString();

        File savesFile = new File(plugin.getDataFolder(), "saves.yml");
        YamlConfiguration savesConfig = YamlConfiguration.loadConfiguration(savesFile);
        int wins = savesConfig.getInt("players." + uuid + ".chatgames_wins", 0);

        return wins;
    }
    
    private String obtenerNombreGrupo(Player player) {
        // Verificar si el jugador tiene un grupo asignado
        for (Entry<String, minealex.tchat.TChat.ChatGroup> entry : plugin.getGroups().entrySet()) {
            String groupName = entry.getKey();

            if (player.hasPermission("tchat.group." + groupName)) {
                return groupName;
            }
        }

        // Si no se encontró un grupo, devolver un valor predeterminado o vacío según lo desees
        return "";
    }

    private String obtenerLuckPermsSuffix(Player player) {
        User user = LuckPermsProvider.get().getUserManager().getUser(player.getUniqueId());

        if (user != null) {
            String suffix = user.getCachedData().getMetaData().getSuffix();
            return suffix != null ? suffix : "";
        }

        return "";
    }
    
    private String obtenerJugadoresIgnorados(Player player) {
        String uuid = player.getUniqueId().toString();
        List<String> jugadoresIgnorados = plugin.getConfig().getStringList("players." + uuid + ".ignore");

        if (jugadoresIgnorados != null) {
            return String.valueOf(jugadoresIgnorados.size());
        }

        return "0";
    }

    private String obtenerNick(Player player) {
        String playerName = player.getName();
        String nick = plugin.getConfig().getString("players." + player.getUniqueId() + ".nick");

        if (nick != null) {
            return nick;
        }

        return playerName;
    }
}
