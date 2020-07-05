package de.teamlapen.vampirism.world.gen.structures.huntercamp;

import com.mojang.serialization.Codec;
import de.teamlapen.vampirism.config.BalanceConfig;
import de.teamlapen.vampirism.config.VampirismConfig;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.gen.settings.StructureSeparationSettings;
import org.apache.logging.log4j.LogManager;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class HunterCampStructure extends Structure<NoFeatureConfig> {

    public static StructureSeparationSettings getSettings() {
        if (VampirismConfig.BALANCE.hunterTentDistance.get() <= VampirismConfig.BALANCE.hunterTentSeparation.get()) {
            LogManager.getLogger(BalanceConfig.class).warn("config value 'hunterTentDistance' is not set greater than 'hunterTentSeparation'. 'hunterTentDistance' increased");
            VampirismConfig.BALANCE.hunterTentDistance.set(VampirismConfig.BALANCE.hunterTentSeparation.get() + 1);
        }
        return new StructureSeparationSettings(VampirismConfig.BALANCE.hunterTentDistance.get(),VampirismConfig.BALANCE.hunterTentSeparation.get(),14357719);
    }

    public HunterCampStructure(Codec<NoFeatureConfig> deserializer) {
        super(deserializer);
    }

    @Nonnull
    @Override
    public IStartFactory<NoFeatureConfig> getStartFactory() {
        return Start::new;
    }

    public static class Start extends StructureStart<NoFeatureConfig> {
        public Start(Structure<NoFeatureConfig> structure, int chunkX, int chunkZ, MutableBoundingBox boundsIn, int referenceIn, long seed) {
            super(structure, chunkX, chunkZ, boundsIn, referenceIn, seed);
        }

        @Override
        public void func_230364_a_(ChunkGenerator generator, TemplateManager templateManagerIn, int chunkX, int chunkZ, Biome biomeIn, NoFeatureConfig featureConfig) {
            HunterCampPieces.init(chunkX, chunkZ, biomeIn, this.rand, this.components);
            this.recalculateStructureSize();
        }
    }
}
