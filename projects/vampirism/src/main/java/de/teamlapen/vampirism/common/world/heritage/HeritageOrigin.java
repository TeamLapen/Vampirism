package de.teamlapen.vampirism.common.world.heritage;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

public enum HeritageOrigin implements StringRepresentable {
    VAMPIRE_FANG("vampire_fang"),
    SANGUINARE_INJECTION("sanguinare_injection"),
    VAMPIRE_PLAYER("vampire_player"),
    ORDINARY_NPC("ordinary_npc"),
    NAMED_NPC("named_npc"),
    UNKNOWN("unknown");

    public static final Codec<HeritageOrigin> CODEC = StringRepresentable.fromEnum(HeritageOrigin::values);

    private final String name;

    HeritageOrigin(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}
