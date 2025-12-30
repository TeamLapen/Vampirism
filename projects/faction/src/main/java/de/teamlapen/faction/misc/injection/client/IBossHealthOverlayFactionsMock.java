package de.teamlapen.faction.misc.injection.client;

import de.teamlapen.faction.misc.extensions.client.IBossHealthOverlay;
import net.minecraft.client.gui.components.LerpingBossEvent;

import java.util.Map;
import java.util.UUID;

@Deprecated
public interface IBossHealthOverlayFactionsMock extends IBossHealthOverlay {
    @Override
    default Map<UUID, LerpingBossEvent> getEvents() {
        throw new IllegalStateException("This class is only supported as injection class");
    }
}
