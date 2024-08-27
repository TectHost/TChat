package commands;

import minealex.tchat.TChat;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class StaffListCommand implements CommandExecutor {

    private final TChat plugin;

    public StaffListCommand(TChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        String prefix = plugin.getMessagesManager().getPrefix();
        if (!(sender instanceof Player player)) {
            String message = plugin.getMessagesManager().getNoPlayer();
            sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + message));
            return true;
        }

        if (!sender.hasPermission("tchat.command.stafflist")) {
            String message = plugin.getMessagesManager().getNoPermission();
            sender.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
            return true;
        }

        List<String> staffList = new ArrayList<>();

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.hasPermission("tchat.admin") || p.hasPermission("tchat.staff") || p.isOp()) {
                staffList.add(p.getName());
            }
        }

        if (staffList.isEmpty()) {
            String message = plugin.getMessagesManager().getNoStaff();
            sender.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
            return true;
        }

        int staffCount = staffList.size();

        String header = plugin.getMessagesManager().getHeaderStaffList().replace("%staff%", String.valueOf(staffCount));
        String footer = plugin.getMessagesManager().getFooterStaffList();

        String staffNames = ChatColor.GREEN + String.join(ChatColor.WHITE + ", " + ChatColor.GREEN, staffList);

        sender.sendMessage(header);
        sender.sendMessage(staffNames);
        sender.sendMessage(footer);

        return true;
    }
}