package de.teamlapen.vampirism.common.world.features;

import de.teamlapen.vampirism.api.util.VIdentifier;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.placement.CaveSurface;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public class ModNoiseParameters {

    public static final ResourceKey<NormalNoise.NoiseParameters> DARK_STONE_ROOTS = ResourceKey.create(Registries.NOISE, VIdentifier.mod("dark_stone_roots"));

    private static final int DARK_STONE_BASE_DEPTH = 4;
    private static final int DARK_STONE_ROOT_DEPTH = 6;
    private static final double DARK_STONE_ROOT_NOISE_START = 0.05;
    private static final double DARK_STONE_ROOT_NOISE_STEP = 0.1;

    public static void bootstrapNoises(BootstrapContext<NormalNoise.NoiseParameters> context) {
        context.register(DARK_STONE_ROOTS, new NormalNoise.NoiseParameters(-2, 1.0, 1.0));
    }

    public static SurfaceRules.RuleSource darkStoneRoots(SurfaceRules.RuleSource darkStone) {
        SurfaceRules.RuleSource[] tiers = new SurfaceRules.RuleSource[DARK_STONE_ROOT_DEPTH + 1];
        for (int i = 0; i < DARK_STONE_ROOT_DEPTH; i++) {
            int depth = DARK_STONE_ROOT_DEPTH - i;
            double threshold = DARK_STONE_ROOT_NOISE_START + (depth - 1) * DARK_STONE_ROOT_NOISE_STEP;
            tiers[i] = SurfaceRules.ifTrue(SurfaceRules.noiseCondition(DARK_STONE_ROOTS, threshold),
                    SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(DARK_STONE_BASE_DEPTH + depth, true, 2, CaveSurface.FLOOR), darkStone));
        }
        tiers[DARK_STONE_ROOT_DEPTH] = SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(DARK_STONE_BASE_DEPTH, true, 2, CaveSurface.FLOOR), darkStone);
        return SurfaceRules.sequence(tiers);
    }
}
