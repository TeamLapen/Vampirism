package de.teamlapen.vampirism.common.world.dimensions.velmorra;

import com.mojang.serialization.MapCodec;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.synth.SimplexNoise;

public class VelmorraDensityFunction implements DensityFunction.SimpleFunction {
    public static final KeyDispatchDataCodec<VelmorraDensityFunction> CODEC = KeyDispatchDataCodec.of(
            MapCodec.unit(new VelmorraDensityFunction(0))
    );

    private static final int FLAT_HALF_SIZE = 135;
    private static final int TRANSITION_SIZE = 32;
    private static final float ISLAND_SCALE = 1.5F;

    private final SimplexNoise islandNoise;

    public VelmorraDensityFunction(long seed) {
        RandomSource randomsource = new LegacyRandomSource(seed);
        randomsource.consumeCount(45745);
        this.islandNoise = new SimplexNoise(randomsource);
    }

    private static float getHeightValue(SimplexNoise noise, int x, int z) {
        // Use actual block coordinates (multiply back by 8 since they were divided in compute())
        int blockX = x * 8;
        int blockZ = z * 8;

        // Calculate distance from the edge of the flat square (using max of x/z for square shape)
        int distFromEdgeX = Math.abs(blockX) - FLAT_HALF_SIZE;
        int distFromEdgeZ = Math.abs(blockZ) - FLAT_HALF_SIZE;
        int distFromEdge = Math.max(distFromEdgeX, distFromEdgeZ);

        // Flat area height
        float flatHeight = 150.0F;

        // Natural terrain height (scaled 50% bigger)
        float distance = Mth.sqrt((float)(x * x + z * z));
        float scaledDistance = distance / ISLAND_SCALE;
        float naturalHeight = flatHeight - scaledDistance * 4.0F;
        naturalHeight = Mth.clamp(naturalHeight, -100.0F, flatHeight);

        if (distFromEdge <= 0) {
            // Inside the flat square
            return flatHeight;
        } else if (distFromEdge < TRANSITION_SIZE) {
            // Smooth transition zone - use smoothstep for nice blending
            float t = (float) distFromEdge / TRANSITION_SIZE;
            float smoothT = t * t * (3.0F - 2.0F * t); // Smoothstep function
            return Mth.lerp(smoothT, flatHeight, naturalHeight);
        } else {
            // Outside transition - natural terrain
            return naturalHeight;
        }
    }

    @Override
    public double compute(DensityFunction.FunctionContext context) {
        return ((double)getHeightValue(this.islandNoise, context.blockX() / 8, context.blockZ() / 8) - 8.0) / 128;
    }

    @Override
    public double minValue() {
        return -0.84375;
    }

    @Override
    public double maxValue() {
        return 0.8625;
    }

    @Override
    public KeyDispatchDataCodec<? extends DensityFunction> codec() {
        return CODEC;
    }
}
