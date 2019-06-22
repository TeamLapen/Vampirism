package de.teamlapen.vampirism.client.core;


import de.teamlapen.lib.lib.util.IInitListener;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.ModLifecycleEvent;

/**
 * Handle client side registration events as well as a few dependent registrations
 */
@OnlyIn(Dist.CLIENT)
public class RegistryManagerClient implements IInitListener {

    @Override
    public void onInitStep(Step step, ModLifecycleEvent event) {
        if (step == Step.COMMON_SETUP) {
            ModBlocksRender.registerColors();
            ModItemsRender.registerColors();
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onRegisterModels(ModelRegistryEvent event) {
        ModEntitiesRender.registerEntityRenderer();
    }
}
