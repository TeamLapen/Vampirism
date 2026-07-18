package de.teamlapen.vampirism.common.world.entity.player.hunter;

import de.teamlapen.faction.common.sounds.ISoundReference;
import de.teamlapen.vampirism.api.world.items.IItemWithTier;
import de.teamlapen.vampirism.common.world.entity.player.hunter.actions.DisguiseHunterAction;
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
    private boolean concealed = false;
    private int concealmentTicks = 0;
    private double vampireDistanceRel = 0;

    /**
     * Set while blessing an item.
     */
    @Nullable
    public ISoundReference blessingSoundReference;

    public void activateConcealment() {
        this.concealed = true;
        this.concealmentTicks = 1;
    }

    public float getConcealmentProgress() {
        return Math.clamp(this.concealmentTicks / (float) DisguiseHunterAction.FADE_TICKS, 0F, 1F);
    }

    public void increaseConcealmentTicks() {
        this.concealmentTicks++;
    }

    public boolean isConcealed() {
        return this.concealed;
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

    public void resetConcealment() {
        this.concealmentTicks = 0;
        this.concealed = false;
    }
}
