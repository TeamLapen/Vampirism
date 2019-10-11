package de.teamlapen.vampirism.world.gen.biome;

import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.world.biome.Biome;

public abstract class VampirismBiome extends Biome {

    public VampirismBiome(String regName, Builder builder) {
        super(builder);
        this.setRegistryName(REFERENCE.MODID, regName);
    }
}
