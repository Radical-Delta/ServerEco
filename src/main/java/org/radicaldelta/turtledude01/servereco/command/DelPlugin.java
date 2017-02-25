package org.radicaldelta.turtledude01.servereco.command;

import org.radicaldelta.turtledude01.servereco.Config;
import org.radicaldelta.turtledude01.servereco.ServerEco;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;

public class DelPlugin implements CommandExecutor {
    Config config = ServerEco.getServerEco().getConfig();
    ServerEco serverEco = ServerEco.getServerEco();
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (src.hasPermission("servereco.command.del")) {
            String plugin = args.<String>getOne("plugin").get();

            config.plugin.remove(plugin);
            serverEco.saveConfig();
        }
        return CommandResult.success();
    }
}
