package de.teamlapen.vampirism.common.world.dimensions.velmorra;

import com.mojang.serialization.MapCodec;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.synth.SimplexNoise;
import org.jetbrains.annotations.NotNull;

public class VelmorraDensityFunction implements DensityFunction.SimpleFunction {
    public static final KeyDispatchDataCodec<VelmorraDensityFunction> CODEC = KeyDispatchDataCodec.of(
            MapCodec.unit(new VelmorraDensityFunction(0))
    );

    private final SimplexNoise islandNoise;

    public VelmorraDensityFunction(long seed) {
        RandomSource randomsource = new LegacyRandomSource(seed);
        randomsource.consumeCount(45745);
        this.islandNoise = new SimplexNoise(randomsource);
    }

    private static float getHeightValue(SimplexNoise noise, int x, int z) {
        float f = 100.0F - Mth.sqrt((float)(x * x + z * z)) * 4.0F;
        f = Mth.clamp(f, -100.0F, 100.0F);

        return f;
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
        return 0.5625;
    }

    @Override
    public @NotNull KeyDispatchDataCodec<? extends DensityFunction> codec() {
        return CODEC;
    }
}
