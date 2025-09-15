package net.spoxy;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

public class RespondCommandFactory {

    public static void registerCommands() {
        CommandMap commandMap;
        Map<String, Object> commands = getCommands();

        try {
            Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }

    }

    private static Array<RespondCommandConfig> getCommands() {
        var commandsSection = getConfig().getConfigurationSection("commands");

        List<RespondCommandConfig> commands = new ArrayList<>();
        for (String key : commandsSection.getKeys(false)) {
            String[] aliases = commandSection.getStringList("aliases").toArray(new String[0]);

            // If no aliases are specified, listen to the command name
            if (aliases.length == 0) {
                aliases = new String[] { key };
            }
            String commandSection = commandsSection.getConfigurationSection(key);
            String response = commandSection.getString("response", "No response set");
            String bedrockResponse = commandSection.getString("bedrockResponse", response);
            String click = commandSection.getString("click", null);
            String hover = commandSection.getString("hover", null);

            commands.add(new RespondCommandConfig(aliases, response, bedrockResponse, click, hover));
        }

        return commands.toArray(new RespondCommandConfig[0]);
    }

    private static void registerCommand(RespondCommandConfig commandConfig, CommandMap commandMap) {
        Command command = new BukkitCommand(commandConfig.aliases()[0]) {
            @Override
            public boolean execute(CommandSender sender, String label, String[] args) {
                if (!(sender instanceof Player)) {
                    return false;
                }
                Player player = (Player) sender;

                // Check if the player is a Bedrock player by checking the prefix
                String message = player.getName().substring(0, 1).equals(App.bedrockPrefix)
                        ? commandConfig.bedrockResponse
                        : commandConfig.response();

                if (commandConfig.click() != null || commandConfig.hover() != null) {
                    TextComponent textComponent = new TextComponent(message);
                    if (commandConfig.click() != null) {
                        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, commandConfig.click()));
                    }
                    if (commandConfig.hover() != null) {
                        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                new ComponentBuilder(commandConfig.hover()).create()));
                    }
                    player.spigot().sendMessage(textComponent);
                } else {
                    player.sendMessage(message);
                }
            }
        };

        if (commandConfig.aliases().length > 1) {
            command.setAliases(Arrays.asList(commandConfig.getAliases()).subList(1, commandConfig.getAliases().length));
        }

        commandMap.register(commandConfig.getAliases()[0], command);
    }
}
