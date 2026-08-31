package de.teamlapen.vampirism.common.world.heritage;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.util.StringRepresentable;

public enum HeritageOrigin implements StringRepresentable {
    INDEPENDENT("independent"),
    INHERITED("inherited");

    public static final Codec<HeritageOrigin> CODEC = Codec.STRING.comapFlatMap(HeritageOrigin::fromSerializedName, HeritageOrigin::getSerializedName);

    private final String name;

    HeritageOrigin(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    private static DataResult<HeritageOrigin> fromSerializedName(String name) {
        return switch (name) {
            case "independent", "vampire_fang", "sanguinare_injection", "ordinary_npc", "unknown" -> DataResult.success(INDEPENDENT);
            case "inherited", "vampire_player", "named_npc" -> DataResult.success(INHERITED);
            default -> DataResult.error(() -> "Unknown heritage origin " + name);
        };
    }
}
