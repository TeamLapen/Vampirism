package de.teamlapen.factions.common.config;

import de.teamlapen.factions.client.config.ClientConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.IConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FactionConfig {

    public static final Logger LOGGER = LogManager.getLogger();


    public static final ClientConfig CLIENT;
    public static final ServerConfig SERVER;
    public static final CommonConfig COMMON;

    private static final ModConfigSpec clientSpec;
    private static final ModConfigSpec serverSpec;
    private static final ModConfigSpec commonSpec;

    static {
        final Pair<ClientConfig, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(ClientConfig::new);
        clientSpec = specPair.getRight();
        CLIENT = specPair.getLeft();
    }

    static {
        final Pair<ServerConfig, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(ServerConfig::new);
        serverSpec = specPair.getRight();
        SERVER = specPair.getLeft();
    }

    static {
        final Pair<CommonConfig, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(CommonConfig::new);
        commonSpec = specPair.getRight();
        COMMON = specPair.getLeft();
    }

    public static void register(ModContainer modContainer) {
        modContainer.registerConfig(net.neoforged.fml.config.ModConfig.Type.CLIENT, clientSpec);
        modContainer.registerConfig(net.neoforged.fml.config.ModConfig.Type.SERVER, serverSpec);
        modContainer.registerConfig(net.neoforged.fml.config.ModConfig.Type.COMMON, commonSpec);
    }

    public static boolean isClientConfigSpec(IConfigSpec specs) {
        return specs == clientSpec;
    }

}
