package de.teamlapen.vampirism.world.gen.biome;

import de.teamlapen.vampirism.api.world.IVampireBiome;
import de.teamlapen.vampirism.config.BalanceMobProps;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.world.gen.features.VampirismBiomeFeatures;
import net.minecraft.entity.EntityClassification;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.BiomeAmbience;
import net.minecraft.world.biome.DefaultBiomeFeatures;
import net.minecraft.world.biome.MoodSoundAmbience;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;

import java.util.function.Function;

public class VampireForestBiome extends VampirismBiome implements IVampireBiome {

    private static final SurfaceBuilderConfig vampire_surface = new SurfaceBuilderConfig(ModBlocks.cursed_earth.getDefaultState(), ModBlocks.cursed_earth.getDefaultState(), ModBlocks.cursed_earth.getDefaultState());

    public VampireForestBiome(ResourceLocation regName, Function<Builder, Builder> builder) {
        super(regName, builder.apply(new Builder().surfaceBuilder(SurfaceBuilder.DEFAULT, vampire_surface).category(Category.FOREST).depth(0.1F).scale(0.025F).func_235097_a_((new BiomeAmbience.Builder()).func_235246_b_/*waterColor*/(0xEE2505).func_235248_c_/*waterfogColor*/(0xEE2505).func_235239_a_(0xAA5555).func_235243_a_(MoodSoundAmbience.field_235027_b_).func_235238_a_()).precipitation(RainType.NONE).parent(null).downfall(0).temperature(0.3f)));
        this.noSunDamageRegister();

        VampirismBiomeFeatures.addVampireTrees(this);
        VampirismBiomeFeatures.addVampirismFlowers(this);
        DefaultBiomeFeatures.addGrass(this);
        DefaultBiomeFeatures.addDeadBushes(this);

        //All EntityTypes used here have to be registered in the static part of ModEntities
        this.addSpawn(EntityClassification.MONSTER, new SpawnListEntry(ModEntities.vampire, BalanceMobProps.mobProps.VAMPIRE_SPAWN_CHANCE / 2, 1, 3));
        this.addSpawn(EntityClassification.MONSTER, new SpawnListEntry(ModEntities.vampire_baron, BalanceMobProps.mobProps.VAMPIRE_BARON_SPAWN_CHANCE, 1, 1));
        this.addSpawn(EntityClassification.AMBIENT, new SpawnListEntry(ModEntities.blinding_bat, BalanceMobProps.mobProps.BLINDING_BAT_SPAWN_CHANCE, 2, 4));
        this.addSpawn(EntityClassification.CREATURE, new SpawnListEntry(ModEntities.dummy_creature, BalanceMobProps.mobProps.DUMMY_CREATURE_SPAWN_CHANCE, 3, 6));
    }

    @Override
    public int getFoliageColor() {
        return 0x1E1F1F;
    }


    @Override
    public int getGrassColor(double p_225528_1_, double p_225528_3_) {
        // 0x7A317A; dark purple
        return 0x1E1F1F;
    }

    @Override
    public int getSkyColor() {
        return 0xA33641;
    }
}
