package de.teamlapen.faction.common.config;

import de.teamlapen.faction.FactionsMod;
import de.teamlapen.faction.Services;
import de.teamlapen.faction.client.config.ClientConfig;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.IConfigSpec;
import net.neoforged.fml.config.ModConfig.Type;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Function;

public class FactionConfig extends Services {

    public final Logger LOGGER = LogManager.getLogger();

    private final Config<ClientConfig> client;
    private final Config<ServerConfig> server;
    private final Config<CommonConfig> common;
    private final ConfigHelper helper;

    public FactionConfig(ModContainer container) {
        super(container);
        this.client = Config.create(ClientConfig::new);
        this.server = Config.create(ServerConfig::new);
        this.common = Config.create(CommonConfig::new);
        this.helper = new ConfigHelper(this);
    }

    @Override
    protected void registerModBus(IEventBus bus) {
        bus.addListener(this::setup);
    }

    //<editor-fold desc="Static Accessors">

    public static ClientConfig client() {
        return FactionsMod.config().client.config();
    }

    public static ServerConfig server() {
        return FactionsMod.config().server.config();
    }

    public static CommonConfig common() {
        return FactionsMod.config().common.config();
    }

    public static ConfigHelper helper() {
        return FactionsMod.config().helper;
    }

    //</editor-fold>

    //<editor-fold desc="Accessors">

    public Config<ClientConfig> clientConfig() {
        return this.client;
    }

    public Config<ServerConfig> serverConfig() {
        return this.server;
    }

    public Config<CommonConfig> commonConfig() {
        return this.common;
    }

    public boolean isClientConfigSpec(IConfigSpec specs) {
        return this.client.isSpec(specs);
    }

    //</editor-fold>

    //<editor-fold desc="Event Handler">

    public void setup(NewRegistryEvent event) {
        container().registerConfig(Type.CLIENT, client.spec());
        container().registerConfig(Type.SERVER, server.spec());
        container().registerConfig(Type.COMMON, common.spec());
    }

    //</editor-fold>

    public record Config<T>(T config, ModConfigSpec spec) {

        public static <T> Config<T> create(Function<ModConfigSpec.Builder, T> consumer) {
            var builder = new ModConfigSpec.Builder().configure(consumer);
            return new Config<>(builder.getLeft(), builder.getRight());
        }

        public boolean isSpec(IConfigSpec spec) {
            return this.spec == spec;
        }

        public boolean isLoaded() {
            return this.spec.isLoaded();
        }
    }
}
