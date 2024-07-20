package utils;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class ChannelsManager {

    private final Map<Player, String> playerChannels = new HashMap<>();

    public void setPlayerChannel(Player player, String channelName) {
        playerChannels.put(player, channelName);
    }

    public String getPlayerChannel(Player player) {
        return playerChannels.get(player);
    }

    public boolean isPlayerInChannel(Player player) {
        return playerChannels.containsKey(player);
    }

    public void removePlayerFromChannel(Player player) {
        playerChannels.remove(player);
    }
}
