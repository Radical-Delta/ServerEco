package org.radicaldelta.turtledude01.servereco.command;

import org.radicaldelta.turtledude01.servereco.Config;
import org.radicaldelta.turtledude01.servereco.ServerEco;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class DebugToggle implements CommandExecutor {
    Config config = ServerEco.getServerEco().getConfig();
    ServerEco serverEco = ServerEco.getServerEco();

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (src.hasPermission("servereco.command.add")) {
            Boolean bool;
            if (!args.getOne("boolean").isPresent()) {
                if (config.debug) {
                    bool = false;
                } else {
                    bool = true;
                }
            } else {
                bool = args.<Boolean>getOne("boolean").get();
            }

            src.sendMessage(Text.of(TextColors.GREEN, "Debug", TextColors.RED, " = ", TextColors.GREEN, bool.toString()));
            config.debug = bool;
            serverEco.saveConfig();
        }
        return CommandResult.success();
    }
}
