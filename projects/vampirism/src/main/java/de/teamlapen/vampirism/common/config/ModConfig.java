package de.teamlapen.vampirism.common.config;


import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.faction.Services;
import de.teamlapen.faction.client.config.values.ColorConfigValue;
import de.teamlapen.faction.common.util.ConfigValueCodec;
import de.teamlapen.faction.misc.extensions.IConfigValue;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.ThreadSafeAPI;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.client.config.ClientConfig;
import net.minecraft.resources.Identifier;
import net.minecraft.util.StringRepresentable;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.IConfigSpec;
import net.neoforged.fml.config.ModConfig.Type;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.util.NeoForgeExtraCodecs;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

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
        this.client = Config.create(VIdentifier.mod("client"), ClientConfig::new);
        this.common = Config.create(VIdentifier.mod("common"), CommonConfig::new);
        this.server = Config.create(VIdentifier.mod("server"), ServerConfig::new);

        this.balanceBuilder = new BalanceBuilder();
        this.balance = new BalanceConfig(balanceBuilder);
    }

    @Override
    protected void registerModBus(IEventBus bus) {
        bus.addListener(this::setup);
        bus.addListener(this::onLoad);
        bus.addListener(this::onReload);
        ColorConfigValue.subscribe(bus);
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
    }

    public void onReload(final ModConfigEvent.Reloading configEvent) {
        if (configEvent.getConfig().getType() == Type.SERVER) {
            VampirismMod.services().sunDamageRegistry().reloadConfiguration();
        }
        if (configEvent.getConfig().getSpec() == balanceSpec) {
            helper.onBalanceConfigChanged(configEvent);
        }
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
        ConfigValueCodec.register(VIdentifier.mod("balance"), balanceSpec);
        if (VampirismMod.inDev) {
            balanceBuilder.checkFields(balance);
        }
        balanceBuilder = null;
    }

    private record Config<T>(T config, ModConfigSpec spec) {

        public static <T> Config<T> create(Identifier id, Function<ModConfigSpec.Builder, T> consumer) {
            var builder = ColorConfigValue.configure(consumer);
            ConfigValueCodec.register(id, builder.getRight());
            return new Config<>(builder.getLeft(), builder.getRight());
        }

        public boolean isSpec(IConfigSpec spec) {
            return this.spec == spec;
        }
    }

}
