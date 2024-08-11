package commands;

import minealex.tchat.TChat;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Damageable;
import org.jetbrains.annotations.NotNull;

public class ShowItemCommand implements CommandExecutor {

    private final TChat plugin;

    public ShowItemCommand(TChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull org.bukkit.command.Command command, @NotNull String label, String[] args) {
        String prefix = plugin.getMessagesManager().getPrefix();
        if (!(sender instanceof Player player)) {
            String message = plugin.getMessagesManager().getNoPlayer();
            sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + message));
            return false;
        }

        if (player.hasPermission("tchat.admin") || player.hasPermission("tchat.showitem")) {

            ItemStack item = player.getInventory().getItemInMainHand();

            if (item.getType().isAir()) {
                player.sendMessage("(BETA) You do not have any items in hand.");
                return false;
            }

            ItemMeta meta = item.getItemMeta();
            if (meta == null) {
                player.sendMessage("(BETA) The item has no meta.");
                return false;
            }

            String itemName = meta.hasDisplayName() ? meta.getDisplayName() : item.getType().toString();

            int durability = item.getType().getMaxDurability() - (meta instanceof Damageable ? ((Damageable) meta).getDamage() : 0);

            assert meta instanceof Damageable;
            String itemJson = String.format(
                    "{\"id\":\"%s\",\"Count\":1,\"tag\":{\"display\":{\"Name\":\"{\\\"text\\\":\\\"%s\\\",\\\"italic\\\":false}\"},\"Damage\":%d}}",
                    item.getType().getKey().getKey(),
                    itemName,
                    ((Damageable) meta).getDamage()
            );

            TextComponent itemComponent = new TextComponent(itemName);
            itemComponent.setColor(net.md_5.bungee.api.ChatColor.WHITE);
            itemComponent.setBold(true);

            itemComponent.setHoverEvent(new net.md_5.bungee.api.chat.HoverEvent(
                    net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_ITEM,
                    new ComponentBuilder(itemJson).create()
            ));

            player.spigot().sendMessage(itemComponent);
        } else {
            String message = plugin.getMessagesManager().getNoPermission();
            player.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
        }

        return true;
    }
}
