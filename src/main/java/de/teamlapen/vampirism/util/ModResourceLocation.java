package de.teamlapen.vampirism.util;

import de.teamlapen.vampirism.REFERENCE;
import net.minecraft.resources.ResourceLocation;

public class ModResourceLocation {

    public static ResourceLocation mod(String path) {
        return ResourceLocation.fromNamespaceAndPath(REFERENCE.MODID, path);
    }
}
