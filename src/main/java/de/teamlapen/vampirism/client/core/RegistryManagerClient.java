package de.teamlapen.vampirism.client.core;


import de.teamlapen.lib.lib.util.IInitListener;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.event.lifecycle.ModLifecycleEvent;

/**
 * Handle client side registration events as well as a few dependent registrations TODO maybe move to proxy
 */
@OnlyIn(Dist.CLIENT)
public class RegistryManagerClient implements IInitListener {

    @Override
    public void onInitStep(Step step, ModLifecycleEvent event) {

    }
}
