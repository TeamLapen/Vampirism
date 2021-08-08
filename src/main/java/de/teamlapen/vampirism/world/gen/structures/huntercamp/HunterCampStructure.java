package de.teamlapen.vampirism.world.gen.structures.huntercamp;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.world.level.levelgen.feature.StructureFeature.StructureStartFactory;

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
        public Start(StructureFeature<NoneFeatureConfiguration> structure, int chunkX, int chunkZ, BoundingBox boundsIn, int referenceIn, long seed) {
            super(structure, chunkX, chunkZ, boundsIn, referenceIn, seed);
        }

        @Override
        public void generatePieces(RegistryAccess registries, ChunkGenerator chunkGenerator, StructureManager templateManager, int chunkX, int chunkZ, Biome biomeIn, NoneFeatureConfiguration featureConfig) {
            HunterCampPieces.init(chunkX, chunkZ, biomeIn, this.random, this.pieces);
            this.calculateBoundingBox();
        }
    }
}
