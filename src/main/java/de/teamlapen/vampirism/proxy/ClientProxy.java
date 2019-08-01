package de.teamlapen.vampirism.proxy;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.client.core.*;
import de.teamlapen.vampirism.client.gui.ModifyInventoryScreen;
import de.teamlapen.vampirism.client.gui.VampirismHUDOverlay;
import de.teamlapen.vampirism.client.render.LayerVampireEntity;
import de.teamlapen.vampirism.client.render.LayerVampirePlayerHead;
import de.teamlapen.vampirism.client.render.RenderHandler;
import de.teamlapen.vampirism.network.SkillTreePacket;
import de.teamlapen.vampirism.player.skills.ClientSkillTreeManager;
import de.teamlapen.vampirism.player.skills.SkillTree;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.lifecycle.ModLifecycleEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * Clientside Proxy
 */
@OnlyIn(Dist.CLIENT)
public class ClientProxy extends CommonProxy {
    private final static Logger LOGGER = LogManager.getLogger(ClientProxy.class);

    private VampirismHUDOverlay overlay;
    private ClientSkillTreeManager skillTreeManager = new ClientSkillTreeManager();


    @Nullable
    @Override
    public Entity getMouseOverEntity() {
        RayTraceResult r = Minecraft.getInstance().objectMouseOver;
        if (r instanceof EntityRayTraceResult) return ((EntityRayTraceResult) r).getEntity();
        return null;
    }

    @Override
    public float getRenderPartialTick() {
        return Minecraft.getInstance().getRenderPartialTicks();
    }

    @Override
    public boolean isClientPlayerNull() {
        return Minecraft.getInstance().player == null;
    }

    @Override
    public SkillTree getSkillTree(boolean client) {
        return client ? skillTreeManager.getSkillTree() : super.getSkillTree(false);
    }

    @Override
    public void renderScreenFullColor(int ticksOn, int ticksOff, int color) {
        if (overlay != null) overlay.makeRenderFullColor(ticksOn, ticksOff, color);
    }

    @Override
    public boolean isPlayerThePlayer(PlayerEntity player) {
        return Minecraft.getInstance().player.equals(player);
    }

    @Override
    public void onInitStep(Step step, ModLifecycleEvent event) {
        super.onInitStep(step, event);
        switch (step) {
            case CLIENT_SETUP:
                ModEntitiesRender.registerEntityRenderer();
                ModKeys.register();
                registerSubscriptions();
                break;
            case LOAD_COMPLETE:
                ModBlocksRender.registerColors();
                ModItemsRender.registerColors();
                ModBlocksRender.register();
                ModParticleFactories.registerFactories();
                ModScreens.registerScreens();
                skillTreeManager.init();
                registerVampireEntityOverlays();
                break;
        }
    }

    private void registerVampireEntityOverlay(EntityRendererManager manager, Class<? extends CreatureEntity> type, ResourceLocation loc) {
        EntityRenderer render = manager.getRenderer(type);
        if (render == null) {
            LOGGER.error("Did not find renderer for {}", type);
            return;
        }
        if (!(render instanceof LivingRenderer)) {
            LOGGER.error("Renderer ({}) for {} does not extend RenderLivingEntity", type, render);
            return;
        }
        LivingRenderer rendererLiving = (LivingRenderer) render;
        rendererLiving.addLayer(new LayerVampireEntity(rendererLiving, loc, true));
    }

    private void registerVampireEntityOverlays() {
        EntityRendererManager manager = Minecraft.getInstance().getRenderManager();
        registerVampirePlayerHead(manager);
        for (Map.Entry<Class<? extends CreatureEntity>, String> entry : VampirismAPI.entityRegistry().getConvertibleOverlay().entrySet()) {
            registerVampireEntityOverlay(manager, entry.getKey(), new ResourceLocation(entry.getValue()));
        }
    }

    private void registerVampirePlayerHead(EntityRendererManager manager) {
        for (PlayerRenderer renderPlayer : manager.getSkinMap().values()) {
            renderPlayer.addLayer(new LayerVampirePlayerHead(renderPlayer));
        }
    }

    @Override
    public void handleSkillTree(SkillTreePacket msg) {
        skillTreeManager.loadUpdate(msg);
    }

    private void registerSubscriptions() {
        overlay = new VampirismHUDOverlay(Minecraft.getInstance());
        MinecraftForge.EVENT_BUS.register(overlay);
        MinecraftForge.EVENT_BUS.register(new RenderHandler(Minecraft.getInstance()));
        MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
        MinecraftForge.EVENT_BUS.register(new ModifyInventoryScreen());
    }
}
