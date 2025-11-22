package de.teamlapen.vampirism.misc.injection.client;

import de.teamlapen.factions.misc.extensions.client.ICamera;

public interface ICameraMock extends ICamera {
    @Override
    default void invokeMove(float zoom, float dy, float dx) {

    }
}
