package minealex.tchat.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import me.clip.placeholderapi.PlaceholderAPI;

import java.io.FileReader;

import minealex.tchat.TChat;

public class JoinListener implements Listener {

    private final TChat plugin;

    public JoinListener(TChat plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        try {
            boolean motdEnabled = isMotdEnabled();
            if (motdEnabled) {
                // Enviar el mensaje de MOTD al jugador
                sendMotdMessage(event.getPlayer().getName());
            }

            // Obtener y configurar el mensaje de join
            String joinMessage = getConfiguredJoinMessage(event.getPlayer().getName());
            if (joinMessage != null) {
                event.setJoinMessage(joinMessage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        try {
            // Obtener y configurar el mensaje de quit
            String quitMessage = getConfiguredQuitMessage(event.getPlayer().getName());
            if (quitMessage != null) {
                event.setQuitMessage(quitMessage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
	private boolean isMotdEnabled() {
        try {
            String filePath = plugin.getDataFolder().getPath() + "/format_config.json";
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(new FileReader(filePath));
            JSONObject jsonObject = (JSONObject) obj;

            JSONObject motd = (JSONObject) jsonObject.get("MOTD");

            // Obtener el valor de la propiedad motdEnabled (predeterminado es true si no está presente)
            return (boolean) motd.getOrDefault("motdEnabled", true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true; // Retorna true por defecto si hay un error
    }

    @SuppressWarnings({ "unchecked", "deprecation" })
    private String getConfiguredJoinMessage(String playerName) {
        try {
            String filePath = plugin.getDataFolder().getPath() + "/format_config.json";
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(new FileReader(filePath));
            JSONObject jsonObject = (JSONObject) obj;

            JSONObject joins = (JSONObject) jsonObject.get("Joins");

            boolean joinMessagesEnabled = (boolean) joins.getOrDefault("joinMessagesEnabled", true);

            if (!joinMessagesEnabled) {
                return null; // Retorna null si los mensajes de join están deshabilitados
            }

            if (joins != null && joins.containsKey("JoinMessage")) {
                String joinMessage = (String) joins.get("JoinMessage");
                joinMessage = joinMessage.replace("%player%", playerName);
                joinMessage = PlaceholderAPI.setPlaceholders(Bukkit.getOfflinePlayer(playerName), joinMessage);
                return ChatColor.translateAlternateColorCodes('&', joinMessage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null; // Retorna null si no se pudo obtener el mensaje configurado
    }

    @SuppressWarnings({ "unchecked", "deprecation" })
    private String getConfiguredQuitMessage(String playerName) {
        try {
            String filePath = plugin.getDataFolder().getPath() + "/format_config.json";
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(new FileReader(filePath));
            JSONObject jsonObject = (JSONObject) obj;

            JSONObject quits = (JSONObject) jsonObject.get("Joins");

            boolean quitMessagesEnabled = (boolean) quits.getOrDefault("quitMessagesEnabled", true);

            if (!quitMessagesEnabled) {
                return null; // Retorna null si los mensajes de quit están deshabilitados
            }

            if (quits != null && quits.containsKey("QuitMessage")) {
                String quitMessage = (String) quits.get("QuitMessage");
                quitMessage = quitMessage.replace("%player%", playerName);
                quitMessage = PlaceholderAPI.setPlaceholders(Bukkit.getOfflinePlayer(playerName), quitMessage);
                return ChatColor.translateAlternateColorCodes('&', quitMessage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null; // Retorna null si no se pudo obtener el mensaje configurado
    }

    private void sendMotdMessage(String playerName) {
        try {
            String filePath = plugin.getDataFolder().getPath() + "/format_config.json";
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(new FileReader(filePath));
            JSONObject jsonObject = (JSONObject) obj;

            if (jsonObject.containsKey("MOTD")) {
                JSONObject motd = (JSONObject) jsonObject.get("MOTD");

                @SuppressWarnings("unchecked")
				boolean motdEnabled = (boolean) motd.getOrDefault("motdEnabled", true);

                if (motdEnabled && motd.containsKey("motdMessage")) {
                    JSONArray motdMessages = (JSONArray) motd.get("motdMessage");
                    for (Object message : motdMessages) {
                        // Enviar cada línea del mensaje de MOTD al jugador
                        String formattedMessage = ChatColor.translateAlternateColorCodes('&', (String) message);
                        Bukkit.getPlayer(playerName).sendMessage(formattedMessage);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
