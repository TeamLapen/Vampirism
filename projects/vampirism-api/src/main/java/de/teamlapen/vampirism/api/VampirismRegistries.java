package de.teamlapen.vampirism.api;

import com.mojang.serialization.MapCodec;
import de.teamlapen.vampirism.api.world.entity.convertible.Converter;
import de.teamlapen.vampirism.api.world.entity.player.vampire.IVampireVision;
import de.teamlapen.vampirism.api.world.items.components.IVampireBook;
import de.teamlapen.vampirism.api.world.items.oil.IOil;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import java.util.function.Supplier;

import static de.teamlapen.vampirism.api.APIUtil.registryKey;
import static de.teamlapen.vampirism.api.APIUtil.supplyRegistry;

/**
 * Registry keys for all Vampirism registries and registry access for api usages
 */
@SuppressWarnings("unused")
public class VampirismRegistries {

    // for registry access in the api

    public static final Supplier<Registry<IOil>> OIL = supplyRegistry(Keys.OIL);

    public static final Supplier<Registry<MapCodec<? extends Converter>>> ENTITY_CONVERTER = supplyRegistry(Keys.ENTITY_CONVERTER);

    public static final Supplier<Registry<IVampireVision>> VAMPIRE_VISION = supplyRegistry(Keys.VAMPIRE_VISION);


    public static class Keys {

        // builtin registries

        public static final ResourceKey<Registry<IOil>> OIL = registryKey("oil");

        public static final ResourceKey<Registry<MapCodec<? extends Converter>>> ENTITY_CONVERTER = registryKey("converting_handler");
        public static final ResourceKey<Registry<IVampireVision>> VAMPIRE_VISION = registryKey("vampire_vision");


        // data pack registries

        public static final ResourceKey<Registry<IVampireBook>> VAMPIRE_BOOK = registryKey("vampire_book");
    }
}