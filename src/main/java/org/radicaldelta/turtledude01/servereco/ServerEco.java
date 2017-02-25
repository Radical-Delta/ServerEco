package org.radicaldelta.turtledude01.servereco;

import com.google.inject.Inject;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import org.slf4j.Logger;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.economy.EconomyTransactionEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.service.ChangeServiceProviderEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.service.economy.transaction.TransactionTypes;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    public void onTransaction(EconomyTransactionEvent event) {
        String cause = event.getCause().toString();
        BigDecimal amount;
        TransactionResult result = null;
        CommentedConfigurationNode config = Config.getConfig().get();

        if (Config.getConfig().get().getNode("plugin", cause).getString() != null) {
            getLogger().info("Plugin: " + cause);
            if (config.getNode("plugin", cause, "account") != null) {
                getLogger().info("Account" + config.getNode("plugin", cause, "account").getString());
                amount = event.getTransactionResult().getAmount();
                String confAct = config.getNode("plugin", cause, "account").getString();
                Optional<Account> act = economyService.getOrCreateAccount(confAct);
                if (event.getTransactionResult() == TransactionTypes.DEPOSIT) {
                    getLogger().debug("Deposit");
                    result = act.get().withdraw(event.getTransactionResult().getCurrency(), amount, Cause.source(this).build());
                }
                if (event.getTransactionResult() == TransactionTypes.WITHDRAW) {
                    getLogger().debug("withdraw");
                    result = act.get().deposit(event.getTransactionResult().getCurrency(), amount, Cause.source(this).build());
                }

                if (result.getResult() == ResultType.ACCOUNT_NO_SPACE) {
                    getLogger().warn("Account " + act + " has no room for the funds!");
                    cancelEco(event);

                }
                else if (result.getResult() == ResultType.ACCOUNT_NO_FUNDS) {
                    getLogger().warn("Account " + act + " has no funds!");
                    cancelEco(event);
                }
                else if (result.getResult() == ResultType.CONTEXT_MISMATCH) {
                    getLogger().warn("Context mismatch"); //not sure what this means tbh
                    cancelEco(event);
                }
                else if (result.getResult() == ResultType.FAILED) {
                    getLogger().warn("Transaction failed with " + act + " on plugin " + cause);
                    cancelEco(event);

                }
                else if (result.getResult() == ResultType.SUCCESS) {
                    getLogger().info("Transaction of " + amount + " to/from " + confAct + " Success!");
                }
            }
        }
        else if (config.getNode("debug").getBoolean()) {
            getLogger().info("Cause for transaction: " + cause);
        }
    }
    public void cancelEco(EconomyTransactionEvent event) {
        TransactionResult result = null;
        Account act = event.getTransactionResult().getAccount();
        BigDecimal amount = event.getTransactionResult().getAmount();
        if (event.getTransactionResult().getResult() == ResultType.SUCCESS) {
            if (event.getTransactionResult() == TransactionTypes.DEPOSIT) {
                getLogger().debug("Deposit");
                result = act.withdraw(event.getTransactionResult().getCurrency(), amount, Cause.source(this).build());
            }
            if (event.getTransactionResult() == TransactionTypes.WITHDRAW) {
                getLogger().debug("withdraw");
                result = act.deposit(event.getTransactionResult().getCurrency(), amount, Cause.source(this).build());
            }

            if (result.getResult() == ResultType.ACCOUNT_NO_SPACE) {
                getLogger().warn("Account " + act + " has no room for the funds!");
            }
            else if (result.getResult() == ResultType.ACCOUNT_NO_FUNDS) {
                getLogger().warn("Account " + act + " has no funds!");
            }
            else if (result.getResult() == ResultType.CONTEXT_MISMATCH) {
                getLogger().warn("Context mismatch"); //not sure what this means tbh
            }
            else if (result.getResult() == ResultType.FAILED) {
                getLogger().warn("Transaction failed and refund failed");
            }
            else if (result.getResult() == ResultType.SUCCESS) {
                //dont really need anything here
            }
        }
    }

    public Path getConfigDir() {
        return configDir;
    }
}

