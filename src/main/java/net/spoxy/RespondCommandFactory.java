package net.spoxy;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class RespondCommandFactory {

    private static List<PluginCommand> commands = new ArrayList<>();

    public static void registerCommands(ConfigurationSection commandsSection) {
        CommandMap commandMap;

        try {
            Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return;
        }

        // Unregister old commands
        if (commands.isEmpty() == false)
            unloadCommands(commandMap);

        for (String key : commandsSection.getKeys(false)) {
            ConfigurationSection commandSection = commandsSection.getConfigurationSection(key);
            RespondCommandConfig commandConfig = new RespondCommandConfig(key, commandSection);
            registerCommand(commandConfig, commandMap);
        }
    }

    private static void unloadCommands(CommandMap commandMap) {

        for (PluginCommand cmd : commands) {
            cmd.setExecutor(null);
        }
        commands.clear();

    }

    private static void registerCommand(RespondCommandConfig commandConfig, CommandMap commandMap) {
        Command command = new BukkitCommand(commandConfig.main()) {
            @Override
            public boolean execute(CommandSender sender, String label, String[] args) {
                if (!(sender instanceof Player)) {
                    return false;
                }
                Player player = (Player) sender;

                // Check if the player is a Bedrock player by checking the prefix
                String message = player.getName().substring(0, 1).equals(SimpleServerMessage.bedrockPrefix)
                        ? commandConfig.bedrockResponse()
                        : commandConfig.response();

                if (commandConfig.click() != null || commandConfig.hover() != null) {
                    TextComponent textComponent = new TextComponent(message);
                    if (commandConfig.click() != null) {
                        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, commandConfig.click()));
                    }
                    if (commandConfig.hover() != null) {
                        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                new net.md_5.bungee.api.chat.hover.content.Text(commandConfig.hover())));
                    }
                    player.spigot().sendMessage(textComponent);
                } else {
                    player.sendMessage(message);
                }

                return true;
            }
        };

        if (commandConfig.aliases().size() > 0) {
            command.setAliases(commandConfig.aliases());
        }

        commandMap.register(commandConfig.main(), command);
        // commands.add((PluginCommand) command);
    }
}
