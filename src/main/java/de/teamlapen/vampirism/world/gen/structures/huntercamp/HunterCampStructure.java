package de.teamlapen.vampirism.world.gen.structures.huntercamp;

import com.mojang.serialization.Codec;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class HunterCampStructure extends StructureFeature<NoneFeatureConfiguration> {

    public HunterCampStructure(Codec<NoneFeatureConfiguration> deserializer) {
        super(deserializer);
    }

    @Nonnull
    @Override
    public StructureStartFactory<NoneFeatureConfiguration> getStartFactory() {
        return Start::new;
    }

    public static class Start extends StructureStart<NoneFeatureConfiguration> {


        public Start(StructureFeature<NoneFeatureConfiguration> p_163595_, ChunkPos p_163596_, int p_163597_, long p_163598_) {
            super(p_163595_, p_163596_, p_163597_, p_163598_);
        }

        @Override
        public void generatePieces(RegistryAccess pRegistryAccess, ChunkGenerator pChunkGenerator, StructureManager pStructureManager, ChunkPos pChunkPos, Biome pBiome, NoneFeatureConfiguration pConfig, LevelHeightAccessor pLevel) {
            HunterCampPieces.init(pChunkPos.x, pChunkPos.z, pBiome, this.random, this);
        }
    }
}
