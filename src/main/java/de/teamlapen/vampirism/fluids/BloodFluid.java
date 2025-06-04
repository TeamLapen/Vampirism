package de.teamlapen.vampirism.fluids;

import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModFluids;
import de.teamlapen.vampirism.core.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;

public abstract class BloodFluid extends BaseFlowingFluid {

    public static final Properties PROPERTIES = new Properties(ModFluids.BLOOD_TYPE, ModFluids.BLOOD, ModFluids.FLOWING_BLOOD).bucket(ModItems.BLOOD_BUCKET).block(ModBlocks.BLOOD).explosionResistance(100.0F).tickRate(8);

    public BloodFluid(Properties properties) {
        super(properties);
    }

    // TODO: Fix that, add some particles that would float in blood and represent some sort of red blood cells. Currently it does not work at all for an unknown reason
    @Override
    protected void animateTick(Level level, BlockPos pos, FluidState state, RandomSource random) {
        if ((state.isSource() || state.getValue(FALLING)) && random.nextInt(10) == 0) {
             level.addParticle(
                    ParticleTypes.UNDERWATER,
                    (double) pos.getX() + random.nextDouble(),
                    (double) pos.getY() + random.nextDouble(),
                    (double) pos.getZ() + random.nextDouble(),
                    0.0,
                    0.0,
                    0.0
            );
        }
    }
}
