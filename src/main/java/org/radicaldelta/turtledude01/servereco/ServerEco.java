package org.radicaldelta.turtledude01.servereco;

import com.google.inject.Inject;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.economy.EconomyTransactionEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.service.ChangeServiceProviderEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.service.economy.transaction.TransactionTypes;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.Optional;

@Plugin(id = "servereco", name = "Server Eco", version = "0.1.0")
public class ServerEco{

    private EconomyService economyService;

    @Inject
    private Logger logger;

    private static ServerEco serverEco;

    public Logger getLogger() {
        return logger;
    }

    @Inject
    @DefaultConfig(sharedRoot = false)
    private Path configDir;

    @Inject
    private ServerEco() {
        this.serverEco = this;
    }

    public static ServerEco getServerEco()
    {
        return serverEco;
    }

    @Listener
    public void onPreInitialization(GamePreInitializationEvent event) {
        Config.getConfig().setup();
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
        CommentedConfigurationNode config = Config.getConfig().get();

        Optional<PluginContainer> container = cause.all().stream().map(o -> {
            if (o instanceof PluginContainer)
                return Optional.of((PluginContainer) o);
            else
                return Sponge.getPluginManager().fromInstance(o);
        }).filter(Optional::isPresent).map(Optional::get).findFirst();

        if (!container.isPresent()) {
            if (config.getNode("debug").getBoolean()) {
                getLogger().warn("Warning! a plugin using the Economy is incompatible with this plugin!");
            }
            return;
        }

        String plugin = container.get().getId();


        if (config.getNode("debug").getBoolean()) {
            getLogger().info("Cause for transaction: " + plugin);
        }

        if (Config.getConfig().get().getNode("plugin", plugin).getString() != null) {
            if (config.getNode("plugin", plugin, "account") != null) {
                getLogger().info("Account" + config.getNode("plugin", plugin, "account").getString());
                amount = event.getTransactionResult().getAmount();
                String confAct = config.getNode("plugin", plugin, "account").getString();
                Optional<Account> act = economyService.getOrCreateAccount(confAct);
                if (event.getTransactionResult().getType() == TransactionTypes.DEPOSIT) {
                    result = act.get().withdraw(event.getTransactionResult().getCurrency(), amount, Cause.source(this).build());
                }
                else if (event.getTransactionResult().getType() == TransactionTypes.WITHDRAW) {
                    result = act.get().deposit(event.getTransactionResult().getCurrency(), amount, Cause.source(this).build());
                }
                else {
                    getLogger().error("A malformed transaction has occured!");
                    return;
                }

                if (result.getResult() == ResultType.SUCCESS) {
                    getLogger().info("Transaction of " + amount + " to/from " + confAct + " Success!");
                }
                else {
                    getLogger().warn("Transaction failed!");
                    cancelEco(event);
                }
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
            }
            else {
                getLogger().warn("Transaction failed and refund failed");
            }
        }
    }

    public Path getConfigDir() {
        return configDir;
    }
}

