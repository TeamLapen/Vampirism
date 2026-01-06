package de.teamlapen.faction.client;

import de.teamlapen.faction.api.util.REFERENCE;
import de.teamlapen.faction.client.proxy.ClientProxy;
import de.teamlapen.faction.common.proxy.IProxy;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.UnknownNullability;

@Mod(value = REFERENCE.MOD_ID, dist = Dist.CLIENT)
public class FactionsClientMod {

    @UnknownNullability
    private static ClientServices SERVICES;

    public FactionsClientMod(ModContainer container, IEventBus modBus) {
        SERVICES = new ClientServices(container);
        SERVICES.register(modBus);

        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    public static ClientServices services() {
        return SERVICES;
    }

    @ApiStatus.Internal
    public static IProxy create() {
        return new ClientProxy();
    }

}
