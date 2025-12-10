package al3x.StarsMessagesChat;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Commands implements CommandExecutor, TabCompleter {

    private final Main plugin;

    public Commands(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            sendHelp(sender);
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("starsmessageschat.reload")) {
                sender.sendMessage("§cУ вас нет прав на выполнение этой команды!");
                return true;
            }

            plugin.reloadPlugin();
            sender.sendMessage("§aКонфигурация плагина StarsMessagesChat перезагружена!");
            return true;
        }

        if (args[0].equalsIgnoreCase("send")) {
            if (!sender.hasPermission("starsmessageschat.send")) {
                sender.sendMessage("§cУ вас нет прав на выполнение этой команды!");
                return true;
            }

            if (args.length < 2) {
                sender.sendMessage("§cИспользование: /" + label + " send <номер_сообщения>");
                return true;
            }

            try {
                int messageIndex = Integer.parseInt(args[1]) - 1;
                List<List<String>> messages = plugin.getConfigManager().getMessages();

                if (messageIndex < 0 || messageIndex >= messages.size()) {
                    sender.sendMessage("§cСообщение с номером " + (messageIndex + 1) + " не найдено!");
                    sender.sendMessage("§cВсего сообщений: " + messages.size());
                    return true;
                }

                List<String> message = messages.get(messageIndex);
                for (String line : message) {
                    sender.sendMessage(line);
                }

                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_CHICKEN_EGG, 1.0f, 1.0f);
                }

            } catch (NumberFormatException e) {
                sender.sendMessage("§cНомер сообщения должен быть числом!");
            }
            return true;
        }

        sendHelp(sender);
        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage("§6§lStarsMessagesChat §7- Помощь по командам:");
        sender.sendMessage("§e/smc reload §7- Перезагрузить конфиг плагина");
        sender.sendMessage("§e/smc send <номер> §7- Отправить конкретное сообщение");
        sender.sendMessage("§e/smc help §7- Показать это меню");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.add("reload");
            completions.add("send");
            completions.add("help");
        } else if (args.length == 2 && args[0].equalsIgnoreCase("send")) {
            int messageCount = plugin.getConfigManager().getMessages().size();
            for (int i = 1; i <= messageCount; i++) {
                completions.add(String.valueOf(i));
            }
        }

        return completions;
    }
}