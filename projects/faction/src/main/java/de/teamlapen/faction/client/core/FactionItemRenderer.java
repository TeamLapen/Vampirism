package de.teamlapen.faction.client.core;

import de.teamlapen.faction.client.color.tint.RefinementTint;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

public class FactionItemRenderer {

    public static void registerColors(RegisterColorHandlersEvent. ItemTintSources event) {
        event.register(RefinementTint.ID, RefinementTint.CODEC);
    }
}
