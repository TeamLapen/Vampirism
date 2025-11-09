package de.teamlapen.vampirism.misc.injection.client;

import de.teamlapen.vampirism.misc.extension.client.IBossHealthOverlay;
import net.minecraft.client.gui.components.LerpingBossEvent;

import java.util.Map;
import java.util.UUID;

public interface IBossHealthOverlayMock extends IBossHealthOverlay {
    @Override
    default Map<UUID, LerpingBossEvent> getEvents() {
        return Map.of();
    }
}
