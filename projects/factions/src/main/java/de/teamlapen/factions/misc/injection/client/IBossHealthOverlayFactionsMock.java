package de.teamlapen.factions.misc.injection.client;

import de.teamlapen.factions.misc.extensions.client.IBossHealthOverlay;
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
