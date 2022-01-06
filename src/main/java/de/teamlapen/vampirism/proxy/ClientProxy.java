package de.teamlapen.vampirism.proxy;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.client.VIngameOverlays;
import de.teamlapen.vampirism.api.general.BloodConversionRegistry;
import de.teamlapen.vampirism.blockentity.GarlicDiffuserBlockEntity;
import de.teamlapen.vampirism.blocks.CoffinBlock;
import de.teamlapen.vampirism.blocks.TentBlock;
import de.teamlapen.vampirism.client.core.*;
import de.teamlapen.vampirism.client.gui.*;
import de.teamlapen.vampirism.client.gui.overlay.*;
import de.teamlapen.vampirism.client.render.RenderHandler;
import de.teamlapen.vampirism.client.render.VampirismBlockEntityWitoutLevelRenderer;
import de.teamlapen.vampirism.entity.converted.VampirismEntityRegistry;
import de.teamlapen.vampirism.inventory.container.TaskBoardContainer;
import de.teamlapen.vampirism.inventory.container.VampirismContainer;
import de.teamlapen.vampirism.network.*;
import de.teamlapen.vampirism.player.skills.ClientSkillTreeManager;
import de.teamlapen.vampirism.player.skills.SkillTree;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.InBedChatScreen;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.TerrainParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.lifecycle.ParallelDispatchEvent;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

import static de.teamlapen.vampirism.blocks.TentBlock.FACING;
import static de.teamlapen.vampirism.blocks.TentBlock.POSITION;

/**
 * Clientside Proxy
 */
@OnlyIn(Dist.CLIENT)
public class ClientProxy extends CommonProxy {
    private final static Logger LOGGER = LogManager.getLogger(ClientProxy.class);
    private final ClientSkillTreeManager skillTreeManager = new ClientSkillTreeManager();
    private VampirismHUDOverlay overlay;
    private CustomBossEventOverlay bossInfoOverlay;
    private VampirismBlockEntityWitoutLevelRenderer itemStackBESR;

    public ClientProxy() {
        RenderHandler renderHandler = new RenderHandler(Minecraft.getInstance());
        MinecraftForge.EVENT_BUS.register(renderHandler);
        //Minecraft.instance is null during runData.
        //noinspection ConstantConditions
        if (Minecraft.getInstance() != null)
            ((ReloadableResourceManager) Minecraft.getInstance().getResourceManager()).registerReloadListener(renderHandler); // Must be added before initial resource manager load
    }

    public void clearBossBarOverlay() {
        this.bossInfoOverlay.clear();
    }

    @Override
    public void displayGarlicBeaconScreen(GarlicDiffuserBlockEntity tile, Component title) {
        Minecraft.getInstance().setScreen(new GarlicDiffuserScreen(tile, title));
    }

    @Override
    public void displayNameSwordScreen(ItemStack stack) {
        Minecraft.getInstance().setScreen(new NameSwordScreen(stack));
    }

    @Override
    public void displayRevertBackScreen() {
        Minecraft.getInstance().setScreen(new RevertBackScreen());
    }

    @Nullable
    @Override
    public Player getClientPlayer() {
        return Minecraft.getInstance().player;
    }

    public VampirismBlockEntityWitoutLevelRenderer getItemStackBESR() {
        return itemStackBESR;
    }

    @Nullable
    @Override
    public Entity getMouseOverEntity() {
        HitResult r = Minecraft.getInstance().hitResult;
        if (r instanceof EntityHitResult) return ((EntityHitResult) r).getEntity();
        return null;
    }

    @Override
    public float getRenderPartialTick() {
        return Minecraft.getInstance().getFrameTime();
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
    public void handlePlayEventPacket(PlayEventPacket msg) {
        if (msg.type() == 1) {
            spawnParticles(Minecraft.getInstance().level, msg.pos(), Block.stateById(msg.stateId()));
        }
    }

    @Override
    public void handleRequestMinionSelect(RequestMinionSelectPacket.Action action, List<Pair<Integer, Component>> minions) {
        Minecraft.getInstance().setScreen(new SelectMinionScreen(action, minions));
    }

    @Override
    public void handleSkillTreePacket(SkillTreePacket msg) {
        skillTreeManager.loadUpdate(msg);
    }

    @Override
    public void handleSleepClient(Player player) {
        if (player.isSleeping()) {
            player.getSleepingPos().ifPresent(pos -> {
                if (player.level.getBlockState(pos).getBlock() instanceof TentBlock) {
                    if (Minecraft.getInstance().screen instanceof InBedChatScreen && !(Minecraft.getInstance().screen instanceof SleepInMultiplayerModScreen)) {
                        Minecraft.getInstance().setScreen(new SleepInMultiplayerModScreen("text.vampirism.tent.stop_sleeping"));
                    }
                    TentBlock.setTentSleepPosition(player, pos, player.level.getBlockState(pos).getValue(POSITION), player.level.getBlockState(pos).getValue(FACING));
                } else if (player.level.getBlockState(pos).getBlock() instanceof CoffinBlock) {
                    if (Minecraft.getInstance().screen instanceof InBedChatScreen && !(Minecraft.getInstance().screen instanceof SleepInMultiplayerModScreen)) {
                        Minecraft.getInstance().setScreen(new SleepInMultiplayerModScreen("text.vampirism.coffin.stop_sleeping"));
                    }
                    CoffinBlock.setCoffinSleepPosition(player,pos);
                }
            });
        }
    }

    @Override
    public void handleTaskPacket(TaskPacket msg) {
        AbstractContainerMenu container = Minecraft.getInstance().player.containerMenu;
        if (msg.containerId() == container.containerId && container instanceof VampirismContainer) {
            ((VampirismContainer) container).init(msg.taskWrappers(), msg.completableTasks(), msg.completedRequirements());
        }
    }

    @Override
    public void handleTaskStatusPacket(TaskStatusPacket msg) {
        AbstractContainerMenu container = Objects.requireNonNull(Minecraft.getInstance().player).containerMenu;
        if (msg.containerId() == container.containerId && container instanceof TaskBoardContainer) {
            ((TaskBoardContainer) container).init(msg.available(), msg.completableTasks(), msg.completedRequirements(), msg.taskBoardId());
        }
    }

    @Override
    public void handleUpdateMultiBossInfoPacket(MultiBossEventPacket msg) {
        this.bossInfoOverlay.read(msg);
    }

    @Override
    public void handleVampireBookPacket(OpenVampireBookPacket msg) {
        Minecraft.getInstance().setScreen(new BookViewScreen(new BookViewScreen.WrittenBookAccess(msg.itemStack())));
    }

    @Override
    public void onInitStep(Step step, ParallelDispatchEvent event) {
        super.onInitStep(step, event);
        switch (step) {
            case CLIENT_SETUP -> {
                this.overlay = new VampirismHUDOverlay(Minecraft.getInstance());
                ModKeys.register();
                registerSubscriptions();
                ActionSelectScreen.loadActionOrder();
                ModBlocksRender.register();
            }
            case LOAD_COMPLETE -> {
                event.enqueueWork(ModItemsRender::registerItemModelPropertyUnsafe);
                event.enqueueWork(ModScreens::registerScreensUnsafe);
                skillTreeManager.init();
                itemStackBESR = new VampirismBlockEntityWitoutLevelRenderer(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
            }
            default -> {
            }
        }
    }

    @Override
    public void renderScreenFullColor(int ticksOn, int ticksOff, int color) {
        if (overlay != null) overlay.makeRenderFullColor(ticksOn, ticksOff, color);
    }

    @Override
    public void resetSkillScreenCache() {
    }

    @Override
    public void showDBNOScreen(Player playerEntity, @Nullable Component deathMessage) {
        if (playerEntity == Minecraft.getInstance().player && !playerEntity.isDeadOrDying()) {
            Minecraft.getInstance().setScreen(new DBNOScreen(deathMessage));
        }
    }

    private void registerSubscriptions() {
        MinecraftForge.EVENT_BUS.register(this.overlay);
        MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
        MinecraftForge.EVENT_BUS.register(new ScreenEventHandler());
    }

    @Override
    public void setupAPIClient() {
        VIngameOverlays.FACTION_RAID_BAR_ELEMENT = this.bossInfoOverlay = new CustomBossEventOverlay();
        VIngameOverlays.BLOOD_BAR_ELEMENT = new BloodBarOverlay();
        VIngameOverlays.FACTION_LEVEL_ELEMENT = new FactionLevelOverlay();
        VIngameOverlays.ACTION_COOLDOWN_ELEMENT = new ActionCooldownOverlay();
        VIngameOverlays.ACTION_DURATION_ELEMENT = new ActionDurationOverlay();
    }

    /**
     * copied but which much lesser particles
     */
    private void spawnParticles(Level world, BlockPos pos, BlockState state) {
        if (!(world instanceof ClientLevel)) return;
        VoxelShape voxelshape = state.getShape(world, pos);
        voxelshape.forAllBoxes((p_199284_3_, p_199284_5_, p_199284_7_, p_199284_9_, p_199284_11_, p_199284_13_) -> {
            double d1 = Math.min(1.0D, p_199284_9_ - p_199284_3_);
            double d2 = Math.min(1.0D, p_199284_11_ - p_199284_5_);
            double d3 = Math.min(1.0D, p_199284_13_ - p_199284_7_);
            int i = Math.max(2, Mth.ceil(d1 / 0.25D));
            int j = Math.max(2, Mth.ceil(d2 / 0.25D));
            int k = Math.max(2, Mth.ceil(d3 / 0.25D));

            for (int l = 0; l < i / 2; ++l) {
                for (int i1 = 0; i1 < j / 2; ++i1) {
                    for (int j1 = 0; j1 < k / 2; ++j1) {
                        double d4 = ((double) l + 0.5D) / (double) i;
                        double d5 = ((double) i1 + 0.5D) / (double) j;
                        double d6 = ((double) j1 + 0.5D) / (double) k;
                        double d7 = d4 * d1 + p_199284_3_;
                        double d8 = d5 * d2 + p_199284_5_;
                        double d9 = d6 * d3 + p_199284_7_;
                        Minecraft.getInstance().particleEngine.add((new TerrainParticle((ClientLevel) world, (double) pos.getX() + d7, (double) pos.getY() + d8, (double) pos.getZ() + d9, d4 - 0.5D, d5 - 0.5D, d6 - 0.5D, state)));
                    }
                }
            }

        });
    }
}
