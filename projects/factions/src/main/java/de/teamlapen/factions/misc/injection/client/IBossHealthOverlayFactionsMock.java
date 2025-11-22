package de.teamlapen.factions.misc.injection.client;

import de.teamlapen.factions.misc.extensions.client.IBossHealthOverlay;
import net.minecraft.client.gui.components.LerpingBossEvent;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;
import java.util.UUID;

@ApiStatus.Internal
public interface IBossHealthOverlayFactionsMock extends IBossHealthOverlay {
    @Override
    default Map<UUID, LerpingBossEvent> getEvents() {
        return Map.of();
    }
}
