package de.teamlapen.vampirism.world.gen.biome;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.entity.EntityClassification;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.DefaultBiomeFeatures;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeManager;

public class VampireForestBiome extends VampirismBiome {
    public final static String regName = "vampire_forest";

    private static final SurfaceBuilderConfig vampire_surface = new SurfaceBuilderConfig(ModBlocks.cursed_earth.getDefaultState(), ModBlocks.cursed_earth.getDefaultState(), ModBlocks.cursed_earth.getDefaultState());

    public VampireForestBiome() {
        super(new Builder().surfaceBuilder(SurfaceBuilder.DEFAULT, vampire_surface).category(Category.FOREST).depth(0.1F).scale(0.025F).waterColor(0xEE2505).waterFogColor(0xEE2505).precipitation(RainType.NONE).parent(null).downfall(0).temperature(0.3f), !VampirismConfig.SERVER.disableVampireForest.get(), Balance.general.VAMPIRE_FOREST_WEIGHT, BiomeManager.BiomeType.WARM, BiomeDictionary.Type.FOREST, BiomeDictionary.Type.DENSE, BiomeDictionary.Type.MAGICAL, BiomeDictionary.Type.SPOOKY);
        this.setRegistryName(REFERENCE.MODID, regName);

        VampirismBiomeFeatures.addVampireTrees(this);
        VampirismBiomeFeatures.addVampirismFlowers(this);
        DefaultBiomeFeatures.addGrass(this);//was frequency 4
        DefaultBiomeFeatures.addDeadBushes(this);//was frequency 3

        this.addSpawn(VReference.VAMPIRE_CREATURE_TYPE, new SpawnListEntry(ModEntities.ghost, 3, 1, 1));
        this.addSpawn(VReference.VAMPIRE_CREATURE_TYPE, new SpawnListEntry(ModEntities.vampire, 7, 1, 3));
        this.addSpawn(VReference.VAMPIRE_CREATURE_TYPE, new SpawnListEntry(ModEntities.vampire_baron, 2, 1, 1));
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
