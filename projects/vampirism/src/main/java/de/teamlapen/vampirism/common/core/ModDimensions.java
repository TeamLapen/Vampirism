package de.teamlapen.vampirism.common.core;

import com.mojang.serialization.MapCodec;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.common.tags.ModTimelineTags;
import de.teamlapen.vampirism.common.world.dimensions.velmorra.VelmorraBiomeSource;
import de.teamlapen.vampirism.common.world.dimensions.velmorra.VelmorraDensityFunction;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ARGB;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.attribute.*;
import net.minecraft.world.level.CardinalLighting;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.synth.BlendedNoise;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import net.minecraft.world.timeline.Timeline;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.Optional;

public class ModDimensions {

    public static final Identifier VELMORRA = VIdentifier.mod("velmorra");
    public static final DeferredRegister<MapCodec<? extends BiomeSource>> BIOME_SOURCES = DeferredRegister.create(Registries.BIOME_SOURCE, REFERENCE.MODID);
    public static final DeferredRegister<MapCodec<? extends DensityFunction>> DENSITY_FUNCTIONS = DeferredRegister.create(Registries.DENSITY_FUNCTION_TYPE, REFERENCE.MODID);

    @SuppressWarnings("unused")
    public static final DeferredHolder<MapCodec<? extends BiomeSource>, MapCodec<? extends BiomeSource>> VELMORRA_BIOME_SOURCE_CODEC = BIOME_SOURCES.register("velmorra", () -> VelmorraBiomeSource.CODEC);
    @SuppressWarnings("unused")
    public static final DeferredHolder<MapCodec<? extends DensityFunction>, MapCodec<? extends DensityFunction>> VELMORRA_DENSITY_FUNCTION_CODEC = DENSITY_FUNCTIONS.register("velmorra", VelmorraDensityFunction.CODEC::codec);

    public static final ResourceKey<DimensionType> VELMORRA_DIMENSION_TYPE = ResourceKey.create(Registries.DIMENSION_TYPE, VELMORRA);
    public static final ResourceKey<LevelStem> VELMORRA_LEVEL_STEM = ResourceKey.create(Registries.LEVEL_STEM, VELMORRA);
    public static final ResourceKey<Level> VELMORRA_LEVEL = ResourceKey.create(Registries.DIMENSION, VELMORRA_LEVEL_STEM.identifier());
    public static final ResourceKey<NoiseGeneratorSettings> VELMORRA_NOISE_GENERATOR = ResourceKey.create(Registries.NOISE_SETTINGS, VELMORRA);
    public static final ResourceKey<DensityFunction> VELMORRA_DENSITY_FUNCTION =  ResourceKey.create(Registries.DENSITY_FUNCTION, VELMORRA);
    public static final ResourceKey<DensityFunction> BASE_3D_NOISE_VELMORRA = ResourceKey.create(Registries.DENSITY_FUNCTION, VIdentifier.mod("base_3d_noise"));


    static void register(IEventBus bus) {
        BIOME_SOURCES.register(bus);
        DENSITY_FUNCTIONS.register(bus);
    }

    static void bootstrapDimensionTypes(BootstrapContext<DimensionType> context) {
        HolderGetter<Timeline> lookup = context.lookup(Registries.TIMELINE);
        context.register(VELMORRA_DIMENSION_TYPE, new DimensionType(
                        true,
                        true,
                        false,
                        false,
                        1.0,
                        0,
                        256,
                        256,
                        BlockTags.INFINIBURN_END,
                        0f,
                new DimensionType.MonsterSettings(UniformInt.of(0, 7), 0),
                DimensionType.Skybox.NONE,
                CardinalLighting.Type.DEFAULT,
                EnvironmentAttributeMap.builder()
                        .set(EnvironmentAttributes.FOG_COLOR, 0x171717)
                        .set(EnvironmentAttributes.SKY_LIGHT_COLOR, ARGB.colorFromFloat(1.0F, 1F, 0.8F, 0.8F))
                        .set(EnvironmentAttributes.WATER_FOG_COLOR, 0x670717)
                        .set(EnvironmentAttributes.SKY_COLOR, 0x131313)
                        .set(EnvironmentAttributes.SKY_LIGHT_LEVEL, 3f)
                        .set(EnvironmentAttributes.FOG_START_DISTANCE, 10.0F)
                        .set(EnvironmentAttributes.FOG_END_DISTANCE, 96.0F)
                        .set(EnvironmentAttributes.BACKGROUND_MUSIC, BackgroundMusic.OVERWORLD)
                        .set(EnvironmentAttributes.AMBIENT_SOUNDS, new AmbientSounds(Optional.empty(), Optional.of(new AmbientMoodSettings(SoundEvents.AMBIENT_CRIMSON_FOREST_MOOD, 6000, 8, 2.0D)), List.of(new AmbientAdditionsSettings(SoundEvents.AMBIENT_CRIMSON_FOREST_ADDITIONS, 0.0111D))))
                        .set(EnvironmentAttributes.BED_RULE, BedRule.EXPLODES)
                        .set(EnvironmentAttributes.CAN_START_RAID, false)
                        .set(EnvironmentAttributes.RESPAWN_ANCHOR_WORKS, false)
                        .set(ModEnvironmentAttributes.SUN_DAMAGE.get(), false)
                        .build(),
                lookup.getOrThrow(ModTimelineTags.IN_VELMORRA),
                Optional.empty()
                )
        );
    }

    static void bootstrapNoise(BootstrapContext<NoiseGeneratorSettings> context) {
        context.register(VELMORRA_NOISE_GENERATOR, new NoiseGeneratorSettings(new NoiseSettings(0,256,1,2), ModBlocks.DARK_STONE.get().defaultBlockState(), Blocks.AIR.defaultBlockState(), rute(context.lookup(Registries.DENSITY_FUNCTION), context.lookup(Registries.NOISE)), rule(), List.of(), 0, false, false, false, true));
    }

    static void bootstrapDensityFunctions(BootstrapContext<DensityFunction> context) {
        context.register(BASE_3D_NOISE_VELMORRA, BlendedNoise.createUnseeded(0.25, 0.25, 80.0, 160.0, 4.0));
        context.register(VELMORRA_DENSITY_FUNCTION, DensityFunctions.add(new VelmorraDensityFunction(0), NoiseRouterData.getFunction(context.lookup(Registries.DENSITY_FUNCTION), BASE_3D_NOISE_VELMORRA)));
    }

    private static SurfaceRules.RuleSource rule() {
        SurfaceRules.RuleSource cursed_earth = new SurfaceRules.BlockRuleSource(ModBlocks.CURSED_EARTH.get().defaultBlockState());
        SurfaceRules.RuleSource grass = new SurfaceRules.BlockRuleSource(ModBlocks.CURSED_GRASS.get().defaultBlockState());
        SurfaceRules.ConditionSource inVampireBiome = SurfaceRules.isBiome(ModBiomes.VELMORRA);
        SurfaceRules.RuleSource vampireForestTopLayer = SurfaceRules.ifTrue(inVampireBiome, grass);
        SurfaceRules.RuleSource vampireForestBaseLayer = SurfaceRules.ifTrue(inVampireBiome, cursed_earth);
        return SurfaceRules.sequence(
                SurfaceRules.sequence(
                        SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, SurfaceRules.ifTrue(SurfaceRules.waterBlockCheck(-1, 0), SurfaceRules.sequence(vampireForestTopLayer))),
                        SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, SurfaceRules.ifTrue(SurfaceRules.waterBlockCheck(-1, 0), SurfaceRules.sequence(vampireForestBaseLayer)))
                )
        );
    }

    private static NoiseRouter rute(HolderGetter<DensityFunction> density, HolderGetter<NormalNoise.NoiseParameters> noise) {
        DensityFunction erosion = DensityFunctions.cache2d(new VelmorraDensityFunction(0));
        DensityFunction finalDensity = NoiseRouterData.postProcess(NoiseRouterData.slideEndLike(NoiseRouterData.getFunction(density, VELMORRA_DENSITY_FUNCTION), 0, 256));

        DensityFunction vegetationXShift = NoiseRouterData.getFunction(density, NoiseRouterData.SHIFT_X);
        DensityFunction vegerationZShift = NoiseRouterData.getFunction(density, NoiseRouterData.SHIFT_Z);
        DensityFunction vegetation = DensityFunctions.shiftedNoise2d(vegetationXShift, vegerationZShift, 0.25, noise.getOrThrow(Noises.VEGETATION));
        return new NoiseRouter(
                DensityFunctions.zero(),
                DensityFunctions.zero(),
                DensityFunctions.zero(),
                DensityFunctions.zero(),
                DensityFunctions.zero(),
                vegetation,
                DensityFunctions.zero(),
                erosion,
                DensityFunctions.zero(),
                NoiseRouterData.getFunction(density, NoiseRouterData.RIDGES),
                NoiseRouterData.slideEnd(DensityFunctions.add(erosion, DensityFunctions.constant(-0.703125))),
                finalDensity,
                DensityFunctions.zero(),
                DensityFunctions.zero(),
                DensityFunctions.zero()
        );
    }

}
