package de.teamlapen.vampirism.common.world.dimensions.velmorra;

import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

public class DimensionRemoveEvent extends Event implements ICancellableEvent
{
    private final ServerLevel level;

    public DimensionRemoveEvent(ServerLevel level)
    {
        this.level = level;
    }

    /**
     * @return The level that is about to be unregistered by Infiniverse.
     */
    public ServerLevel getLevel()
    {
        return this.level;
    }
}