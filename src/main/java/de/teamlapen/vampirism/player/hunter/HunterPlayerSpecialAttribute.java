package de.teamlapen.vampirism.player.hunter;

import de.teamlapen.vampirism.player.hunter.actions.DisguiseHunterAction;

/**
 * Stores special attributes that can be activated by skills or actions.
 * One attribute should only be modified by ONE skill/action.
 */
public class HunterPlayerSpecialAttribute {

    private boolean disguised = false;
    private int disguiseTicks = 0;

    private boolean vampireNearby = false;
    private int vampireNearbyTicks = 0;

    public void activateDisguise() {
        disguised = true;
        disguiseTicks = 1;
    }

    public float getDisguiseProgress() {
        return disguiseTicks > DisguiseHunterAction.FADE_TICKS ? 1F : disguiseTicks / (float) DisguiseHunterAction.FADE_TICKS;
    }

    public void increaseDisguiseTicks() {
        disguiseTicks++;
    }

    public boolean isDisguised() {
        return disguised;
    }

    public void resetDisguise() {
        disguiseTicks = 0;
        disguised = false;
    }

    public void nearbyVampire() {
        if (vampireNearby) {
            vampireNearbyTicks++;
        } else {
            vampireNearby = true;
            vampireNearbyTicks = 1;
        }
    }

    public float getVampireNearbyProgress() {
        return vampireNearbyTicks > 20 ? 0.2F : 0.2F * (vampireNearbyTicks / (float) 20);
    }

    public boolean isVampireNearby() {
        return vampireNearby;
    }

    public void resetVampireNearby() {
        vampireNearby = false;
        vampireNearbyTicks = 0;
    }
}
