package de.teamlapen.vampirism.api.world;

import com.mojang.serialization.Codec;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

/**
 * Can be used to determine which strength/tier something is of. e.g. used for holy water and garlic
 */
public enum EnumStrength implements StringRepresentable {
    NONE("none", 0),
    WEAK("weak", 1),
    MEDIUM("medium", 2),
    STRONG("strong", 3);

    public static final Codec<EnumStrength> CODEC = StringRepresentable.fromEnum(EnumStrength::values);
    public static final StreamCodec<?, EnumStrength> STREAM_CODEC = NeoForgeStreamCodecs.enumCodec(EnumStrength.class);

    private final String name;
    private final int strength;

    EnumStrength(String name, int strength) {
        this.name = name;
        this.strength = strength;
    }

    public int getStrength() {
        return strength;
    }

    /**
     * If this strength is stronger than the given one.
     */
    public boolean isStrongerThan(EnumStrength compare) {
        return this.strength > compare.strength;
    }


    @Override
    public String getSerializedName() {
        return this.name;
    }
}
