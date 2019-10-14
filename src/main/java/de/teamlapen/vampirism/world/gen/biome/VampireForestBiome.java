package de.teamlapen.vampirism.world.gen.biome;

import de.teamlapen.vampirism.config.BalanceMobProps;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.world.gen.features.VampirismBiomeFeatures;
import net.minecraft.entity.EntityClassification;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.DefaultBiomeFeatures;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;

public class VampireForestBiome extends VampireBiome {
    public final static String regName = "vampire_forest";

    private static final SurfaceBuilderConfig vampire_surface = new SurfaceBuilderConfig(ModBlocks.cursed_earth.getDefaultState(), ModBlocks.cursed_earth.getDefaultState(), ModBlocks.cursed_earth.getDefaultState());

    public VampireForestBiome() {//TODO 1.14 entity weight
        super(regName, new Builder().surfaceBuilder(SurfaceBuilder.DEFAULT, vampire_surface).category(Category.FOREST).depth(0.1F).scale(0.025F).waterColor(0xEE2505).waterFogColor(0xEE2505).precipitation(RainType.NONE).parent(null).downfall(0).temperature(0.3f));

        VampirismBiomeFeatures.addVampireTrees(this);
        VampirismBiomeFeatures.addVampirismFlowers(this);
        DefaultBiomeFeatures.addGrass(this);
        DefaultBiomeFeatures.addDeadBushes(this);

        //All EntityTypes used here have to be registered in the static part of ModEntities
        this.addSpawn(EntityClassification.MONSTER, new SpawnListEntry(ModEntities.ghost, BalanceMobProps.mobProps.GHOST_SPAWN_CHANCE, 1, 1));
        this.addSpawn(EntityClassification.MONSTER, new SpawnListEntry(ModEntities.vampire, BalanceMobProps.mobProps.VAMPIRE_SPAWN_CHANCE, 1, 3));
        this.addSpawn(EntityClassification.MONSTER, new SpawnListEntry(ModEntities.vampire_baron, BalanceMobProps.mobProps.VAMPIRE_BARON_SPAWN_CHANCE, 1, 1));
        this.addSpawn(EntityClassification.AMBIENT, new SpawnListEntry(ModEntities.blinding_bat, BalanceMobProps.mobProps.BLINDING_BAT_SPAWN_CHANCE, 2, 4));
        this.addSpawn(EntityClassification.CREATURE, new SpawnListEntry(ModEntities.dummy_creature, BalanceMobProps.mobProps.DUMMY_CREATURE_SPAWN_CHANCE, 3, 6));
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
