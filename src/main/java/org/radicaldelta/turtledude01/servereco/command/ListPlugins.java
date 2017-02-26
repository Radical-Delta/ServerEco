package org.radicaldelta.turtledude01.servereco.command;

import org.radicaldelta.turtledude01.servereco.Config;
import org.radicaldelta.turtledude01.servereco.ServerEco;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.LiteralText.Builder;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.lang.reflect.Array;
import java.security.Key;
import java.util.*;

public class ListPlugins implements CommandExecutor{
    Config config = ServerEco.getServerEco().getConfig();
    ServerEco serverEco = ServerEco.getServerEco();
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (src.hasPermission("servereco.command.list")) {
            Builder builder = Text.builder("");

            builder.append(Text.of(TextColors.GREEN, "---Server Eco---\n"));
            Iterator it = config.plugin.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                builder.append(Text.of(TextColors.DARK_AQUA, pair.getKey(), TextColors.RED, " = ", TextColors.DARK_AQUA, pair.getValue(), "\n"));
            }
            builder.append(Text.of(TextColors.GREEN, "----------------"));
            src.sendMessage(builder.build());
        }
        return CommandResult.success();
    }
}
