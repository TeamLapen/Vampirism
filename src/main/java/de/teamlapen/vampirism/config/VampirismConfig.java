package de.teamlapen.vampirism.config;


import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.ThreadSafeAPI;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.entity.SundamageRegistry;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.config.IConfigSpec;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

@EventBusSubscriber(modid = REFERENCE.MODID, bus = EventBusSubscriber.Bus.MOD)
public class VampirismConfig {

    public static final Logger LOGGER = LogManager.getLogger();

    public static final ClientConfig CLIENT;
    public static final ServerConfig SERVER;
    public static final CommonConfig COMMON;
    public static final @NotNull BalanceConfig BALANCE;
    public static final @NotNull ConfigHelper HELPER = new ConfigHelper();

    private static final ModConfigSpec clientSpec;
    private static final ModConfigSpec serverSpec;
    private static final ModConfigSpec commonSpec;
    private static ModConfigSpec balanceSpec;
    private static @Nullable BalanceBuilder balanceBuilder;

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

    static {
        balanceBuilder = new BalanceBuilder();
        BALANCE = new BalanceConfig(balanceBuilder);
    }

    public static boolean isClientConfigSpec(IConfigSpec<?> specs) {
        return specs == clientSpec;
    }

    @ThreadSafeAPI
    public static <T extends BalanceBuilder.Conf> void addBalanceModification(@NotNull String key, @NotNull Consumer<T> modifier) {
        if (balanceBuilder == null) {
            throw new IllegalStateException("Must add balance modifications during mod construction");
        }
        balanceBuilder.addBalanceModifier(key, modifier);
    }

    public static void buildBalanceConfig() {
        if (balanceBuilder == null) return;
        /*
        Build balance configuration
         */
        final Pair<BalanceConfig, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure((builder) -> {
            builder.comment("A ton of options which allow you to balance the mod to your desire");
            builder.push("balance");
            balanceBuilder.build(BALANCE, builder);
            builder.pop();
            return BALANCE;
        });
        balanceSpec = specPair.getRight();
        if (VampirismMod.inDev) {
            balanceBuilder.checkFields(BALANCE);
        }
        balanceBuilder = null;
    }

    public static void register(ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, commonSpec);
        modContainer.registerConfig(ModConfig.Type.CLIENT, clientSpec);
        modContainer.registerConfig(ModConfig.Type.SERVER, serverSpec);
        modContainer.registerConfig(ModConfig.Type.SERVER, balanceSpec, "vampirism-balance.toml");
    }

    @SubscribeEvent
    public static void onLoad(final ModConfigEvent.@NotNull Loading configEvent) {
        if (configEvent.getConfig().getType() == ModConfig.Type.SERVER) {
            ((SundamageRegistry) VampirismAPI.sundamageRegistry()).reloadConfiguration();
        } else if (configEvent.getConfig().getType() == ModConfig.Type.CLIENT) {
            if (CLIENT.guiLevelOffsetY.get() == 0) {
                CLIENT.guiLevelOffsetY.set(47); //Temporary workaround to reset incorrect values
            }
        }
        if (configEvent.getConfig().getSpec() == balanceSpec) {
            HELPER.onBalanceConfigChanged(configEvent);
        }
    }

    @SubscribeEvent
    public static void onReload(final ModConfigEvent.@NotNull Reloading configEvent) {
        if (configEvent.getConfig().getType() == ModConfig.Type.SERVER) {
            ((SundamageRegistry) VampirismAPI.sundamageRegistry()).reloadConfiguration();
        }
        if (configEvent.getConfig().getSpec() == balanceSpec) {
            HELPER.onBalanceConfigChanged(configEvent);
        }
    }

}
