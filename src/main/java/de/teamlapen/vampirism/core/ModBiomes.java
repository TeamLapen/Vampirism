package de.teamlapen.vampirism.core;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.world.biome.OverworldModifications;
import de.teamlapen.vampirism.world.biome.VampirismBiomes;
import de.teamlapen.vampirism.world.gen.modifier.ExtendedAddSpawnsBiomeModifier;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.function.Function;

/**
 * For new dynamic registry related things see {@link VampirismBiomes} and {@link OverworldModifications}
 */
public class ModBiomes {
    public static final DeferredRegister<Biome> BIOMES = DeferredRegister.create(ForgeRegistries.BIOMES, REFERENCE.MODID);
    public static final DeferredRegister<Codec<? extends BiomeModifier>> BIOME_MODIFIER_SERIALIZERS = DeferredRegister.create(ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, REFERENCE.MODID);

    public static final RegistryObject<Biome> VAMPIRE_FOREST = BIOMES.register("vampire_forest", VampirismBiomes::createVampireForest);

    public static final RegistryObject<Codec<ExtendedAddSpawnsBiomeModifier>> ADD_SPAWNS_BIOME_MODIFIER_TYPE = BIOME_MODIFIER_SERIALIZERS.register("extended_add_spawns", () ->
            RecordCodecBuilder.create(builder -> builder.group(
                    Biome.LIST_CODEC.fieldOf("biomes").forGetter(ExtendedAddSpawnsBiomeModifier::biomes),
                    Biome.LIST_CODEC.fieldOf("excludedBiomes").forGetter(ExtendedAddSpawnsBiomeModifier::excludedBiomes),
                    new ExtraCodecs.EitherCodec<>(ExtendedAddSpawnsBiomeModifier.ExtendedSpawnData.CODEC.listOf(), ExtendedAddSpawnsBiomeModifier.ExtendedSpawnData.CODEC).xmap(
                            either -> either.map(Function.identity(), List::of), // convert list/singleton to list when decoding
                            list -> list.size() == 1 ? Either.right(list.get(0)) : Either.left(list) // convert list to singleton/list when encoding
                    ).fieldOf("spawners").forGetter(ExtendedAddSpawnsBiomeModifier::spawners)
            ).apply(builder, ExtendedAddSpawnsBiomeModifier::new))
    );

    static void registerBiomes(IEventBus bus) {
        BIOMES.register(bus);
        BIOME_MODIFIER_SERIALIZERS.register(bus);
        VampirismAPI.sundamageRegistry().addNoSundamageBiomes(VAMPIRE_FOREST.getKey().location());
    }

}
