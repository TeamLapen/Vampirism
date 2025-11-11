package de.teamlapen.vampirism.common.util;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.common.NeoForge;

public abstract class Services {

    private final ModContainer container;

    public Services(ModContainer container) {
        this.container = container;
    }

    public ModContainer container() {
        return this.container;
    }

    protected void registerModBus(IEventBus bus) {
    }

    protected void registerGameBus(IEventBus bus) {
    }

    public void register(IEventBus bus) {
        this.registerModBus(bus);
        this.registerGameBus(NeoForge.EVENT_BUS);
    }

}
