package hook;

import minealex.tchat.TChat;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class DiscordHook {

    private final TChat plugin;

    public DiscordHook(TChat plugin) {
        this.plugin = plugin;
    }

    public void sendMessage(String message) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(plugin.getDiscordManager().getDiscordHook());
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);

            String jsonPayload = "{\"content\": \"" + escapeJson(message) + "\"}";
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int code = connection.getResponseCode();
            if (code != HttpURLConnection.HTTP_NO_CONTENT) {
                plugin.getLogger().info("Failed to send message. HTTP error code: " + code);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public void sendJoinMessage(String username) {
        if (plugin.getDiscordManager().isJoinEnabled()) {
            HttpURLConnection connection = null;
            try {
                URL url = new URL(plugin.getDiscordManager().getDiscordHook());
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json; utf-8");
                connection.setRequestProperty("Accept", "application/json");
                connection.setDoOutput(true);

                String jsonPayload = buildEmbedJoin(username);
                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                int code = connection.getResponseCode();
                if (code != HttpURLConnection.HTTP_NO_CONTENT) {
                    plugin.getLogger().info("Failed to send message. HTTP error code: " + code);
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }
    }

    public void sendLeftMessage(String username) {
        if (plugin.getDiscordManager().isQuitEnabled()) {
            HttpURLConnection connection = null;
            try {
                URL url = new URL(plugin.getDiscordManager().getDiscordHook());
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json; utf-8");
                connection.setRequestProperty("Accept", "application/json");
                connection.setDoOutput(true);

                String jsonPayload = buildEmbedLeft(username);
                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                int code = connection.getResponseCode();
                if (code != HttpURLConnection.HTTP_NO_CONTENT) {
                    plugin.getLogger().info("Failed to send message. HTTP error code: " + code);
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }
    }

    private String buildEmbedLeft(String username) {
        String descriptionTemplate = plugin.getDiscordManager().getQuitDescription();
        String description = descriptionTemplate.replace("%player%", escapeJson(username));
        String avatarUrl = "https://mc-heads.net/avatar/" + username;

        StringBuilder embedBuilder = new StringBuilder();
        embedBuilder.append("{")
                .append("\"embeds\": [{").append("\"title\": \"").append(plugin.getDiscordManager().getQuitTitle()).append("\",").append("\"description\": \"").append(escapeJson(description)).append("\",").append("\"color\": ").append(plugin.getDiscordManager().getQuitColor());

        if (plugin.getDiscordManager().isQuitAvatarEnabled()) {
            embedBuilder.append(",")
                    .append("\"thumbnail\": {").append("\"url\": \"").append(escapeJson(avatarUrl)).append("\"")
                    .append("}");
        }

        embedBuilder.append("}]")
                .append("}");

        return embedBuilder.toString();
    }

    private String buildEmbedJoin(String username) {
        String descriptionTemplate = plugin.getDiscordManager().getJoinDescription();
        String description = descriptionTemplate.replace("%player%", escapeJson(username));
        String avatarUrl = "https://mc-heads.net/avatar/" + username;

        StringBuilder embedBuilder = new StringBuilder();
        embedBuilder.append("{")
                .append("\"embeds\": [{").append("\"title\": \"").append(plugin.getDiscordManager().getJoinTitle()).append("\",").append("\"description\": \"").append(escapeJson(description)).append("\",").append("\"color\": ").append(plugin.getDiscordManager().getJoinColor());

        if (plugin.getDiscordManager().isJoinAvatarEnabled()) {
            embedBuilder.append(",")
                    .append("\"thumbnail\": {").append("\"url\": \"").append(escapeJson(avatarUrl)).append("\"")
                    .append("}");
        }

        embedBuilder.append("}]")
                .append("}");

        return embedBuilder.toString();
    }

    public void sendDeathMessage(String username) {
        if (plugin.getDiscordManager().isDeathEnabled()) {
            HttpURLConnection connection = null;
            try {
                URL url = new URL(plugin.getDiscordManager().getDiscordHook());
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json; utf-8");
                connection.setRequestProperty("Accept", "application/json");
                connection.setDoOutput(true);

                String jsonPayload = buildEmbedDeath(username);
                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                int code = connection.getResponseCode();
                if (code != HttpURLConnection.HTTP_NO_CONTENT) {
                    plugin.getLogger().info("Failed to send message. HTTP error code: " + code);
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }
    }

    private String buildEmbedDeath(String username) {
        String descriptionTemplate = plugin.getDiscordManager().getDeathDescription();
        String description = descriptionTemplate.replace("%player%", escapeJson(username));
        String avatarUrl = "https://mc-heads.net/avatar/" + username;

        StringBuilder embedBuilder = new StringBuilder();
        embedBuilder.append("{")
                .append("\"embeds\": [{").append("\"title\": \"").append(plugin.getDiscordManager().getDeathTitle()).append("\",").append("\"description\": \"").append(escapeJson(description)).append("\",").append("\"color\": ").append(plugin.getDiscordManager().getDeathColor());

        if (plugin.getDiscordManager().isDeathAvatarEnabled()) {
            embedBuilder.append(",")
                    .append("\"thumbnail\": {").append("\"url\": \"").append(escapeJson(avatarUrl)).append("\"")
                    .append("}");
        }

        embedBuilder.append("}]")
                .append("}");

        return embedBuilder.toString();
    }

    private String escapeJson(String message) {
        return message.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
