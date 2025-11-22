package de.teamlapen.factions.client.core;

import de.teamlapen.factions.client.color.tint.RefinementTint;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import org.jetbrains.annotations.NotNull;

public class ModItemRenderer {

    public static void registerColors(RegisterColorHandlersEvent. ItemTintSources event) {
        event.register(RefinementTint.ID, RefinementTint.CODEC);
    }
}
