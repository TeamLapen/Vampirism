package de.teamlapen.vampirism.world.gen.biome;

import de.teamlapen.vampirism.core.ModEntities;
import net.minecraft.entity.EntityClassification;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.DefaultBiomeFeatures;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;

public class VampireForestBiome extends VampirismBiome {
    public final static String name = "vampireForest";

    public VampireForestBiome(Biome.Builder biomeBuilder) {
        super(biomeBuilder.surfaceBuilder(SurfaceBuilder.DEFAULT, VampirismBiomeFeatures.VAMPIRE_SURFACE).category(Category.FOREST).depth(0.1F).scale(0.025F).waterColor(0xEE2505).waterFogColor(0xEE2505).precipitation(RainType.NONE).parent(null).downfall(0).temperature(0.3f));

        VampirismBiomeFeatures.addVampireTrees(this);
        VampirismBiomeFeatures.addVampirismFlowers(this);
        DefaultBiomeFeatures.addGrass(this);//was frequency 4
        DefaultBiomeFeatures.addDeadBushes(this);//was frequency 3

        this.addSpawn(EntityClassification.MONSTER, new SpawnListEntry(ModEntities.ghost, 3, 1, 1));
        this.addSpawn(EntityClassification.MONSTER, new SpawnListEntry(ModEntities.vampire, 7, 1, 3));
        this.addSpawn(EntityClassification.MONSTER, new SpawnListEntry(ModEntities.vampire_baron, 2, 1, 1));
        this.addSpawn(EntityClassification.AMBIENT, new SpawnListEntry(ModEntities.blinding_bat, 8, 2, 4));
        this.addSpawn(EntityClassification.CREATURE, new SpawnListEntry(ModEntities.dummy_creature, 15, 3, 6));
    }

    @Override
    public int getFoliageColor(BlockPos pos) {
        return 0x1E1F1F;
    }


    @Override
    public int getGrassColor(BlockPos pos) {
        // 0x7A317A; dark purple
        return 0x1E1F1F;
    }

    @Override
    public int getSkyColorByTemp(float p_76731_1_) {
        return 0xA33641;
    }
}
