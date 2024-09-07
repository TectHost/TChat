package listeners;

import config.BannedCommandsManager;
import minealex.tchat.TChat;
import org.bukkit.command.CommandMap;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.List;

public class TabCompleteListener implements Listener {

    private final TChat plugin;
    private final BannedCommandsManager bannedCommandsManager;
    private final List<String> remove;
    private final List<String> add;

    public TabCompleteListener(@NotNull TChat plugin) {
        this.plugin = plugin;
        this.bannedCommandsManager = plugin.getBannedCommandsManager();
        this.remove = bannedCommandsManager.getNoTabCompleteCommands();
        this.add = bannedCommandsManager.getAddTabCommands();
    }

    @EventHandler
    void onPlayerCommandSend(@NotNull PlayerCommandSendEvent event) {
        if (!event.getPlayer().hasPermission(bannedCommandsManager.getBypassPermissionTab()) && !event.getPlayer().hasPermission("tchat.admin")) {
            if (bannedCommandsManager.getBlockAllCommands()) {
                event.getCommands().removeIf(command -> command.contains(":"));
            }

            event.getCommands().removeAll(this.remove);
        }

        registerCommands();
    }

    private void registerCommands() {
        CommandMap commandMap = getCommandMap();

        for (String commandName : this.add) {
            if (commandMap != null && commandMap.getCommand(commandName) == null) {
                BukkitCommand dynamicCommand = new BukkitCommand(commandName) {
                    @Override
                    public boolean execute(@NotNull org.bukkit.command.CommandSender sender, @NotNull String label, @NotNull String[] args) {
                        return true;
                    }
                };

                commandMap.register(plugin.getName(), dynamicCommand);
            }
        }
    }

    private @Nullable CommandMap getCommandMap() {
        try {
            Field commandMapField = plugin.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            return (CommandMap) commandMapField.get(plugin.getServer());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return null;
        }
    }
}
