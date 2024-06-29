package de.teamlapen.vampirism.api.entity.vampire;

import de.teamlapen.vampirism.api.difficulty.IAdjustableLevel;
import de.teamlapen.vampirism.api.entity.IEntityLeader;
import de.teamlapen.vampirism.api.entity.IVillageCaptureEntity;
import org.jetbrains.annotations.Nullable;

/**
 * Advanced vampire
 */
public interface IAdvancedVampire extends IVampireMob, IAdjustableLevel, IVillageCaptureEntity, IEntityLeader {

    /**
     * The type integer declares multiple things about the hunter. This the different values are stored in the different bits of the integer.
     * <br>
     * - Bits 0-1 are used for the vampire eyes<br>
     * - Bits 2-3 are used for the vampire fangs<br>
     * - bits 4-12 are used for the vampire body texture<br>
     */
    default int getVampireType() {
        return 0;
    }

    default int getEyeType() {
        return getVampireType() & 0b111111;
    }

    default int getFangType() {
        return getVampireType() >> 6 & 0b111111;
    }

    default int getBodyTexture() {
        return getVampireType() >> 12 & 0b11111111;
    }

    @Nullable
    String getTextureName();

}
