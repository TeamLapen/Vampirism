package de.teamlapen.vampirism.world.biome;

import com.mojang.datafixers.util.Pair;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModBiomes;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.mixin.accessor.MultiNoiseBiomeSourceParameterListPresetAccessor;
import de.teamlapen.vampirism.modcompat.terrablender.TerraBlenderCompat;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList;
import net.minecraft.world.level.levelgen.SurfaceRules;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

/**
 * Overworld is modified in the following ways:
 * 1) New surface rules. These are added via a Mixin hook ({@link de.teamlapen.vampirism.mixin.NoiseGeneratorSettingsMixin} on static init. And later on via TerraBlender, if installed
 * 2) The overworld BiomeSource preset is modified on load complete, if TerraBlender is not installed, to include the vampirism forest. If TerraBlender is installed this is done via TerraBlender in common setup in {@link de.teamlapen.vampirism.modcompat.terrablender.TerraBlenderRegistration}
 */
public class OverworldModifications {

    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * @param a The "container"
     * @param b The point to check
     * @return Whether all parameters of point are completely contained inside outer
     */
    private static boolean intersects(Climate.@NotNull ParameterPoint a, Climate.@NotNull ParameterPoint b) {
        return intersects(a.temperature(), b.temperature()) && intersects(a.humidity(), b.humidity()) && intersects(a.continentalness(), b.continentalness()) && intersects(a.erosion(), b.erosion()) && intersects(a.depth(), b.depth()) && intersects(a.weirdness(), b.weirdness());
    }

    /**
     * @return Whether point is completely contained inside outer
     */
    private static boolean intersects(Climate.@NotNull Parameter a, Climate.@NotNull Parameter b) {
        return (a.max() > b.min() && a.min() < b.max()) || (a.max() == a.min() && b.max() == b.min() && a.max() == b.max());
    }

    /**
     * Call on main thread.
     * <p>
     * Add our biomes to the overworld biome source preset
     */
    public static void addBiomesToOverworldUnsafe() {
        if (TerraBlenderCompat.areBiomesAddedViaTerraBlender()) { //If we are already adding the biome to the overworld using TerraBlender, we shouldn't hack it into the overworld preset
            LOGGER.info("Vampirism Biomes are added via TerraBlender. Not adding them to overworld preset.");
            return;
        }
        if (!VampirismConfig.COMMON.addVampireForestToOverworld.get()) {
            return;
        }
        /*
         * Hack the vampire forest into the Overworld biome list preset, replacing some taiga biome areas.
         *
         * Create a wrapper function for the parameterSource function, which calls the original one and then modifies the result
         */
        final MultiNoiseBiomeSourceParameterList.Preset.SourceProvider originalParameterSourceFunction = ((MultiNoiseBiomeSourceParameterListPresetAccessor) (Object) MultiNoiseBiomeSourceParameterList.Preset.OVERWORLD).getProvider();

        MultiNoiseBiomeSourceParameterList.Preset.SourceProvider wrapperParameterSourceFunction = new MultiNoiseBiomeSourceParameterList.Preset.SourceProvider() {
            @Override
            public <T> Climate.ParameterList<T> apply(Function<ResourceKey<Biome>, T> p_275485_) {
                //Create copy of vanilla list
                Climate.ParameterList<Holder<Biome>> vanillaList = originalParameterSourceFunction.apply((Function<ResourceKey<Biome>, Holder<Biome>>) p_275485_);
                List<Pair<Climate.ParameterPoint, Holder<Biome>>> biomes = new ArrayList<>(vanillaList.values());

                //Setup parameter point (basically the volume in the n-d parameter space) at which the biome should be generated
                //Order of parameters: Temp , humidity, continentalness, erosion, depth, weirdness
                Climate.ParameterPoint[] forestPoints = new Climate.ParameterPoint[]{
                        Climate.parameters(Climate.Parameter.span(-0.40F, -0.19F), Climate.Parameter.span(0.1F, 0.3F), Climate.Parameter.span(-0.11F, 0.55F), Climate.Parameter.span(-0.375F, -0.2225F), Climate.Parameter.point(0), Climate.Parameter.span(-0.56666666F, -0.05F), 0),
//                    Climate.parameters(Climate.Parameter.span(-0.40F, -0.19F), Climate.Parameter.span(0.1F, 0.3F), Climate.Parameter.span(-0.11F, 0.55F), Climate.Parameter.span(-0.375F, -0.2225F), Climate.Parameter.point(1), Climate.Parameter.span(-0.56666666F, -0.05F), 0),
                        Climate.parameters(Climate.Parameter.span(-0.40F, -0.19F), Climate.Parameter.span(0.1F, 0.3F), Climate.Parameter.span(-0.11F, 0.55F), Climate.Parameter.span(-0.375F, -0.2225F), Climate.Parameter.point(0), Climate.Parameter.span(0.05f, 0.4F), 0),
//                    Climate.parameters(Climate.Parameter.span(-0.40F, -0.19F), Climate.Parameter.span(0.1F, 0.3F), Climate.Parameter.span(-0.11F, 0.55F), Climate.Parameter.span(-0.375F, -0.2225F), Climate.Parameter.point(1), Climate.Parameter.span(0.05f, 0.4F), 0)
                };


                //Remove vanilla biomes that are completely inside the given range
                int oldCount = biomes.size();
                int removed = 0;
                Iterator<Pair<Climate.ParameterPoint, Holder<Biome>>> it = biomes.iterator();
                while (it.hasNext()) {
                    Pair<Climate.ParameterPoint, Holder<Biome>> pair = it.next();
                    //It should be safe to get the biome here because {@link BiomeSource} does so as well right after this function call
                    removed += pair.getSecond().unwrapKey().map(biomeId -> {
                        if ("minecraft".equals(biomeId.location().getNamespace()) && Arrays.stream(forestPoints).anyMatch(p -> intersects(p, pair.getFirst()))) {
                            it.remove();
                            LOGGER.debug("Removing biome {} from parameter point {} in overworld preset", biomeId, pair.getFirst());
                            return 1;
                        }
                        return 0;
                    }).orElse(0);
                }
                LOGGER.debug("Removed a total of {} points from {}", removed, oldCount);


                LOGGER.info("Adding biome {} to ParameterPoints {} in Preset.OVERWORLD", ModBiomes.VAMPIRE_FOREST.location(), Arrays.toString(forestPoints));
                for (Climate.ParameterPoint forestPoint : forestPoints) {
                    biomes.add(Pair.of(forestPoint, (Holder<Biome>) p_275485_.apply(ModBiomes.VAMPIRE_FOREST)));
                }

                return (Climate.ParameterList<T>) new Climate.ParameterList<>(biomes);
            }
        };

        ((MultiNoiseBiomeSourceParameterListPresetAccessor) (Object) MultiNoiseBiomeSourceParameterList.Preset.OVERWORLD).setProvider(wrapperParameterSourceFunction);
    }

    public static SurfaceRules.@NotNull RuleSource buildOverworldSurfaceRules() {
        //Any blocks here must be available before block registration, so they must be initialized statically
        SurfaceRules.RuleSource cursed_earth = new SurfaceRules.BlockRuleSource(ModBlocks.CURSED_EARTH.get().defaultBlockState());
        SurfaceRules.RuleSource grass = new SurfaceRules.BlockRuleSource(ModBlocks.CURSED_GRASS.get().defaultBlockState());
        SurfaceRules.ConditionSource inVampireBiome = SurfaceRules.isBiome(ModBiomes.VAMPIRE_FOREST);
        SurfaceRules.RuleSource vampireForestTopLayer = SurfaceRules.ifTrue(inVampireBiome, grass);
        SurfaceRules.RuleSource vampireForestBaseLayer = SurfaceRules.ifTrue(inVampireBiome, cursed_earth);
        return SurfaceRules.sequence(
                SurfaceRules.ifTrue(SurfaceRules.abovePreliminarySurface(),
                        SurfaceRules.sequence(
                                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, SurfaceRules.ifTrue(SurfaceRules.waterBlockCheck(-1, 0), SurfaceRules.sequence(vampireForestTopLayer))),
                                SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, SurfaceRules.ifTrue(SurfaceRules.waterBlockCheck(-1, 0), SurfaceRules.sequence(vampireForestBaseLayer)))
                        ))
        );
    }
}
