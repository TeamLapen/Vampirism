package de.teamlapen.faction;

import de.teamlapen.faction.api.FactionsApi;
import de.teamlapen.faction.api.util.REFERENCE;
import de.teamlapen.faction.client.FactionsClientMod;
import de.teamlapen.faction.common.CommonServices;
import de.teamlapen.faction.common.config.FactionConfig;
import de.teamlapen.faction.common.proxy.IProxy;
import de.teamlapen.faction.server.proxy.ServerProxy;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import org.jetbrains.annotations.UnknownNullability;

@Mod(REFERENCE.MOD_ID)
public class FactionsMod {

    public static final IProxy proxy = FMLEnvironment.getDist() == Dist.CLIENT ? FactionsClientMod.create() : new ServerProxy();
    @UnknownNullability
    private static FactionConfig CONFIG;

    public FactionsMod(ModContainer container, IEventBus modBus) {
        CONFIG = new FactionConfig(container);
        CONFIG.register(modBus);
        ((CommonServices) FactionsApi.services()).register(modBus);
    }

    public static FactionConfig config() {
        return CONFIG;
    }
}
