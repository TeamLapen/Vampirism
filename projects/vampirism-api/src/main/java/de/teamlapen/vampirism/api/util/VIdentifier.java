package de.teamlapen.vampirism.api.util;

import de.teamlapen.vampirism.api.VReference;
import net.minecraft.resources.Identifier;

public class VIdentifier {

    public static final Identifier EMPTY = mc("empty");

    public static Identifier loc(String namespace, String path) {
        return Identifier.fromNamespaceAndPath(namespace, path);
    }

    public static Identifier mod(String path) {
        return Identifier.fromNamespaceAndPath(VReference.MODID, path);
    }

    public static String modString(String path) {
        return VReference.MODID + ":" + path;
    }

    public static Identifier common(String path) {
        return Identifier.fromNamespaceAndPath("c", path);
    }

    public static Identifier mc(String path) {
        return Identifier.withDefaultNamespace(path);
    }
}
