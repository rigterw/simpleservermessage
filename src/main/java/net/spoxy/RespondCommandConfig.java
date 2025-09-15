package net.spoxy;

import java.util.List;

import org.bukkit.configuration.ConfigurationSection;

public record RespondCommandConfig(String main, List<String> aliases, String response, String bedrockResponse,
        String click,
        String hover) {

    public RespondCommandConfig(String key, ConfigurationSection section) {
        this(
                (section.getStringList("command") == null || section.getStringList("command").isEmpty())
                        ? key
                        : section.getStringList("command").get(0),
                (section.getStringList("command") == null || section.getStringList("command").isEmpty())
                        ? new java.util.ArrayList<>()
                        : section.getStringList("command").subList(1, section.getStringList("command").size()),
                SimpleServerMessage.translateColors(section.getString("response", "No response set")),
                SimpleServerMessage.translateColors(
                        section.getString("bedrockResponse", section.getString("response", "No response set"))),
                section.getString("click", null),
                section.getString("hover", null));
    }

}
