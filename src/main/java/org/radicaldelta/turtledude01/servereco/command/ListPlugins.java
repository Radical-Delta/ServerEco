package org.radicaldelta.turtledude01.servereco.command;

import org.radicaldelta.turtledude01.servereco.Config;
import org.radicaldelta.turtledude01.servereco.ServerEco;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ListPlugins implements CommandExecutor {
    Config config = ServerEco.getServerEco().getConfig();
    ServerEco serverEco = ServerEco.getServerEco();

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (src.hasPermission("servereco.command.list")) {
            List<Text> contents = new ArrayList<>();

            Iterator it = config.plugin.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                contents.add(Text.of(TextColors.DARK_AQUA, pair.getKey(), TextColors.RED, " = ", TextColors.DARK_AQUA, pair.getValue(), "\n"));
            }
            contents.add(Text.of(TextColors.GREEN, "----------------"));
            PaginationList.builder()
                    .title(Text.of(TextColors.GOLD, "{ ", TextColors.YELLOW, "Server Eco", TextColors.GOLD, " }"))
                    .contents(contents)
                    .padding(Text.of(TextColors.GREEN, "-"))
                    .sendTo(src);
        }
        return CommandResult.success();
    }
}
