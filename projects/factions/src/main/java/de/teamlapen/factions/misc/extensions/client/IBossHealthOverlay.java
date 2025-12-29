package de.teamlapen.factions.misc.extensions.client;

import net.minecraft.client.gui.components.LerpingBossEvent;

import java.util.Map;
import java.util.UUID;

public interface IBossHealthOverlay {

    Map<UUID, LerpingBossEvent> getEvents();
}
