package utils;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

    public int getChannelPlayerCount(String channelName) {
        int count = 0;
        for (String channel : playerChannels.values()) {
            if (channel.equalsIgnoreCase(channelName)) {
                count++;
            }
        }
        return count;
    }

    public void removePlayerFromChannel(Player player) {
        playerChannels.remove(player);
    }

    public Set<Player> getPlayersInChannel(String channelName) {
        Set<Player> playersInChannel = new HashSet<>();
        for (Map.Entry<Player, String> entry : playerChannels.entrySet()) {
            if (entry.getValue().equalsIgnoreCase(channelName)) {
                playersInChannel.add(entry.getKey());
            }
        }
        return playersInChannel;
    }
}
