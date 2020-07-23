package de.teamlapen.vampirism.world.gen.biome;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;

public abstract class VampirismBiome extends Biome {

    public VampirismBiome(ResourceLocation regName, Builder builder) {
        super(builder);
        this.setRegistryName(regName);
    }
}
