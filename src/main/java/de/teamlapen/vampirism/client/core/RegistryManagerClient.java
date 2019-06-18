package de.teamlapen.vampirism.client.core;


import de.teamlapen.lib.lib.util.IInitListener;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.event.FMLStateEvent;

/**
 * Handle client side registration events as well as a few dependent registrations
 */
@OnlyIn(Dist.CLIENT)
public class RegistryManagerClient implements IInitListener {//TODO Mod Loading process @maxanier

    @Override
    public void onInitStep(Step step, FMLStateEvent event) {
        switch (step) {
            case INIT:
                ModBlocksRender.registerColors();
                ModItemsRender.registerColors();
            case POST_INIT:

        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onRegisterModels(ModelRegistryEvent event) {
        ModEntitiesRender.registerEntityRenderer();
    }
}
