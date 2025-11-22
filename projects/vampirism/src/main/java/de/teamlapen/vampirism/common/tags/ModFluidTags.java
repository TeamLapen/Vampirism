package de.teamlapen.vampirism.common.tags;

import de.teamlapen.vampirism.api.util.VResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;

public class ModFluidTags {
    public static final TagKey<Fluid> BLOOD = tag("blood");

    private static TagKey<Fluid> tag(String name) {
        return FluidTags.create(VResourceLocation.mod(name));
    }
}
