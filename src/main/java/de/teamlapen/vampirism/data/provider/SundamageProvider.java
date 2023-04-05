package de.teamlapen.vampirism.data.provider;

import com.google.common.collect.Sets;
import com.mojang.serialization.JsonOps;
import de.teamlapen.vampirism.core.ModTags;
import de.teamlapen.vampirism.data.reloadlistener.SundamageReloadListener;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class SundamageProvider implements DataProvider {

    private static final Logger LOGGER = LogManager.getLogger();

    protected final PackOutput.PathProvider pathProvider;
    private final String modId;

    public SundamageProvider(PackOutput packOutput, String modId) {
        this.pathProvider = packOutput.createPathProvider(PackOutput.Target.DATA_PACK, "vampirism");
        this.modId = modId;
    }

    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput pOutput) {
        Consumer consumer = new Consumer();
        this.registerNoSundamageBiomes(consumer);

        return DataProvider.saveStable(pOutput, SundamageReloadListener.RawFile.CODEC.encodeStart(JsonOps.INSTANCE, consumer.build()).getOrThrow(false, LOGGER::error), pathProvider.json(new ResourceLocation(modId, "no_sun_damage")));
    }

    @Override
    public @NotNull String getName() {
        return "No Sundamage";
    }

    protected void registerNoSundamageBiomes(Consumer consumer) {
        consumer.addBiome(ModTags.Biomes.IS_VAMPIRE_BIOME);
        consumer.addDimension(BuiltinDimensionTypes.NETHER);
        consumer.addDimension(BuiltinDimensionTypes.END);
    }

    public static class Consumer {

        private final Set<ExtraCodecs.TagOrElementLocation> biomes = Sets.newHashSet();
        private final Set<ExtraCodecs.TagOrElementLocation> dimensions = Sets.newHashSet();
        private final Set<ResourceKey<Level>> levels = Sets.newHashSet();
        private final Set<ResourceKey<Level>> sunDamageLevels = Sets.newHashSet();

        public void addBiome(ResourceLocation biome) {
            this.biomes.add(new ExtraCodecs.TagOrElementLocation(biome, false));
        }

        public void addBiome(ResourceKey<Biome> biome) {
            this.biomes.add(new ExtraCodecs.TagOrElementLocation(biome.location(), false));
        }

        public void addBiome(TagKey<Biome> biome) {
            this.biomes.add(new ExtraCodecs.TagOrElementLocation(biome.location(), true));
        }

        public void addDimension(ResourceLocation dimension) {
            this.dimensions.add(new ExtraCodecs.TagOrElementLocation(dimension, false));
        }

        public void addDimension(ResourceKey<DimensionType> dimension) {
            this.dimensions.add(new ExtraCodecs.TagOrElementLocation(dimension.location(), false));
        }

        public void addDimension(TagKey<DimensionType> dimension) {
            this.dimensions.add(new ExtraCodecs.TagOrElementLocation(dimension.location(), true));
        }

        public void noSunDamageLevel(ResourceKey<Level> level) {
            this.levels.add(level);
        }

        public void sunDamageLevel(ResourceKey<Level> level) {
            this.sunDamageLevels.add(level);
        }

        private SundamageReloadListener.RawFile build() {
            return new SundamageReloadListener.RawFile(false, new ArrayList<>(this.biomes), new ArrayList<>(this.dimensions), new ArrayList<>(this.levels), new ArrayList<>(this.sunDamageLevels));
        }
    }
}
