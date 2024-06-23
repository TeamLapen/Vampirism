package de.teamlapen.vampirism.core.tags;

import de.teamlapen.vampirism.api.util.VResourceLocation;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.jetbrains.annotations.NotNull;

public class ModStructureTags {
    public static final TagKey<Structure> HUNTER_OUTPOST = tag("hunter_outpost");

    private static @NotNull TagKey<Structure> tag(@NotNull String name) {
        return TagKey.create(Registries.STRUCTURE, VResourceLocation.mod(name));
    }

}
