package net.spoxy;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Hello world!
 *
 */
public class SimpleServerMessage extends JavaPlugin {

    static String bedrockPrefix;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        Plugin floodGate = Bukkit.getPluginManager().getPlugin("floodgate");
        if (floodGate != null && floodGate.isEnabled()) {
            bedrockPrefix = floodGate.getConfig().getString("username-prefix");
        } else {
            bedrockPrefix = "";
        }

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

    // Reinitialize the plugin (e.g. after a config reload)
    private void Init() {
        RespondCommandFactory.registerCommands(getConfig().getConfigurationSection("commands"));

    }

    public static String translateColors(String input) {
        if (input == null)
            return null;

        // 1️⃣ Replace unescaped & codes with §
        input = input.replaceAll("(?i)(?<!/)&([0-9a-fk-or])", "§$1");

        // 2️⃣ Replace \& with &
        input = input.replaceAll("/&", "&");

        return input;
    }
}
