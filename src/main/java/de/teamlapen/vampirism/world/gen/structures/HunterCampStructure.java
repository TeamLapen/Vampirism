package de.teamlapen.vampirism.world.gen.structures;

import com.mojang.datafixers.Dynamic;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.ScatteredStructure;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.template.TemplateManager;

import javax.annotation.Nonnull;
import java.util.Random;
import java.util.function.Function;

public class HunterCampStructure extends ScatteredStructure<NoFeatureConfig> {
    public HunterCampStructure(Function<Dynamic<?>, ? extends NoFeatureConfig> function) {
        super(function);
    }

    @Override
    public int getSize() {
        return 1;//TODO 1.14 Structure
    }

    @Nonnull
    @Override
    public IStartFactory getStartFactory() {
        return Start::new;
    }

    @Nonnull
    @Override
    public String getStructureName() {
        return "Hunter_Camp";
    }

    @Override
    public boolean hasStartAt(@Nonnull ChunkGenerator<?> chunkGen, @Nonnull Random rand, int chunkPosX, int chunkPosZ) {
        ChunkPos chunkpos = this.getStartPositionForPosition(chunkGen, rand, chunkPosX, chunkPosZ, 0, 0);
        if (chunkPosX == chunkpos.x && chunkPosZ == chunkpos.z) {
            Biome biome = chunkGen.getBiomeProvider().getBiome(new BlockPos(chunkPosX * 16 + 9, 0, chunkPosZ * 16 + 9));
            return chunkGen.hasStructure(biome, this);
        }

        return false;
    }

    @Override
    protected int getSeedModifier() {
        return 14357617;//TODO 1.14 Structure
    }

    @Override
    protected int getBiomeFeatureDistance(ChunkGenerator<?> p_204030_1_) {
        return 2;//Balance.general.HUNTER_CAMP_DENSITY;//TODO 1.14 Structure
    }

    @Override
    protected int getBiomeFeatureSeparation(ChunkGenerator<?> p_211745_1_) {
        return 1;//super.getBiomeFeatureSeparation(p_211745_1_) >= Balance.general.HUNTER_CAMP_DENSITY?Balance.general.HUNTER_CAMP_DENSITY-1:super.getBiomeFeatureSeparation(p_211745_1_);//TODO 1.14 Structure
    }

    public static class Start extends StructureStart {
        public Start(Structure<?> p_i51341_1_, int chunkX, int chunkZ, Biome biomeIn, MutableBoundingBox boundsIn, int referenceIn, long seed) {
            super(p_i51341_1_, chunkX, chunkZ, biomeIn, boundsIn, referenceIn, seed);
        }

        @Override
        public void init(@Nonnull ChunkGenerator<?> generator, @Nonnull TemplateManager templateManagerIn, int chunkX, int chunkZ, Biome biomeIn) {
            HunterCampPieces.init(chunkX, chunkZ, biomeIn, this.rand, this.components);
            this.recalculateStructureSize();
        }
    }
}
