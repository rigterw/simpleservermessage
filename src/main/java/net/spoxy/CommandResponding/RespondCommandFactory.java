package net.spoxy.CommandResponding;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.configuration.ConfigurationSection;

public class RespondCommandFactory {

    private Map<String, DynCommand> _commands = new HashMap<String, DynCommand>();

    /**
     * Register or update commands based on the given configuration section
     * 
     * @param commandsSection The commands section from the config
     */
    public void registerCommands(ConfigurationSection commandsSection) {
        CommandMap commandMap;
        List<String> currentCommands = new ArrayList<>(_commands.keySet());

        // load command Map
        try {
            Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return;
        }

        // Register or update all commands from the config
        for (String key : commandsSection.getKeys(false)) {
            // Create a command config object from the config
            ConfigurationSection commandSection = commandsSection.getConfigurationSection(key);
            RespondCommandConfig commandConfig = new RespondCommandConfig(key, commandSection);

            // If the command already exists, update it. Otherwise, register a new command.
            if (currentCommands.contains(key)) {
                _commands.get(key).setConfig(commandConfig);
                currentCommands.remove(key);
            } else {
                _registerCommand(commandConfig, commandMap);
            }
        }

        // disable all commands that got removed.
        for (String key : currentCommands) {
            _commands.get(key).disable();
        }
    }

    /**
     * Register a new command in the command map
     * 
     * @param commandConfig The command configuration
     * @param commandMap    The command map to register the command in
     */
    private void _registerCommand(RespondCommandConfig commandConfig, CommandMap commandMap) {
        DynCommand command = new DynCommand(commandConfig);
        commandMap.register("ssm", command);
        _commands.put(command.getName(), command);
    }

}
