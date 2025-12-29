package de.teamlapen.factions.api.util;

import net.minecraft.resources.Identifier;

@SuppressWarnings("unused")
public class FResourceLocation {
    public static Identifier loc(String namespace, String path) {
        return Identifier.fromNamespaceAndPath(namespace, path);
    }

    public static Identifier mod(String path) {
        return Identifier.fromNamespaceAndPath(REFERENCE.MOD_ID, path);
    }

    public static String modString(String path) {
        return REFERENCE.MOD_ID + ":" + path;
    }

    public static Identifier common(String path) {
        return Identifier.fromNamespaceAndPath("c", path);
    }

    public static Identifier mc(String path) {
        return Identifier.withDefaultNamespace(path);
    }
}
