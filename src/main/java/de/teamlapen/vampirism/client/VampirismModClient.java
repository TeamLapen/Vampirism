package de.teamlapen.vampirism.client;

import de.teamlapen.lib.lib.util.IInitListener;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.client.core.ClientRegistryHandler;
import de.teamlapen.vampirism.core.RegistryManager;
import de.teamlapen.vampirism.proxy.ClientProxy;
import de.teamlapen.vampirism.proxy.IProxy;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public class VampirismModClient {

    private final RegistryManager registryManager;
    private final IEventBus modEventBus;

    public VampirismModClient(IEventBus modEventBus, RegistryManager registryManager) {
        this.modEventBus = modEventBus;
        this.registryManager = registryManager;
        ClientRegistryHandler.init(modEventBus);
    }

    @SubscribeEvent
    public void setupClient(@NotNull FMLClientSetupEvent event) {
        VampirismMod.proxy.onInitStep(IInitListener.Step.CLIENT_SETUP, event);
        registryManager.onInitStep(IInitListener.Step.CLIENT_SETUP, event);
    }

    public static IProxy getProxy() {
        return new ClientProxy();
    }
}
