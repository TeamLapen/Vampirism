package de.teamlapen.vampirism.world.gen.biome;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.Feature;

/**
 * base class fro storing all {@link Feature}
 */
public abstract class VampirismBiome extends Biome {
    public static boolean debug = false;

    protected VampirismBiome(Biome.Builder biomeBuilder) {
        super(biomeBuilder);
    }
}
