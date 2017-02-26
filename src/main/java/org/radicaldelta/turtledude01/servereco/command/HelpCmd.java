package org.radicaldelta.turtledude01.servereco.command;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.ArrayList;
import java.util.List;

public class HelpCmd implements CommandExecutor{
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (src.hasPermission("servereco.command.list")) {
            List<Text> contents = new ArrayList<>();

            contents.add(Text.of(TextColors.GREEN, "/se help",TextColors.RED, " - ", TextColors.GREEN, "Display help (this menu)"));
            contents.add(Text.of(TextColors.GREEN, "/se add <",TextColors.DARK_AQUA, "plugin", TextColors.GREEN,"> <", TextColors.DARK_AQUA, "account", TextColors.GREEN, ">", TextColors.RED, " - ", TextColors.GREEN, "Add a plugin to the config"));
            contents.add(Text.of(TextColors.GREEN, "/se del <", TextColors.DARK_AQUA, "plugin", TextColors.GREEN, ">",TextColors.RED, " - ", TextColors.GREEN, "Delete a plugin from the config"));
            contents.add(Text.of(TextColors.GREEN, "/se list",TextColors.RED, " - ", TextColors.GREEN, "List all configured plugins"));

            PaginationList.builder()
                    .title(Text.of(TextColors.GREEN, "Server Eco: help"))
                    .contents(contents)
                    .padding(Text.of(TextColors.GREEN, "-"))
                    .sendTo(src);
        }
        return CommandResult.success();
    }
}
