package de.teamlapen.vampirism.modcompat.terrablender;


import com.mojang.datafixers.util.Pair;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModBiomes;
import de.teamlapen.vampirism.world.biome.OverworldModifications;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;
import terrablender.api.Region;
import terrablender.api.RegionType;
import terrablender.api.Regions;
import terrablender.api.SurfaceRuleManager;
import terrablender.worldgen.RegionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class TerraBlenderRegistration {

    public static void registerRegions() {
        Regions.register(new ForestRegion());
    }

    public static void registerSurfaceRules() {
        SurfaceRuleManager.addSurfaceRules(SurfaceRuleManager.RuleCategory.OVERWORLD, REFERENCE.MODID, OverworldModifications.buildOverworldSurfaceRules());
    }

    static class ForestRegion extends Region {

        public ForestRegion() {
            super(new ResourceLocation(REFERENCE.MODID, "overworld"), RegionType.OVERWORLD, VampirismConfig.COMMON.vampireForestWeight_terrablender.get());
        }

        @Override
        public void addBiomes(Registry<Biome> registry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper) {
            this.addModifiedVanillaOverworldBiomes(mapper, builder -> {
                List<Climate.ParameterPoint> points = new ArrayList<>(RegionUtils.getVanillaParameterPoints(Biomes.TAIGA));
                points.addAll(RegionUtils.getVanillaParameterPoints(Biomes.FOREST));
                points.forEach(point -> builder.replaceBiome(point, ModBiomes.VAMPIRE_FOREST.getKey()));
            });
        }
    }
}
