package de.teamlapen.vampirism.misc.injection.client;

import de.teamlapen.faction.misc.extensions.client.ICamera;

@Deprecated
public interface ICameraVampirismMock extends ICamera {
    @Override
    default void invokeMove(float zoom, float dy, float dx) {
        throw new IllegalStateException("This class is only supported as injection class");
    }
}
