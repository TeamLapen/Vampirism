package de.teamlapen.vampirism.proxy;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.general.BloodConversionRegistry;
import de.teamlapen.vampirism.client.core.*;
import de.teamlapen.vampirism.client.gui.*;
import de.teamlapen.vampirism.client.render.LayerVampireEntity;
import de.teamlapen.vampirism.client.render.LayerVampirePlayerHead;
import de.teamlapen.vampirism.client.render.RenderHandler;
import de.teamlapen.vampirism.entity.converted.VampirismEntityRegistry;
import de.teamlapen.vampirism.network.BloodValuePacket;
import de.teamlapen.vampirism.network.OpenVampireBookPacket;
import de.teamlapen.vampirism.network.SkillTreePacket;
import de.teamlapen.vampirism.player.skills.ClientSkillTreeManager;
import de.teamlapen.vampirism.player.skills.SkillTree;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ReadBookScreen;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
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

    @Override
    public void displayNameSwordScreen(ItemStack stack) {
        Minecraft.getInstance().displayGuiScreen(new NameSwordScreen(stack));
    }

    @Override
    public void displayRevertBackScreen() {
        Minecraft.getInstance().displayGuiScreen(new RevertBackScreen());
    }

    @Nullable
    @Override
    public PlayerEntity getClientPlayer() {
        return Minecraft.getInstance().player;
    }

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
    public SkillTree getSkillTree(boolean client) {
        return client ? skillTreeManager.getSkillTree() : super.getSkillTree(false);
    }

    @Override
    public void handleBloodValuePacket(BloodValuePacket msg) {
        ((VampirismEntityRegistry) VampirismAPI.entityRegistry()).applyNewResources(msg.getValues()[0].getFirst(), msg.getValues()[0].getSecond());
        BloodConversionRegistry.applyNewItemResources(msg.getValues()[1].getFirst(), msg.getValues()[1].getSecond());
        BloodConversionRegistry.applyNewFluidResources(msg.getValues()[2].getFirst(), msg.getValues()[2].getSecond());
    }

    @Override
    public void handleSkillTreePacket(SkillTreePacket msg) {
        skillTreeManager.loadUpdate(msg);
    }

    @Override
    public void handleVampireBookPacket(OpenVampireBookPacket msg) {
        Minecraft.getInstance().displayGuiScreen(new ReadBookScreen(new ReadBookScreen.WrittenBookInfo(msg.itemStack)));
    }

    @Override
    public void onInitStep(Step step, ModLifecycleEvent event) {
        super.onInitStep(step, event);
        switch (step) {
            case CLIENT_SETUP:
                ModEntitiesRender.registerEntityRenderer();
                ModKeys.register();
                registerSubscriptions();
                SelectActionScreen.loadActionOrder();
                ModBlocksRender.register();
                break;
            case LOAD_COMPLETE:
                ModBlocksRender.registerColors();
                ModItemsRender.registerColors();
                ModParticleFactories.registerFactories();
                ModScreens.registerScreens();
                skillTreeManager.init();
                registerVampireEntityOverlays();
                break;
            default:
                break;
        }
    }

    @Override
    public void renderScreenFullColor(int ticksOn, int ticksOff, int color) {
        if (overlay != null) overlay.makeRenderFullColor(ticksOn, ticksOff, color);
    }

    private void registerSubscriptions() {
        overlay = new VampirismHUDOverlay(Minecraft.getInstance());
        MinecraftForge.EVENT_BUS.register(overlay);
        MinecraftForge.EVENT_BUS.register(new RenderHandler(Minecraft.getInstance()));
        MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
        MinecraftForge.EVENT_BUS.register(new ModifyInventoryScreen());
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
        for (Map.Entry<Class<? extends CreatureEntity>, ResourceLocation> entry : VampirismAPI.entityRegistry().getConvertibleOverlay().entrySet()) {
            registerVampireEntityOverlay(manager, entry.getKey(), entry.getValue());
        }
    }

    private void registerVampirePlayerHead(EntityRendererManager manager) {
        for (PlayerRenderer renderPlayer : manager.getSkinMap().values()) {
            renderPlayer.addLayer(new LayerVampirePlayerHead(renderPlayer));
        }
    }
}
