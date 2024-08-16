package de.teamlapen.vampirism.api.event;

import net.minecraft.client.player.LocalPlayer;
import net.neoforged.bus.api.Event;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class VampireFogEvent extends Event {
    private final LocalPlayer player;
    private float fogDistanceMultiplier;

    public VampireFogEvent(@NotNull LocalPlayer player, float fogDistanceMultiplier) {
        this.player = player;
        this.fogDistanceMultiplier = fogDistanceMultiplier;
    }

    /**
     * @return The Player that is seeing the fog.
     */
    @NotNull
    public LocalPlayer getPlayer() {
        return player;
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
