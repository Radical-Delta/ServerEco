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
        load();
        if (configNode.getNode("version").getInt() != 1) {
            populate();
            save();
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
        get().getNode("version").setValue(1).setComment("DO NOT TOUCH or your config will go poof");
        get().getNode("debug").setValue(false).setComment("If true, You will get information like the plugins ID displayed into the server console");
        get().getNode("plugin", "servereco", "account").setValue("Server").setComment("This is an example of how to configure this plugin, Just add more like this below to configure another plugin to use an account");
    }

    public CommentedConfigurationNode get() {
        return configNode;
    }

}
