package al3x.StarsMessagesChat;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class ConfigManager {

    private final Main plugin;
    private final FileConfiguration config;
    private final List<List<String>> messages;
    private int timeInterval;

    public ConfigManager(Main plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        this.messages = new ArrayList<>();
        
        loadConfig();
    }

    private void loadConfig() {
        // Загружаем интервал
        timeInterval = config.getInt("time", 240);
        
        // Загружаем сообщения
        messages.clear();
        ConfigurationSection messagesSection = config.getConfigurationSection("messages");
        
        if (messagesSection != null) {
            for (String key : messagesSection.getKeys(false)) {
                if (messagesSection.isList(key)) {
                    List<String> messageLines = messagesSection.getStringList(key);
                    List<String> coloredLines = new ArrayList<>();
                    
                    // Применяем цвета к каждой строке
                    for (String line : messageLines) {
                        coloredLines.add(ChatColor.translateAlternateColorCodes('&', line));
                    }
                    
                    messages.add(coloredLines);
                }
            }
        }
        
        // Если нет сообщений в sections, проверяем старый формат
        if (messages.isEmpty()) {
            List<String> defaultMessage = new ArrayList<>();
            defaultMessage.add(ChatColor.translateAlternateColorCodes('&', "&7"));
            defaultMessage.add(ChatColor.translateAlternateColorCodes('&', " &x&F&C&7&B&0&0&lꐁ &fХотите &x&F&C&7&B&0&0&lподдержать &x&F&C&7&B&0&0&lсервер &fи получить доступ"));
            defaultMessage.add(ChatColor.translateAlternateColorCodes('&', " &x&F&C&7&B&0&0&lꐁ &fк эксклюзивным возможностям?"));
            defaultMessage.add(ChatColor.translateAlternateColorCodes('&', " &x&F&C&7&B&0&0&lꐁ &fПокупайте донат услуги на нашем &x&F&C&7&B&0&0&lсайте"));
            defaultMessage.add(ChatColor.translateAlternateColorCodes('&', " &x&F&C&7&B&0&0&lꐀ LightWorld.ru"));
            defaultMessage.add(ChatColor.translateAlternateColorCodes('&', "&7"));
            messages.add(defaultMessage);
        }
    }

    public List<List<String>> getMessages() {
        return messages;
    }

    public int getTimeInterval() {
        return timeInterval;
    }
}