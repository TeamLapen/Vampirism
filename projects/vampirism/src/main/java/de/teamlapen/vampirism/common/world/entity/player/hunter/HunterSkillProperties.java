package de.teamlapen.vampirism.common.world.entity.player.hunter;

import de.teamlapen.faction.common.sounds.ISoundReference;
import de.teamlapen.vampirism.api.world.items.IItemWithTier;
import de.teamlapen.vampirism.common.world.entity.player.hunter.actions.CamouflageAction;
import de.teamlapen.vampirism.common.world.items.HunterCoatItem;
import org.jetbrains.annotations.Nullable;

/**
 * Stores special attributes that can be activated by skills or actions.
 * One attribute should only be modified by ONE skill/action.
 */
public class HunterSkillProperties {

    /**
     * Value from {@link HunterCoatItem#isFullyEquipped(net.minecraft.world.entity.player.Player)} cached in {@link HunterPlayer#onUpdate()}
     * Null if not fully equipped, otherwise min tier
     */
    @Nullable
    public IItemWithTier.Tier fullHunterCoat;
    private boolean inCamouflage = false;
    private int camouflageTicks = 0;
    private double vampireDistanceRel = 0;

    /**
     * Set while blessing an item.
     */
    @Nullable
    public ISoundReference blessingSoundReference;

    public void activateCamouflage() {
        this.inCamouflage = true;
        this.camouflageTicks = 1;
    }

    public float getCamouflageProgress() {
        return Math.clamp(this.camouflageTicks / (float) CamouflageAction.FADE_TICKS, 0F, 1F);
    }

    public void increaseCamouflageTicks() {
        this.camouflageTicks++;
    }

    public boolean isInCamouflage() {
        return this.inCamouflage;
    }

    public void resetCamouflage() {
        this.camouflageTicks = 0;
        this.inCamouflage = false;
    }

    public boolean isVampireNearby() {
        return vampireDistanceRel > 0;
    }

    public void nearbyVampire(double distanceRel) {
        vampireDistanceRel = distanceRel;
    }

    public float getVampireNearbyProgress() {
        return (float) vampireDistanceRel;
    }
}
