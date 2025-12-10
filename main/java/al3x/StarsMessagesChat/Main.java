package al3x.StarsMessagesChat;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class Main extends JavaPlugin {

    private ConfigManager configManager;
    private BukkitRunnable announcementTask;
    private int currentMessageIndex = 0;

    @Override
    public void onEnable() {
        // Сохраняем дефолтный конфиг
        saveDefaultConfig();
        
        // Инициализируем менеджер конфига
        configManager = new ConfigManager(this);
        
        // Регистрируем команду
        getCommand("starsmessages").setExecutor(new Commands(this));
        
        // Запускаем задачу отправки сообщений
        startAnnouncementTask();
        
        getLogger().info("Плагин StarsMessagesChat успешно запущен!");
    }

    @Override
    public void onDisable() {
        // Останавливаем задачу при выключении плагина
        if (announcementTask != null) {
            announcementTask.cancel();
        }
        
        getLogger().info("Плагин StarsMessagesChat выключен.");
    }

    private void startAnnouncementTask() {
        // Получаем интервал из конфига (в тиках, 20 тиков = 1 секунда)
        int interval = configManager.getTimeInterval() * 20; // Конвертируем секунды в тики
        
        announcementTask = new BukkitRunnable() {
            @Override
            public void run() {
                sendNextAnnouncement();
            }
        };
        
        // Запускаем задачу с заданным интервалом
        announcementTask.runTaskTimer(this, 0L, interval);
    }

    private void sendNextAnnouncement() {
        List<List<String>> messages = configManager.getMessages();
        
        if (messages.isEmpty()) {
            getLogger().warning("В конфиге нет сообщений для отправки!");
            return;
        }
        
        // Получаем текущее сообщение
        List<String> currentMessage = messages.get(currentMessageIndex);
        
        // Отправляем сообщение всем онлайн-игрокам
        for (Player player : Bukkit.getOnlinePlayers()) {
            for (String line : currentMessage) {
                if (line != null && !line.isEmpty()) {
                    player.sendMessage(line);
                }
            }
            
            // Воспроизводим звук Chicken Plop
            player.playSound(player.getLocation(), Sound.ENTITY_CHICKEN_EGG, 1.0f, 1.0f);
        }
        
        // Переходим к следующему сообщению
        currentMessageIndex = (currentMessageIndex + 1) % messages.size();
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public void reloadPlugin() {
        reloadConfig();
        configManager = new ConfigManager(this);
        
        // Перезапускаем задачу
        if (announcementTask != null) {
            announcementTask.cancel();
        }
        currentMessageIndex = 0;
        startAnnouncementTask();
    }
}