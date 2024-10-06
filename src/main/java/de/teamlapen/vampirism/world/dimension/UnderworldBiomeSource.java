package de.teamlapen.vampirism.world.dimension;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.core.ModBiomes;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.QuartPos;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.levelgen.DensityFunction;

import java.util.stream.Stream;

public class UnderworldBiomeSource extends BiomeSource {
    public static final MapCodec<UnderworldBiomeSource> CODEC = RecordCodecBuilder.mapCodec(inst -> {
        return inst.group(
                RegistryOps.retrieveElement(ModBiomes.VAMPIRE_FOREST))
                .apply(inst, inst.stable(UnderworldBiomeSource::new));
    });

    private final Holder<Biome> biome;

    public UnderworldBiomeSource(HolderGetter<Biome> biomeGetter) {
        this.biome = biomeGetter.getOrThrow(ModBiomes.VAMPIRE_FOREST);
    }

    private UnderworldBiomeSource(Holder<Biome> biome) {
        this.biome = biome;
    }

    @Override
    protected MapCodec<? extends BiomeSource> codec() {
        return CODEC;
    }

    @Override
    protected Stream<Holder<Biome>> collectPossibleBiomes() {
        return Stream.of(this.biome);
    }

    @Override
    public Holder<Biome> getNoiseBiome(int x, int y, int z, Climate.Sampler sampler) {
        int i = QuartPos.toBlock(x);
        int j = QuartPos.toBlock(y);
        int k = QuartPos.toBlock(z);
        int l = SectionPos.blockToSectionCoord(i);
        int i1 = SectionPos.blockToSectionCoord(k);
        if ((long)l * (long)l + (long)i1 * (long)i1 <= 4096L) {
            return this.biome;
        } else {
            int j1 = (SectionPos.blockToSectionCoord(i) * 2 + 1) * 8;
            int k1 = (SectionPos.blockToSectionCoord(k) * 2 + 1) * 8;
            double d0 = sampler.erosion().compute(new DensityFunction.SinglePointContext(j1, j, k1));
            if (d0 > 0.25) {
                return this.biome;
            } else if (d0 >= -0.0625) {
                return this.biome;
            } else {
                return this.biome;
            }
        }
    }
}
