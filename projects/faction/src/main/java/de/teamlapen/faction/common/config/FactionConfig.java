package de.teamlapen.faction.common.config;

import de.teamlapen.faction.FactionsMod;
import de.teamlapen.faction.Services;
import de.teamlapen.faction.client.config.ClientConfig;
import de.teamlapen.faction.client.config.preferences.UserPreferences;
import de.teamlapen.faction.client.config.values.ColorConfigValue;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.IConfigSpec;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.config.ModConfig.Type;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Function;

public class FactionConfig extends Services {

    public final Logger LOGGER = LogManager.getLogger();

    private final Config<ClientConfig> client;
    private final Config<ServerConfig> server;
    private final ConfigHelper helper;

    public FactionConfig(ModContainer container) {
        super(container);
        this.client = Config.create(ClientConfig::new);
        this.server = Config.create(ServerConfig::new);
        this.helper = new ConfigHelper(this);
    }

    //<editor-fold desc="Static Accessors">

    public static ClientConfig client() {
        return FactionsMod.config().client.config();
    }

    public static UserPreferences preferences() {
        return UserPreferences.get();
    }

    public static ServerConfig server() {
        return FactionsMod.config().server.config();
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

    public boolean isClientConfigSpec(IConfigSpec specs) {
        return this.client.isSpec(specs);
    }

    //</editor-fold>

    //<editor-fold desc="Event Handler">

    @Override
    protected void registerModBus(IEventBus bus) {
        bus.addListener(this::setup);
        bus.addListener(this::configLoaded);
        ColorConfigValue.subscribe(bus);
    }

    private void setup(NewRegistryEvent event) {
        container().registerConfig(Type.CLIENT, client.spec());
        container().registerConfig(Type.SERVER, server.spec());
    }

    private void configLoaded(ModConfigEvent event) {
        this.client.update(event.getConfig());
        this.server.update(event.getConfig());
    }

    //</editor-fold>

    //<editor-fold desc="Helper">

    public record Config<T extends IConfigs>(T config, ModConfigSpec spec) {

        public static <T extends IConfigs> Config<T> create(Function<ModConfigSpec.Builder, T> consumer) {
            var builder = ColorConfigValue.configure(consumer);
            return new Config<>(builder.getLeft(), builder.getRight());
        }

        public boolean isSpec(IConfigSpec spec) {
            return this.spec == spec;
        }

        public boolean isLoaded() {
            return this.spec.isLoaded();
        }

        public void update(ModConfig config) {
            if (isSpec(config.getSpec())) {
                this.config.refresh();
            }
        }
    }

    public interface IConfigs {

        default void refresh() {

        }
    }

    //</editor-fold>
}
