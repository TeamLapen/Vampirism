package de.teamlapen.vampirism.proxy;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.client.core.ClientEventHandler;
import de.teamlapen.vampirism.client.core.ModKeys;
import de.teamlapen.vampirism.client.gui.VampirismHUDOverlay;
import de.teamlapen.vampirism.client.render.LayerVampireEntity;
import de.teamlapen.vampirism.client.render.LayerVampirePlayerHead;
import de.teamlapen.vampirism.client.render.RenderHandler;
import de.teamlapen.vampirism.core.RegistryManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLStateEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Map;

/**
 * Clientside Proxy
 */
@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {
    private final static String TAG = "ClientProxy";

    private VampirismHUDOverlay overlay;

    public ClientProxy() {
        RegistryManager.setupClientRegistryManager();
    }

    @Override
    public float getRenderPartialTick() {
        return Minecraft.getMinecraft().getRenderPartialTicks();
    }

    @Override
    public boolean isClientPlayerNull() {
        return Minecraft.getMinecraft().player == null;
    }

    @Override
    public boolean isPlayerThePlayer(EntityPlayer player) {
        return Minecraft.getMinecraft().player.equals(player);
    }

    @Override
    public void onInitStep(Step step, FMLStateEvent event) {
        super.onInitStep(step, event);
        RegistryManager.getRegistryManagerClient().onInitStep(step, event);
        switch (step) {
            case PRE_INIT:
                ModKeys.register();
                registerSubscriptions();
                break;
            case INIT:

                break;
            case POST_INIT:
                registerVampireEntityOverlays();
                break;
        }
    }

    @Override
    public void renderScreenFullColor(int ticksOn, int ticksOff, int color) {
        if (overlay != null) overlay.makeRenderFullColor(ticksOn, ticksOff, color);
    }

    private void registerSubscriptions() {
        overlay = new VampirismHUDOverlay(Minecraft.getMinecraft());
        MinecraftForge.EVENT_BUS.register(overlay);
        MinecraftForge.EVENT_BUS.register(new RenderHandler(Minecraft.getMinecraft()));
        MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
    }

    private void registerVampireEntityOverlay(RenderManager manager, Class<? extends EntityCreature> clazz, ResourceLocation loc) {
        Render render = manager.getEntityClassRenderObject(clazz);
        if (render == null) {
            VampirismMod.log.e(TAG, "Did not find renderer for %s", clazz);
            return;
        }
        if (!(render instanceof RenderLivingBase)) {
            VampirismMod.log.e(TAG, "Renderer (%s) for %s does not extend RenderLivingEntity", clazz, render);
            return;
        }
        RenderLivingBase rendererLiving = (RenderLivingBase) render;
        rendererLiving.addLayer(new LayerVampireEntity(rendererLiving, loc, true));
    }

    private void registerVampireEntityOverlays() {
        RenderManager manager = Minecraft.getMinecraft().getRenderManager();
        registerVampirePlayerHead(manager);
        for (Map.Entry<Class<? extends EntityCreature>, String> entry : VampirismAPI.entityRegistry().getConvertibleOverlay().entrySet()) {
            registerVampireEntityOverlay(manager, entry.getKey(), new ResourceLocation(entry.getValue()));
        }
    }

    private void registerVampirePlayerHead(RenderManager manager) {
        for (RenderPlayer renderPlayer : manager.getSkinMap().values()) {
            renderPlayer.addLayer(new LayerVampirePlayerHead(renderPlayer));
        }
    }
}
