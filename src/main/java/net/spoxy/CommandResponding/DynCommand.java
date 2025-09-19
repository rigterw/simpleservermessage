package net.spoxy.CommandResponding;

import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.spoxy.SimpleServerMessage;

public class DynCommand extends BukkitCommand {

    private RespondCommandConfig commandConfig;
    private boolean active = true;

    public DynCommand(RespondCommandConfig commandConfig) {
        super(commandConfig.main().toLowerCase());
        setConfig(commandConfig);
    }

    public void setConfig(RespondCommandConfig commandConfig) {
        this.active = true;
        this.commandConfig = commandConfig;
        this.setAliases(commandConfig.aliases().stream().map(String::toLowerCase).toList());
    }

    public void disable() {
        this.active = false;
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        if (!active) {
            sender.sendMessage("§cUnknown command. Type \"/help\" for help.");
            return true;
        }
        Player player = (Player) sender;

        // Check if the player is a Bedrock player by checking the prefix
        String message = SimpleServerMessage.isBedrockPlayer(player)
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

}
