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
        saveDefaultConfig();
        
        configManager = new ConfigManager(this);
        
        getCommand("starsmessages").setExecutor(new Commands(this));
        
        startAnnouncementTask();
        
        getLogger().info("Плагин StarsMessagesChat успешно запущен!");
    }

    @Override
    public void onDisable() {
        if (announcementTask != null) {
            announcementTask.cancel();
        }
        
        getLogger().info("Плагин StarsMessagesChat выключен.");
    }

    private void startAnnouncementTask() {
        int interval = configManager.getTimeInterval() * 20;
        
        announcementTask = new BukkitRunnable() {
            @Override
            public void run() {
                sendNextAnnouncement();
            }
        };
        
        announcementTask.runTaskTimer(this, 0L, interval);
    }

    private void sendNextAnnouncement() {
        List<List<String>> messages = configManager.getMessages();
        
        if (messages.isEmpty()) {
            getLogger().warning("В конфиге нет сообщений для отправки!");
            return;
        }
        
        List<String> currentMessage = messages.get(currentMessageIndex);
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            for (String line : currentMessage) {
                if (line != null && !line.isEmpty()) {
                    player.sendMessage(line);
                }
            }
            
            player.playSound(player.getLocation(), Sound.ENTITY_CHICKEN_EGG, 1.0f, 1.0f);
        }
        
        currentMessageIndex = (currentMessageIndex + 1) % messages.size();
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public void reloadPlugin() {
        reloadConfig();
        configManager = new ConfigManager(this);
        
        if (announcementTask != null) {
            announcementTask.cancel();
        }
        currentMessageIndex = 0;
        startAnnouncementTask();
    }

}
