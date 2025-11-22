package de.teamlapen.factions.client;

import de.teamlapen.factions.api.util.REFERENCE;
import de.teamlapen.factions.client.proxy.ClientProxy;
import de.teamlapen.factions.common.proxy.IProxy;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.jetbrains.annotations.ApiStatus;

@Mod(value = REFERENCE.MOD_ID, dist = Dist.CLIENT)
public class FactionsClientMod {

    private static ClientServices SERVICES;

    public FactionsClientMod(ModContainer container, IEventBus modBus) {
        SERVICES = new ClientServices(container);
        SERVICES.register(modBus);
    }

    public static ClientServices services() {
        return SERVICES;
    }

    @ApiStatus.Internal
    public static IProxy create() {
        return new ClientProxy();
    }

}
