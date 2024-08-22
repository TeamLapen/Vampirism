package de.teamlapen.vampirism.api.event;

import net.neoforged.bus.api.Event;

@SuppressWarnings("unused")
public class VampireFogEvent extends Event {
    private float fogDistanceMultiplier;

    public VampireFogEvent(float fogDistanceMultiplier) {
        this.fogDistanceMultiplier = fogDistanceMultiplier;
    }

    /**
     * @return The fog distance multiplier of vampire fog.
     */
    public float getFogDistanceMultiplier() {
        return fogDistanceMultiplier;
    }

    /**
     * @param fogDistanceMultiplier The fog distance multiplier of vampire fog
     */
    public void setFogDistanceMultiplier(float fogDistanceMultiplier) {
        this.fogDistanceMultiplier = fogDistanceMultiplier;
    }
}
