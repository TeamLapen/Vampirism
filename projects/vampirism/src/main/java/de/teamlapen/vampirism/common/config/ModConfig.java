package de.teamlapen.vampirism.common.config;


import de.teamlapen.faction.Services;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.ThreadSafeAPI;
import de.teamlapen.vampirism.client.config.ClientConfig;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.IConfigSpec;
import net.neoforged.fml.config.ModConfig.Type;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Function;

public class ModConfig extends Services {

    public static final Logger LOGGER = LogManager.getLogger();

    private final Config<ClientConfig> client;
    private final Config<CommonConfig> common;
    private final Config<ServerConfig> server;
    private final BalanceConfig balance;
    private final ConfigHelper helper = new ConfigHelper();

    private ModConfigSpec balanceSpec;
    private @Nullable BalanceBuilder balanceBuilder;

    public ModConfig(ModContainer container) {
        super(container);
        this.client = Config.create(ClientConfig::new);
        this.common = Config.create(CommonConfig::new);
        this.server = Config.create(ServerConfig::new);

        this.balanceBuilder = new BalanceBuilder();
        this.balance = new BalanceConfig(balanceBuilder);
    }

    @Override
    protected void registerModBus(IEventBus bus) {
        bus.addListener(this::setup);
        bus.addListener(this::onLoad);
        bus.addListener(this::onReload);
    }

    @ThreadSafeAPI
    public <T extends BalanceBuilder.Conf> void addBalanceModification(String key, Consumer<T> modifier) {
        if (this.balanceBuilder == null) {
            throw new IllegalStateException("Must add balance modifications during mod construction");
        }
        this.balanceBuilder.addBalanceModifier(key, modifier);
    }

    //<editor-fold desc="Static Accessors">

    public static ClientConfig client() {
        return VampirismMod.config().clientConfig();
    }

    public static CommonConfig common() {
        return VampirismMod.config().commonConfig();
    }

    public static ServerConfig server() {
        return VampirismMod.config().serverConfig();
    }

    public static BalanceConfig balance() {
        return VampirismMod.config().balanceConfig();
    }

    public static ConfigHelper helper() {
        return VampirismMod.config().configHelper();
    }

    //</editor-fold>

    //<editor-fold desc="Accessors">

    public ClientConfig clientConfig() {
        return client.config();
    }

    public CommonConfig commonConfig() {
        return common.config();
    }

    public ServerConfig serverConfig() {
        return server.config();
    }

    public BalanceConfig balanceConfig() {
        return balance;
    }

    public ConfigHelper configHelper() {
        return helper;
    }

    //</editor-fold>

    //<editor-fold desc="Event Handler">

    private void setup(NewRegistryEvent event) {
        buildBalanceConfig();
        container().registerConfig(Type.COMMON, common.spec());
        container().registerConfig(Type.CLIENT, client.spec());
        container().registerConfig(Type.SERVER, server.spec());
        container().registerConfig(Type.SERVER, balanceSpec, "vampirism-balance.toml");
    }

    public void onLoad(final ModConfigEvent.Loading configEvent) {
        if (configEvent.getConfig().getType() == Type.SERVER) {
            VampirismMod.services().sunDamageRegistry().reloadConfiguration();
        }
        if (configEvent.getConfig().getSpec() == balanceSpec) {
            helper.onBalanceConfigChanged(configEvent);
        }
        ColorConfigValue.reloadAll(configEvent.getConfig().getSpec());
    }

    public void onReload(final ModConfigEvent.Reloading configEvent) {
        if (configEvent.getConfig().getType() == Type.SERVER) {
            VampirismMod.services().sunDamageRegistry().reloadConfiguration();
        }
        if (configEvent.getConfig().getSpec() == balanceSpec) {
            helper.onBalanceConfigChanged(configEvent);
        }
        ColorConfigValue.reloadAll(configEvent.getConfig().getSpec());
    }

    //</editor-fold>

    public void buildBalanceConfig() {
        if (balanceBuilder == null) return;
        /*
        Build balance configuration
         */
        final Pair<BalanceConfig, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure((builder) -> {
            balanceBuilder.build(balance, builder);
            return balance;
        });
        balanceSpec = specPair.getRight();
        if (VampirismMod.inDev) {
            balanceBuilder.checkFields(balance);
        }
        balanceBuilder = null;
    }

    private record Config<T>(T config, ModConfigSpec spec) {

        public static <T> Config<T> create(Function<ModConfigSpec.Builder, T> consumer) {
            ColorConfigValue.beginCollection();
            var builder = new ModConfigSpec.Builder().configure(consumer);
            ModConfigSpec spec = builder.getRight();
            ColorConfigValue.endCollection(spec);
            return new Config<>(builder.getLeft(), spec);
        }

        public boolean isSpec(IConfigSpec spec) {
            return this.spec == spec;
        }
    }

}
