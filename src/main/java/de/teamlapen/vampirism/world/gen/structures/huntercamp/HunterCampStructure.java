package de.teamlapen.vampirism.world.gen.structures.huntercamp;

import de.teamlapen.vampirism.config.BalanceConfig;
import de.teamlapen.vampirism.config.VampirismConfig;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.ScatteredStructure;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.template.TemplateManager;
import org.apache.logging.log4j.LogManager;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class HunterCampStructure extends ScatteredStructure<NoFeatureConfig> {

    public HunterCampStructure() {
        super(NoFeatureConfig::deserialize);
    }

    @Override
    public int getSize() {
        //just for legacy
        return 1;
    }

    @Nonnull
    @Override
    public IStartFactory getStartFactory() {
        return Start::new;
    }

    @Nonnull
    @Override
    @SuppressWarnings("ConstantConditions")
    public String getStructureName() {
        //just for legacy
        return this.getRegistryName().toString();
    }

    @Override
    protected int getSeedModifier() {
        return 14357719;
    }

    @Override
    protected int getBiomeFeatureDistance(ChunkGenerator<?> chunkGeneratorIn) {
        if (VampirismConfig.BALANCE.hunterTentDistance.get() <= VampirismConfig.BALANCE.hunterTentSeparation.get()) {
            LogManager.getLogger(BalanceConfig.class).warn("config value 'hunterTentDistance' is not set greater than 'hunterTentSeparation'. 'hunterTentDistance' increased");
            VampirismConfig.BALANCE.hunterTentDistance.set(VampirismConfig.BALANCE.hunterTentSeparation.get() + 1);
        }
        return VampirismConfig.BALANCE.hunterTentDistance.get();
    }

    @Override
    protected int getBiomeFeatureSeparation(ChunkGenerator<?> chunkGeneratorIn) {
        return VampirismConfig.BALANCE.hunterTentSeparation.get();
    }

    public static class Start extends StructureStart {
        public Start(Structure<?> p_i51341_1_, int chunkX, int chunkZ, Biome biomeIn, MutableBoundingBox boundsIn, int referenceIn, long seed) {
            super(p_i51341_1_, chunkX, chunkZ, biomeIn, boundsIn, referenceIn, seed);
        }

        @Override
        public void init(ChunkGenerator<?> generator, TemplateManager templateManagerIn, int chunkX, int chunkZ, Biome biomeIn) {
            HunterCampPieces.init(chunkX, chunkZ, biomeIn, this.rand, this.components);
            this.recalculateStructureSize();
        }
    }
}
