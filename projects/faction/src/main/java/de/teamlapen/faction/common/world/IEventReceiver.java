package de.teamlapen.faction.common.world;

import net.minecraft.resources.Identifier;

public interface IEventReceiver {

    void onEvent(Identifier event);
}
