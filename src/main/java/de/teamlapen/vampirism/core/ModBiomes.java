package de.teamlapen.vampirism.core;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.world.biome.OverworldModifications;
import de.teamlapen.vampirism.world.biome.VampirismBiomes;
import de.teamlapen.vampirism.world.gen.modifier.ExtendedAddSpawnsBiomeModifier;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
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

    public static final DeferredRegister<Codec<? extends BiomeModifier>> BIOME_MODIFIER_SERIALIZERS = DeferredRegister.create(ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, REFERENCE.MODID);

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

    public static final ResourceKey<Biome> VAMPIRE_FOREST = ResourceKey.create(Registries.BIOME, new ResourceLocation(REFERENCE.MODID, "vampire_forest"));


    static void register(IEventBus bus) {
        BIOME_MODIFIER_SERIALIZERS.register(bus);
        VampirismAPI.sundamageRegistry().addNoSundamageBiomes(VAMPIRE_FOREST.location());
    }

    public static void createBiomes(BootstapContext<Biome> context) {
        HolderGetter<PlacedFeature> placedFeatures = context.lookup(Registries.PLACED_FEATURE);
        HolderGetter<ConfiguredWorldCarver<?>> configuredCarvers = context.lookup(Registries.CONFIGURED_CARVER);

        context.register(ModBiomes.VAMPIRE_FOREST, VampirismBiomes.createVampireForest(placedFeatures, configuredCarvers));
    }
}
