package de.teamlapen.vampirism.world.gen.structures;

import com.mojang.datafixers.Dynamic;

import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.ScatteredStructure;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.template.TemplateManager;

import java.util.function.Function;

public class HunterCampStructure extends ScatteredStructure<NoFeatureConfig> {
    public HunterCampStructure(Function<Dynamic<?>, ? extends NoFeatureConfig> p_i51427_1_) {
        super(p_i51427_1_);
    }

    @Override
    public IStartFactory getStartFactory() {
        return Start::new;
    }

    @Override
    public String getStructureName() {
        return "Hunter_Camp";
    }

    @Override
    public int getSize() {
        return 1;//TODO 1.14 Structure
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
        public void init(ChunkGenerator<?> generator, TemplateManager templateManagerIn, int chunkX, int chunkZ, Biome biomeIn) {
            HunterCampPieces.Fireplace hunterCamp = new HunterCampPieces.Fireplace(this.rand, chunkX, 63, chunkZ);
            this.components.add(hunterCamp);
            hunterCamp.buildComponent(hunterCamp, components, rand);
            this.recalculateStructureSize();
        }
    }
}
