package de.teamlapen.vampirism.api.entity.hunter;

import de.teamlapen.vampirism.api.difficulty.IAdjustableLevel;
import de.teamlapen.vampirism.api.entity.IVillageCaptureEntity;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

/**
 * Advanced vampire hunter
 */
public interface IAdvancedHunter extends IHunterMob, IAdjustableLevel, IVillageCaptureEntity {

    /**
     * The type integer declares multiple things about the hunter. This the different values are stored in the different bits of the integer.
     * <br>
     *     - bit 0 defines if the hunter has a cloak<br>
     *     - bits 1-9 are used for the hunter body texture<br>
     */
    int getHunterType();

    default boolean hasCloak() {
        return (this.getHunterType() & 0b1) == 1;
    }

    default int getBodyTexture() {
        return this.getHunterType() >> 1 & 0b11111111;
    }

    @Nullable
    String getTextureName();

    boolean isLookingForHome();

    void setCampArea(AABB box);

}
