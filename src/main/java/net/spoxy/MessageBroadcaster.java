package net.spoxy;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class MessageBroadcaster {

    private int bedrockIndex = 0;
    private int javaIndex = 0;
    private int taskId;

    private String[] bedrockMessages;
    private String[] javaMessages;

    private boolean random = false;

    private JavaPlugin plugin;

    public MessageBroadcaster(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void setConfig(ConfigurationSection config) {

        int interval = config.getInt("interval", 300);
        random = config.getBoolean("random", false);

        javaMessages = _createArray(config, "messages");
        bedrockMessages = _createArray(config, "bedrockMessages");

        BukkitScheduler scheduler = Bukkit.getScheduler();

        if (taskId != 0) {
            // Cancel old task
            scheduler.cancelTask(taskId);
        }

        taskId = scheduler.scheduleSyncRepeatingTask(plugin, this::_sendMessage, interval * 1200L,
                interval * 1200L);

    }

    private void _sendMessage() {

        _setIndexes();

        String javaMessage = javaMessages[javaIndex];
        String bedrockMessage = bedrockMessages[bedrockIndex];

        // Broadcast the messages
        if (javaMessage.equals(bedrockMessage)) {
            // If the messages are the same, just send one message
            Bukkit.broadcastMessage(javaMessage);
        } else {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage(SimpleServerMessage.isBedrockPlayer(player) ? bedrockMessage : javaMessage);
            }
        }
    }

    private void _setIndexes() {
        if (random) {
            bedrockIndex = (int) (Math.random() * bedrockMessages.length);
            javaIndex = (int) (Math.random() * javaMessages.length);
        } else {
            bedrockIndex = (bedrockIndex + 1) % bedrockMessages.length;
            javaIndex = (javaIndex + 1) % javaMessages.length;
        }

        if (javaMessages.length == bedrockMessages.length) {
            // Sync indexes if the lengths are the same
            bedrockIndex = javaIndex;
        }
    }

    private String[] _createArray(ConfigurationSection config, String key) {
        String[] messages = config.getStringList(key).toArray(new String[0]);
        if (messages.length == 0) {
            // If there are no bedrock messages, use the java ones
            if (key.equals("bedrockMessages")) {
                return javaMessages;
            } else {
                return new String[] { "No messages set" };
            }
        }

        for (int i = 0; i < messages.length; i++) {
            messages[i] = SimpleServerMessage.translateColors(messages[i]);
        }

        return messages;

    }
}
