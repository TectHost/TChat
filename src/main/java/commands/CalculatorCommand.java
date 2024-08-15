package commands;

import minealex.tchat.TChat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class CalculatorCommand implements CommandExecutor {

    private final TChat plugin;

    public CalculatorCommand(TChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {
        String prefix = plugin.getMessagesManager().getPrefix();
        if (args.length != 3) {
            String message = plugin.getMessagesManager().getUsageCalculator();
            sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + message));
            return false;
        }

        try {
            double num1 = Double.parseDouble(args[0]);
            String operator = args[1];
            double num2 = Double.parseDouble(args[2]);

            double result;
            switch (operator) {
                case "+":
                    result = num1 + num2;
                    break;
                case "-":
                    result = num1 - num2;
                    break;
                case "*":
                    result = num1 * num2;
                    break;
                case "/":
                    if (num2 == 0) {
                        String message = plugin.getMessagesManager().getDivisionZero();
                        sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + message));
                        return false;
                    }
                    result = num1 / num2;
                    break;
                default:
                    String message = plugin.getMessagesManager().getInvalidOperator();
                    sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + message));
                    return false;
            }

            String message = plugin.getMessagesManager().getCalculatorResult();
            message = message.replace("%result%", String.valueOf(result));
            sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + message));
            return true;

        } catch (NumberFormatException e) {
            String message = plugin.getMessagesManager().getInvalidNumber();
            sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + message));
            return false;
        }
    }
}
