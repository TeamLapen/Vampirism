package de.teamlapen.faction;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.ApiStatus;

public class Services {

    @ApiStatus.OverrideOnly
    protected void registerModBus(IEventBus bus) {
    }

    @ApiStatus.OverrideOnly
    protected void registerGameBus(@SuppressWarnings("SameParameterValue") IEventBus bus) {
    }

    public void register(IEventBus bus) {
        this.registerModBus(bus);
        this.registerGameBus(NeoForge.EVENT_BUS);
    }
}
