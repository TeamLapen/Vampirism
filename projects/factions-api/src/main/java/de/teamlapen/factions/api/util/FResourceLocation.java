package de.teamlapen.factions.api.util;

import net.minecraft.resources.ResourceLocation;

@SuppressWarnings("unused")
public class FResourceLocation {
    public static ResourceLocation loc(String namespace, String path) {
        return ResourceLocation.fromNamespaceAndPath(namespace, path);
    }

    public static ResourceLocation mod(String path) {
        return ResourceLocation.fromNamespaceAndPath(REFERENCE.MOD_ID, path);
    }

    public static String modString(String path) {
        return REFERENCE.MOD_ID + ":" + path;
    }

    public static ResourceLocation common(String path) {
        return ResourceLocation.fromNamespaceAndPath("c", path);
    }

    public static ResourceLocation mc(String path) {
        return ResourceLocation.withDefaultNamespace(path);
    }
}
