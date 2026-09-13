package de.teamlapen.faction.api.event;

import de.teamlapen.faction.api.client.ItemBarProvider;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;

public class RegisterItemBarsEvent extends Event implements IModBusEvent {

    private final Map<Identifier, ItemBarProvider> providers;

    @ApiStatus.Internal
    public RegisterItemBarsEvent(Map<Identifier, ItemBarProvider> providers) {
        this.providers = providers;
    }

    public void register(Identifier id, ItemBarProvider provider) {
        if (this.providers.putIfAbsent(id, provider) != null) {
            throw new IllegalArgumentException("Duplicate item bar provider " + id);
        }
    }
}
