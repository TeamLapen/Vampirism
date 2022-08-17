package de.teamlapen.vampirism.world.gen.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.core.ModBiomes;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.util.random.Weight;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.MobSpawnSettingsBuilder;
import net.minecraftforge.common.world.ModifiableBiomeInfo;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record ExtendedAddSpawnsBiomeModifier(HolderSet<Biome> biomes, HolderSet<Biome> excludedBiomes, List<ExtendedSpawnData> spawners) implements BiomeModifier {

    public static @NotNull ExtendedAddSpawnsBiomeModifier singleSpawn(HolderSet<Biome> biomes, HolderSet<Biome> excludedBiomes, @NotNull ExtendedSpawnData spawner) {
        return new ExtendedAddSpawnsBiomeModifier(biomes, excludedBiomes, List.of(spawner));
    }

    @Override
    public void modify(@NotNull Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.@NotNull Builder builder) {
        if (phase == Phase.ADD && this.biomes.contains(biome) && !this.excludedBiomes.contains(biome)) {
            MobSpawnSettingsBuilder spawns = builder.getMobSpawnSettings();
            for (ExtendedSpawnData spawner : this.spawners) {
                spawns.addSpawn(spawner.category, spawner);
            }
        }
    }

    @Override
    public @NotNull Codec<? extends BiomeModifier> codec() {
        return ModBiomes.ADD_SPAWNS_BIOME_MODIFIER_TYPE.get();
    }

    public static class ExtendedSpawnData extends MobSpawnSettings.SpawnerData {

        public static final Codec<ExtendedSpawnData> CODEC = RecordCodecBuilder.create((builder) -> {
            return builder.group(MobSpawnSettings.SpawnerData.CODEC.fieldOf("super").forGetter(b -> {
                return b;
            }), MobCategory.CODEC.fieldOf("category").forGetter(b -> {
                return b.category;
            })).apply(builder, ExtendedSpawnData::new);
        });

        private final MobCategory category;

        public ExtendedSpawnData(MobSpawnSettings.@NotNull SpawnerData data, MobCategory category) {
            super(data.type, data.getWeight(), data.minCount, data.maxCount);
            this.category = category;
        }

        public ExtendedSpawnData(@NotNull EntityType<?> type, int weight, int minCount, int maxCount, MobCategory category) {
            super(type, weight, minCount, maxCount);
            this.category = category;
        }

        public ExtendedSpawnData(@NotNull EntityType<?> type, @NotNull Weight weight, int minCount, int maxCount, MobCategory category) {
            super(type, weight, minCount, maxCount);
            this.category = category;
        }
    }
}
