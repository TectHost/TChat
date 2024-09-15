package commands;

import config.TagsManager;
import minealex.tchat.TChat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import utils.TagsInventoryManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TagsCommand implements CommandExecutor, TabCompleter {

    private final TagsManager tagsManager;
    private final TChat plugin;
    private final TagsInventoryManager tagsInventoryManager;

    public TagsCommand(@NotNull TChat plugin, TagsManager tagsManager, TagsInventoryManager tagsInventoryManager) {
        this.plugin = plugin;
        this.tagsManager = tagsManager;
        this.tagsInventoryManager = tagsInventoryManager;
        Objects.requireNonNull(plugin.getCommand("tags")).setExecutor(this);
        Objects.requireNonNull(plugin.getCommand("tags")).setTabCompleter(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        String p = plugin.getMessagesManager().getPrefix();
        if (!(sender instanceof Player player)) {
            String m = plugin.getMessagesManager().getNoPlayer();
            sender.sendMessage(plugin.getTranslateColors().translateColors(null, p + m));
            return true;
        }

        if (args.length == 0) {
            if (plugin.getTagsMenuConfigManager().isEnabled()) {
                tagsInventoryManager.openMenu(player, plugin.getTagsMenuConfigManager().getDmenu());
            } else {
                String m = plugin.getMessagesManager().getTagsUsage();
                player.sendMessage(plugin.getTranslateColors().translateColors(player, p + m));
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("list")) {
            if (!player.hasPermission("tchat.tags.list") && !player.hasPermission("tchat.admin")) {
                String m = plugin.getMessagesManager().getNoPermission();
                player.sendMessage(plugin.getTranslateColors().translateColors(player, p + m));
                return true;
            }

            String m = plugin.getMessagesManager().getTagsList();
            player.sendMessage(plugin.getTranslateColors().translateColors(player, p + m));
            for (String tag : tagsManager.getTags().keySet()) {
                player.sendMessage("- " + tag);
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("select")) {
            if (args.length < 2) {
                String m = plugin.getMessagesManager().getTagsUsageSelect();
                player.sendMessage(plugin.getTranslateColors().translateColors(player, p + m));
                return true;
            }

            TagsManager.Tag tag = tagsManager.getTags().get(args[1]);

            if (tag == null) {
                String m = plugin.getMessagesManager().getTagsNotFound().replace("%tag%", args[1]);
                player.sendMessage(plugin.getTranslateColors().translateColors(player, p + m));
                return true;
            }

            if (!player.hasPermission(tag.getPermission()) && !player.hasPermission("tchat.tags.select") && !player.hasPermission("tchat.tags.all") && !player.hasPermission("tchat.admin")) {
                String m = plugin.getMessagesManager().getNoPermission();
                player.sendMessage(plugin.getTranslateColors().translateColors(player, p + m));
                return true;
            }

            String m = plugin.getMessagesManager().getTagsSelected().replace("%tag%", args[1]);
            player.sendMessage(plugin.getTranslateColors().translateColors(player, p + m));
            plugin.getSaveManager().saveSelectedTag(player.getUniqueId(), args[1]);
            return true;
        }

        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String @NotNull [] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            if ("list".startsWith(args[0].toLowerCase()) && sender.hasPermission("tchat.tags.list") || sender.hasPermission("tchat.admin")) {
                completions.add("list");
            }
            if ("select".startsWith(args[0].toLowerCase()) && sender.hasPermission("tchat.tags.select") || sender.hasPermission("tchat.admin")) {
                completions.add("select");
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("select") && sender.hasPermission("tchat.tags.select") || sender.hasPermission("tchat.admin")) {
            for (String tag : tagsManager.getTags().keySet()) {
                if (tag.toLowerCase().startsWith(args[1].toLowerCase())) {
                    completions.add(tag);
                }
            }
        }

        return completions;
    }
}
