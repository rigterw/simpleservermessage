package net.spoxy;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.geysermc.floodgate.api.FloodgateApi;

/**
 * Hello world!
 *
 */
public class SimpleServerMessage extends JavaPlugin {

    static String bedrockPrefix;

    private MessageBroadcaster messager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        messager = new MessageBroadcaster(this);
        Init();
    };

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("ssmreload")) {
            if (!sender.hasPermission("ssm.reload")) {
                sender.sendMessage("§cYou do not have permission to use this command.");
                return true;
            }

            reloadConfig();
            Init();
            sender.sendMessage("§aSimpleServerMessage config reloaded!");
            return true;
        }
        return false;
    }

    /**
     * Initialize the plugin, load the config and set up commands and messages
     */
    private void Init() {

        FileConfiguration config = getConfig();

        RespondCommandFactory.registerCommands(config.getConfigurationSection("commands"));
        messager.setConfig(config.getConfigurationSection("messager"));

    }

    /**
     * Convert & color codes to § color codes, while allowing escaped /& codes to
     * remain as &
     * 
     * @param input The string to translate
     * @return A string where & color codes are replaced with § color codes
     */
    public static String translateColors(String input) {
        if (input == null)
            return null;

        // 1️⃣ Replace unescaped & codes with §
        input = input.replaceAll("(?i)(?<!/)&([0-9a-fk-or])", "§$1");

        // 2️⃣ Replace \& with &
        input = input.replaceAll("/&", "&");

        return input;
    }

    /**
     * Check if a player is a Bedrock player using Floodgate
     * 
     * @param player The object of the player
     * @return True if the player is using the floodgate plugin (Bedrock), false if
     *         not. Also returns false if no floodgate plugin is found
     */
    public static boolean isBedrockPlayer(Player player) {
        FloodgateApi floodgate = FloodgateApi.getInstance();
        if (floodgate == null) {
            return false;

        }
        return floodgate.isFloodgatePlayer(player.getUniqueId());

    }
}
