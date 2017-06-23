package de.teamlapen.vampirism.client.core;


import de.teamlapen.lib.lib.util.IInitListener;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.event.FMLStateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Handle client side registration events as well as a few dependent registrations
 */
@SideOnly(Side.CLIENT)
public class RegistryManagerClient implements IInitListener {

    @Override
    public void onInitStep(Step step, FMLStateEvent event) {
        switch (step) {
            case INIT:
                ModBlocksRender.registerColors();
                ModItemsRender.registerColors();
            case POST_INIT:

        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onRegisterModels(ModelRegistryEvent event) {
        ModBlocksRender.register();
        ModItemsRender.register();
        ModEntitiesRender.registerEntityRenderer();
    }
}
