package de.teamlapen.faction;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.ApiStatus;

public class Services {

    private final ModContainer container;

    public Services(ModContainer container) {
        this.container = container;
    }

    public ModContainer container() {
        return this.container;
    }

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
