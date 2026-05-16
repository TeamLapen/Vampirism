package de.teamlapen.vampirism.common.integration.terrablender;

import com.mojang.datafixers.util.Pair;
import de.teamlapen.integration.Integration;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.core.ModBiomes;
import de.teamlapen.vampirism.common.integration.ITerraBlenderBiomeProvider;
import de.teamlapen.vampirism.common.world.biomes.OverworldModifications;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import terrablender.api.Region;
import terrablender.api.RegionType;
import terrablender.api.Regions;
import terrablender.api.SurfaceRuleManager;
import terrablender.worldgen.RegionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * When TerraBlender is installed we use it to add our biomes to the overworld, instead of hacking it into the overworld preset. This is more compatible with other mods.
 * <br>
 * #registerBiomeProviderIfPresentUnsafe() is called during common setup.
 * The hack code in VampirismWorldGen is called during LoadComplete and can therefore check #arreBiomesAddedViaTerraBlender
 */
@Integration(modId = "terrablender")
public class TerraBlenderIntegration {

    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public static void registerBiomeProviderIfPresentUnsafe(FMLCommonSetupEvent event) {
        if (!ModConfig.common().addVampireForestToOverworld.get()) {
            return;
        }

        registerRegions();
        registerSurfaceRules();
        LOGGER.info("TerraBlender is installed. Using it to add vampire Forest to overworld.");
        VampirismMod.integrations().useTerraBlender(new Provider(true));
    }

    private record Provider(boolean isUsingTerraBlender) implements ITerraBlenderBiomeProvider {
    }

    public static void registerRegions() {
        Regions.register(new ForestRegion());
    }

    public static void registerSurfaceRules() {
        SurfaceRuleManager.addSurfaceRules(SurfaceRuleManager.RuleCategory.OVERWORLD, REFERENCE.MODID, OverworldModifications.buildOverworldSurfaceRules());
    }

    static class ForestRegion extends Region {

        public ForestRegion() {
            super(VIdentifier.mod("overworld"), RegionType.OVERWORLD, ModConfig.common().vampireForestWeightTerrablender.get());
        }

        @Override
        public void addBiomes(Registry<Biome> registry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper) {
            this.addModifiedVanillaOverworldBiomes(mapper, builder -> {
                List<Climate.ParameterPoint> points = new ArrayList<>(RegionUtils.getVanillaParameterPoints(Biomes.TAIGA));
                points.addAll(RegionUtils.getVanillaParameterPoints(Biomes.FOREST));
                points.forEach(point -> builder.replaceBiome(point, ModBiomes.VAMPIRE_FOREST));
            });
        }
    }
}