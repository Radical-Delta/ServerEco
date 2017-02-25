package org.radicaldelta.turtledude01.servereco;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Config {

    private static Config config = new Config();

    private Config() {
    }

    public static Config getConfig() {
        return config;
    }

    private Path configFile = ServerEco.getServerEco().getConfigDir();
    private ConfigurationLoader<CommentedConfigurationNode> configLoader = HoconConfigurationLoader.builder().setPath(configFile).build();
    private CommentedConfigurationNode configNode;

    public void setup() {
        if (!Files.exists(configFile)) {
            try {
                Files.createFile(configFile);
                load();
                populate();
                save();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            load();
        }
    }

    public void load() {
        try {
            configNode = configLoader.load();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            configLoader.save(configNode);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void populate() {
        get().getNode("debug").setValue(false);
        get().getNode("plugin", "servereco", "account").setValue("Server");
    }

    public CommentedConfigurationNode get() {
        return configNode;
    }

}
