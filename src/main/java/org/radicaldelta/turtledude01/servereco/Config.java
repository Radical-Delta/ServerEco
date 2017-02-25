package org.radicaldelta.turtledude01.servereco;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.Collections;
import java.util.Map;

@ConfigSerializable
public class Config {
    public static final TypeToken<Config> type = TypeToken.of(Config.class);
    @Setting
    public boolean debug = false;
    @Setting
    public Map<String, String> plugin = Collections.emptyMap();
}
