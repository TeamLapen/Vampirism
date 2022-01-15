package de.teamlapen.vampirism.modcompat.terrablender;

import com.mojang.datafixers.util.Pair;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.core.ModBiomes;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.SurfaceRules;
import terrablender.api.BiomeProvider;
import terrablender.api.BiomeProviders;
import terrablender.worldgen.TBClimate;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * Simple provider to add our biome to the overworld
 */
public class TerraBlenderBiomeProvider extends BiomeProvider {

    private static final ResourceLocation ID = new ResourceLocation(REFERENCE.MODID, "overworld");

    public static void register() {
        BiomeProviders.register(new TerraBlenderBiomeProvider());
    }

    public TerraBlenderBiomeProvider() {
        super(ID , 2);
    }

    @Override
    public void addOverworldBiomes(Registry<Biome> registry, Consumer<Pair<TBClimate.ParameterPoint, ResourceKey<Biome>>> mapper)
    {
        addBiomeSimilar(mapper, Biomes.TAIGA, ModBiomes.VAMPIRE_FOREST_KEY);
    }

    @Override
    public Optional<SurfaceRules.RuleSource> getOverworldSurfaceRules() {
        return Optional.of(ModBiomes.buildOverworldSurfaceRules());
    }
}
