package utils;

import minealex.tchat.TChat;
import org.bukkit.scheduler.BukkitRunnable;

public class PollScheduler {

    public static void startPollChecker(TChat plugin) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Poll currentPoll = Poll.getCurrentPoll();
                if (currentPoll != null && currentPoll.hasEnded()) {
                    currentPoll.finalizePoll();
                }
            }
        }.runTaskTimer(plugin, 20L, 20L);
    }
}
