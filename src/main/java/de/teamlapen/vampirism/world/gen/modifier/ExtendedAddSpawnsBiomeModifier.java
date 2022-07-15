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

import java.util.List;

public record ExtendedAddSpawnsBiomeModifier(HolderSet<Biome> biomes, HolderSet<Biome> excludedBiomes, List<ExtendedSpawnData> spawners ) implements BiomeModifier {

    public static ExtendedAddSpawnsBiomeModifier singleSpawn(HolderSet<Biome> biomes, HolderSet<Biome> excludedBiomes, ExtendedSpawnData spawner)
    {
        return new ExtendedAddSpawnsBiomeModifier(biomes, excludedBiomes, List.of(spawner));
    }

    @Override
    public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        if (phase == Phase.ADD && this.biomes.contains(biome) && !this.excludedBiomes.contains(biome))
        {
            MobSpawnSettingsBuilder spawns = builder.getMobSpawnSettings();
            for (ExtendedSpawnData spawner : this.spawners)
            {
                spawns.addSpawn(spawner.category, spawner);
            }
        }
    }

    @Override
    public Codec<? extends BiomeModifier> codec() {
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

        public ExtendedSpawnData(MobSpawnSettings.SpawnerData data, MobCategory category) {
            super(data.type, data.getWeight(), data.minCount, data.maxCount);
            this.category = category;
        }

        public ExtendedSpawnData(EntityType<?> type, int weight, int minCount, int maxCount, MobCategory category) {
            super(type, weight, minCount, maxCount);
            this.category = category;
        }

        public ExtendedSpawnData(EntityType<?> type, Weight weight, int minCount, int maxCount, MobCategory category) {
            super(type, weight, minCount, maxCount);
            this.category = category;
        }
    }
}
