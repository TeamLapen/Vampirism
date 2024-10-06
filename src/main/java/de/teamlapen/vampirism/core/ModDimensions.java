package de.teamlapen.vampirism.core;

import com.mojang.serialization.MapCodec;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.world.dimension.UnderworldBiomeSource;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.OptionalLong;

public class ModDimensions {

    public static final ResourceKey<DimensionType> UNDERWORLD = ResourceKey.create(Registries.DIMENSION_TYPE, VResourceLocation.mod("underworld"));
    public static final ResourceKey<LevelStem> UNDERWORLD_STEM = ResourceKey.create(Registries.LEVEL_STEM, VResourceLocation.mod("underworld"));
    public static final DeferredRegister<MapCodec<? extends BiomeSource>> BIOME_SOURCES = DeferredRegister.create(Registries.BIOME_SOURCE, REFERENCE.MODID);

    public static final DeferredHolder<MapCodec<? extends BiomeSource>, MapCodec<? extends BiomeSource>> UNDERWORLD_BIOME_SOURCE = BIOME_SOURCES.register("underworld", () -> UnderworldBiomeSource.CODEC);

    static void register(IEventBus bus) {
        BIOME_SOURCES.register(bus);
    }
    static void bootstrapTypes(BootstrapContext<DimensionType> context) {
        context.register(UNDERWORLD, new DimensionType(OptionalLong.of(6000), false, false, false, false, 1.0, false, false, 0, 256, 256, BlockTags.INFINIBURN_END, BuiltinDimensionTypes.END_EFFECTS, 0f, new DimensionType.MonsterSettings(false, false, UniformInt.of(0,7), 0)));
    }

    static void bootstrapLevels(BootstrapContext<LevelStem> context) {
        var dimensionTypes = context.lookup(Registries.DIMENSION_TYPE);
        var noiseSettings = context.lookup(Registries.NOISE_SETTINGS);
        var biomes = context.lookup(Registries.BIOME);
        context.register(UNDERWORLD_STEM, new LevelStem(dimensionTypes.getOrThrow(UNDERWORLD), new NoiseBasedChunkGenerator(new UnderworldBiomeSource(biomes), noiseSettings.getOrThrow(NoiseGeneratorSettings.END))));
    }

}
