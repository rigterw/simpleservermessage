package net.spoxy;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Hello world!
 *
 */
public class App extends JavaPlugin {

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
    };
}
