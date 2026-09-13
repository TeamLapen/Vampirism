package de.teamlapen.faction.api.client;

public record ItemBar(float progress, int color) {

    public ItemBar {
        progress = Math.clamp(progress, 0f, 1f);
    }
}
