package org.radicaldelta.turtledude01.servereco;

import com.google.inject.Inject;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.radicaldelta.turtledude01.servereco.command.*;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.economy.EconomyTransactionEvent;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.service.ChangeServiceProviderEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.service.economy.transaction.TransactionTypes;
import org.spongepowered.api.text.Text;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

@Plugin(id = "servereco", name = "Server Eco", version = "0.1.2")
public class ServerEco {

    static Config config = new Config();
    private static ServerEco serverEco;

    @Inject
    @DefaultConfig(sharedRoot = false)
    ConfigurationLoader<CommentedConfigurationNode> loader;

    @Inject
    @DefaultConfig(sharedRoot = false)
    Path path;

    private EconomyService economyService;

    @Inject
    private Logger logger;

    public static ServerEco getServerEco() {
        return serverEco;
    }

    public Logger getLogger() {
        return logger;
    }

    @Listener
    public void onPreInitialization(GamePreInitializationEvent event) {
        serverEco = this;
        configSetup();
    }

    @Listener
    public void onGameStartedServerEvent(GameStartedServerEvent event) {
        CommandSpec addPlugin = CommandSpec.builder()
                .description(Text.of("Add a plugin to ServerEco"))
                .permission("servereco.command.add")
                .arguments(
                        GenericArguments.onlyOne(GenericArguments.string(Text.of("plugin"))),
                        GenericArguments.onlyOne(GenericArguments.string(Text.of("account")))
                )
                .executor(new AddPlugin())
                .build();

        CommandSpec delPlugin = CommandSpec.builder()
                .description(Text.of("Add a plugin to ServerEco"))
                .permission("servereco.command.add")
                .arguments(
                        GenericArguments.onlyOne(GenericArguments.string(Text.of("plugin")))
                )
                .executor(new DelPlugin())
                .build();

        CommandSpec listPlugins = CommandSpec.builder()
                .description(Text.of("List all configured Plugin-> Account pairs"))
                .permission("servereco.command.list")
                .executor(new ListPlugins())
                .build();

        CommandSpec debugToggle = CommandSpec.builder()
                .description(Text.of("Toggle or set debug"))
                .permission("servereco.command.debug")
                .arguments(
                        GenericArguments.optional(GenericArguments.bool(Text.of("boolean")))
                )
                .executor(new DebugToggle())
                .build();

        CommandSpec helpCmd = CommandSpec.builder()
                .description(Text.of("Show help for ServerEco"))
                .permission("servereco.command.help")
                .executor(new HelpCmd())
                .build();

        CommandSpec serverEco = CommandSpec.builder()
                .description(Text.of("Base ServerEco command"))
                .permission("servereco.command.base")
                .child(addPlugin, "add")
                .child(delPlugin, "del")
                .child(listPlugins, "list")
                .child(debugToggle, "debug")
                .child(helpCmd, "help")
                .executor(new HelpCmd())
                .build();

        Sponge.getCommandManager().register(this, serverEco, "se", "servereco");
    }

    @Listener
    public void onReloadEvent(GameReloadEvent event) {
        configSetup();
    }

    @Listener
    public void onChangeServiceProvider(ChangeServiceProviderEvent event) {
        if (event.getService().equals(EconomyService.class)) {
            economyService = (EconomyService) event.getNewProviderRegistration().getProvider();
        }
    }

    @Listener
    public void onEconomyTransactionEvent(EconomyTransactionEvent event) {
        Cause cause = event.getCause();
        BigDecimal amount;
        TransactionResult result = null;
        Optional<PluginContainer> container = cause.all().stream().map(o -> {
            if (o instanceof PluginContainer)
                return Optional.of((PluginContainer) o);
            else
                return Sponge.getPluginManager().fromInstance(o);
        }).filter(Optional::isPresent).map(Optional::get).findFirst();
        if (!container.isPresent()) {
            if (config.debug) {
                getLogger().warn("Warning! a plugin using the Economy is incompatible with this plugin!");
            }
            return;
        }

        String pluginName = container.get().getId();


        if (config.debug) {
            getLogger().info("Cause for transaction: " + pluginName);
        }

        if (config.plugin.containsKey(pluginName)) {
            amount = event.getTransactionResult().getAmount();
            String confAct = config.plugin.get(pluginName);
            Optional<Account> act = economyService.getOrCreateAccount(confAct);
            if (event.getTransactionResult().getType() == TransactionTypes.DEPOSIT) {
                result = act.get().withdraw(event.getTransactionResult().getCurrency(), amount, Cause.source(this).build());
            } else if (event.getTransactionResult().getType() == TransactionTypes.WITHDRAW) {
                result = act.get().deposit(event.getTransactionResult().getCurrency(), amount, Cause.source(this).build());
            } else {
                getLogger().error("A malformed transaction has occured!");
                return;
            }

            if (result.getResult() == ResultType.SUCCESS) {
                getLogger().info("Transaction of " + amount + " to/from " + confAct + " Success!");
            } else {
                getLogger().warn("Transaction failed!");
                cancelEco(event);
            }
        }
    }

    public void cancelEco(EconomyTransactionEvent event) {
        TransactionResult result = null;
        Account act = event.getTransactionResult().getAccount();
        BigDecimal amount = event.getTransactionResult().getAmount();
        if (event.getTransactionResult().getResult() == ResultType.SUCCESS) {
            if (event.getTransactionResult() == TransactionTypes.DEPOSIT) {
                result = act.withdraw(event.getTransactionResult().getCurrency(), amount, Cause.source(this).build());
            }
            if (event.getTransactionResult() == TransactionTypes.WITHDRAW) {
                result = act.deposit(event.getTransactionResult().getCurrency(), amount, Cause.source(this).build());
            }

            if (result.getResult() == ResultType.SUCCESS) {
                //dont really need anything here
            } else {
                getLogger().warn("Transaction failed and refund failed");
            }
        }
    }

    public void configSetup() {
        if (!Files.exists(path)) {
            try {
                Sponge.getGame().getAssetManager().getAsset(this, "default.conf").get().copyToFile(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            config = loader.load().getValue(Config.type);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ObjectMappingException e) {
            e.printStackTrace();
        }
    }

    public ConfigurationLoader<CommentedConfigurationNode> getConfigLoader() {
        return loader;
    }

    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        try {
            loader.load().setValue(config.type, config);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ObjectMappingException e) {
            e.printStackTrace();
        }
    }

    public void saveConfig() {
        try {
            loader.save(loader.createEmptyNode().setValue(Config.type, config));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ObjectMappingException e) {
            e.printStackTrace();
        }
    }
}
