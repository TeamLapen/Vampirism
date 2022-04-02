package de.teamlapen.vampirism.modcompat.terrablender;


import com.mojang.datafixers.util.Pair;
import de.teamlapen.vampirism.REFERENCE;
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

import java.util.function.Consumer;

public class TerraBlenderRegistration {

    public static void registerRegions(){
        Regions.register(new ForestRegion());
    }

    public static void registerSurfaceRules(){
        SurfaceRuleManager.addSurfaceRules(SurfaceRuleManager.RuleCategory.OVERWORLD, REFERENCE.MODID, OverworldModifications.buildOverworldSurfaceRules());
    }

    static class ForestRegion extends Region{

        public ForestRegion() {
            super(new ResourceLocation(REFERENCE.MODID, "overworld"), RegionType.OVERWORLD, 2);
        }

        @Override
        public void addBiomes(Registry<Biome> registry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper) {
            this.addBiomeSimilar(mapper, Biomes.TAIGA, ModBiomes.VAMPIRE_FOREST);
        }
    }
}
