package de.teamlapen.vampirism.proxy;

import de.teamlapen.vampirism.client.core.ModBlocksRender;
import de.teamlapen.vampirism.client.core.ModEntitiesRender;
import de.teamlapen.vampirism.client.core.ModItemsRender;
import de.teamlapen.vampirism.client.core.ModKeys;
import de.teamlapen.vampirism.client.gui.VampirismHUDOverlay;
import de.teamlapen.vampirism.client.render.LayerVampirePlayerHead;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLStateEvent;

/**
 * Clientside Proxy
 */
public class ClientProxy extends CommonProxy {

    @Override
    public void onInitStep(Step step, FMLStateEvent event) {
        super.onInitStep(step, event);
        ModBlocksRender.onInitStep(step, event);
        ModItemsRender.onInitStep(step, event);
        ModEntitiesRender.onInitStep(step, event);
        ModKeys.onInitStep(step, event);
        if (step == Step.INIT) {
            registerSubscriptions();
            registerVampireHead();
        }
    }

    private void registerSubscriptions() {
        MinecraftForge.EVENT_BUS.register(new VampirismHUDOverlay(Minecraft.getMinecraft()));
    }

    private void registerVampireHead() {
        for (RenderPlayer renderPlayer : Minecraft.getMinecraft().getRenderManager().getSkinMap().values()) {
            renderPlayer.addLayer(new LayerVampirePlayerHead(renderPlayer));
        }
    }


}
