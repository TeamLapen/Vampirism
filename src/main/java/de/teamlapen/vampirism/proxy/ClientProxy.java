package de.teamlapen.vampirism.proxy;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.general.BloodConversionRegistry;
import de.teamlapen.vampirism.blocks.CoffinBlock;
import de.teamlapen.vampirism.blocks.TentBlock;
import de.teamlapen.vampirism.client.core.*;
import de.teamlapen.vampirism.client.gui.*;
import de.teamlapen.vampirism.client.render.RenderHandler;
import de.teamlapen.vampirism.client.render.layers.VampireEntityLayer;
import de.teamlapen.vampirism.client.render.layers.VampirePlayerHeadLayer;
import de.teamlapen.vampirism.entity.converted.VampirismEntityRegistry;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.inventory.container.TaskMasterContainer;
import de.teamlapen.vampirism.network.*;
import de.teamlapen.vampirism.player.skills.ClientSkillTreeManager;
import de.teamlapen.vampirism.player.skills.SkillTree;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ReadBookScreen;
import net.minecraft.client.gui.screen.SleepInMultiplayerScreen;
import net.minecraft.client.particle.DiggingParticle;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.ModLifecycleEvent;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

import static de.teamlapen.vampirism.blocks.TentBlock.FACING;
import static de.teamlapen.vampirism.blocks.TentBlock.POSITION;

/**
 * Clientside Proxy
 */
@OnlyIn(Dist.CLIENT)
public class ClientProxy extends CommonProxy {
    private final static Logger LOGGER = LogManager.getLogger(ClientProxy.class);

    private VampirismHUDOverlay overlay;
    private final ClientSkillTreeManager skillTreeManager = new ClientSkillTreeManager();

    public ClientProxy() {
        RenderHandler renderHandler = new RenderHandler(Minecraft.getInstance());
        MinecraftForge.EVENT_BUS.register(renderHandler);
        //Minecraft.instance is null during runData.
        //noinspection ConstantConditions
        if (Minecraft.getInstance() != null)
            ((IReloadableResourceManager) Minecraft.getInstance().getResourceManager()).addReloadListener(renderHandler); // Must be added before initial resource manager load
    }

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
    public void handleSleepClient(PlayerEntity player) {
        if (player.isSleeping()) {
            player.getBedPosition().ifPresent(pos -> {
                if (player.world.getBlockState(pos).getBlock() instanceof TentBlock) {
                    if (Minecraft.getInstance().currentScreen instanceof SleepInMultiplayerScreen && !(Minecraft.getInstance().currentScreen instanceof SleepInMultiplayerModScreen)) {
                        Minecraft.getInstance().displayGuiScreen(new SleepInMultiplayerModScreen("text.vampirism.tent.stop_sleeping"));
                    }
                    TentBlock.setTentSleepPosition(player, pos, player.world.getBlockState(pos).get(POSITION), player.world.getBlockState(pos).get(FACING));
                } else if (player.world.getBlockState(pos).getBlock() instanceof CoffinBlock) {
                    if (Minecraft.getInstance().currentScreen instanceof SleepInMultiplayerScreen && !(Minecraft.getInstance().currentScreen instanceof SleepInMultiplayerModScreen)) {
                        Minecraft.getInstance().displayGuiScreen(new SleepInMultiplayerModScreen("text.vampirism.coffin.stop_sleeping"));
                    }
                }
            });
        }
    }

    @Override
    public void onInitStep(Step step, ModLifecycleEvent event) {
        super.onInitStep(step, event);
        switch (step) {
            case CLIENT_SETUP:
                ModEntitiesRender.registerEntityRenderer(((FMLClientSetupEvent) event).getMinecraftSupplier());
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
        MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
        MinecraftForge.EVENT_BUS.register(new ScreenEventHandler());
    }

    private void registerVampireEntityOverlay(EntityRendererManager manager, EntityType<? extends CreatureEntity> type, ResourceLocation loc) {
        EntityRenderer<?> render = manager.renderers.get(type);
        if (render == null) {
            LOGGER.error("Did not find renderer for {}", type);
            return;
        }
        if (!(render instanceof LivingRenderer)) {
            LOGGER.error("Renderer ({}) for {} does not extend RenderLivingEntity", type, render);
            return;
        }
        LivingRenderer rendererLiving = (LivingRenderer) render;
        rendererLiving.addLayer(new VampireEntityLayer(rendererLiving, loc, true));
    }

    private void registerVampireEntityOverlays() {
        EntityRendererManager manager = Minecraft.getInstance().getRenderManager();
        registerVampirePlayerHead(manager);
        for (Map.Entry<EntityType<? extends CreatureEntity>, ResourceLocation> entry : VampirismAPI.entityRegistry().getConvertibleOverlay().entrySet()) {
            registerVampireEntityOverlay(manager, entry.getKey(), entry.getValue());
        }
    }

    private void registerVampirePlayerHead(EntityRendererManager manager) {
        for (PlayerRenderer renderPlayer : manager.getSkinMap().values()) {
            renderPlayer.addLayer(new VampirePlayerHeadLayer(renderPlayer));
        }
    }

    @Override
    public void handlePlayEventPacket(PlayEventPacket msg) {
        switch (msg.type) {
            case 1:
                spawnParticles(Minecraft.getInstance().world, msg.pos, Block.getStateById(msg.stateId));
                break;
        }
    }

    /**
     * copied from {@link net.minecraft.client.particle.ParticleManager#addBlockDestroyEffects(net.minecraft.util.math.BlockPos, net.minecraft.block.BlockState)} but which much lesser particles
     */
    private void spawnParticles(World world, BlockPos pos, BlockState state) {
        VoxelShape voxelshape = state.getShape(world, pos);
        voxelshape.forEachBox((p_199284_3_, p_199284_5_, p_199284_7_, p_199284_9_, p_199284_11_, p_199284_13_) -> {
            double d1 = Math.min(1.0D, p_199284_9_ - p_199284_3_);
            double d2 = Math.min(1.0D, p_199284_11_ - p_199284_5_);
            double d3 = Math.min(1.0D, p_199284_13_ - p_199284_7_);
            int i = Math.max(2, MathHelper.ceil(d1 / 0.25D));
            int j = Math.max(2, MathHelper.ceil(d2 / 0.25D));
            int k = Math.max(2, MathHelper.ceil(d3 / 0.25D));

            for (int l = 0; l < i / 2; ++l) {
                for (int i1 = 0; i1 < j / 2; ++i1) {
                    for (int j1 = 0; j1 < k / 2; ++j1) {
                        double d4 = ((double) l + 0.5D) / (double) i;
                        double d5 = ((double) i1 + 0.5D) / (double) j;
                        double d6 = ((double) j1 + 0.5D) / (double) k;
                        double d7 = d4 * d1 + p_199284_3_;
                        double d8 = d5 * d2 + p_199284_5_;
                        double d9 = d6 * d3 + p_199284_7_;
                        Minecraft.getInstance().particles.addEffect((new DiggingParticle(world, (double) pos.getX() + d7, (double) pos.getY() + d8, (double) pos.getZ() + d9, d4 - 0.5D, d5 - 0.5D, d6 - 0.5D, state)).setBlockPos(pos));
                    }
                }
            }

        });
    }

    @Override
    public void handleRequestMinionSelect(RequestMinionSelectPacket.Action action, List<Pair<Integer, ITextComponent>> minions) {
        Minecraft.getInstance().displayGuiScreen(new SelectMinionScreen(action, minions));
    }

    @Override
    public void handleTaskStatusPacket(TaskStatusPacket msg) {
        Container container = Minecraft.getInstance().player.openContainer;
        if (msg.containerId == container.windowId && container instanceof TaskMasterContainer) {
            ((TaskMasterContainer) container).setPossibleTasks(msg.possibleTasks);
        }
        FactionPlayerHandler.getOpt(Minecraft.getInstance().player).ifPresent(factionPlayerHandler -> factionPlayerHandler.getCurrentFactionPlayer().ifPresent(factionPlayer -> factionPlayer.getTaskManager().setCompletedTasks(msg.completedTasks)));
    }

}
