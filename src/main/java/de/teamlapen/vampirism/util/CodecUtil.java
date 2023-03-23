package de.teamlapen.vampirism.util;

import com.mojang.serialization.Codec;

import java.util.UUID;

public class CodecUtil {
    public static final Codec<UUID> UUID = Codec.STRING.xmap(java.util.UUID::fromString, java.util.UUID::toString);

}
