package de.teamlapen.vampirism.client.renderer;

import de.teamlapen.factions.client.IMinecraftAccessor;
import de.teamlapen.factions.common.config.FactionConfig;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.util.VampirismEventFactory;
import de.teamlapen.vampirism.client.renderer.entities.layers.ConvertedVampireEntityLayer;
import de.teamlapen.vampirism.client.renderer.entities.state.IConvertedOverlayRenderState;
import de.teamlapen.vampirism.client.renderer.entities.state.IVampirismRenderState;
import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.core.ModRefinements;
import de.teamlapen.vampirism.common.util.Helper;
import de.teamlapen.vampirism.common.world.blocks.CoffinBlock;
import de.teamlapen.vampirism.common.world.entity.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.common.world.items.CrucifixItem;
import de.teamlapen.vampirism.misc.extension.client.ILivingEntityRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.debug.DebugScreenEntries;
import net.minecraft.client.gui.components.debug.DebugScreenEntryStatus;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.event.level.LevelEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

/**
 * Handle most general rendering related stuff
 */
@SuppressWarnings("unused")
public class RenderHandler implements IMinecraftAccessor {
    private final int VAMPIRE_BIOME_FADE_TICKS = 60;
    private final Logger LOGGER = LogManager.getLogger();

    private int vampireBiomeTicks = 0;
    /**
     * If inside a foggy area.
     * Only updated every n ticks
     */
    private boolean insideFog = false;

    private float vampireBiomeFogDistanceMultiplier = 1;

    @SubscribeEvent
    public void onCameraSetup(ViewportEvent.@NotNull ComputeCameraAngles event) {
        if (FactionConfig.helper().preventRenderingDebugBoundingBoxes()) {
            DebugScreenEntryStatus status = this.mc().debugEntries.getStatus(DebugScreenEntries.ENTITY_HITBOXES);
            if (status != DebugScreenEntryStatus.NEVER) {
                this.mc().debugEntries.setStatus(DebugScreenEntries.ENTITY_HITBOXES, DebugScreenEntryStatus.NEVER);
            }
        }
        if (event.getCamera().entity() instanceof LivingEntity && ((LivingEntity) event.getCamera().entity()).isSleeping()) {
            ((LivingEntity) event.getCamera().entity()).getSleepingPos().map(pos -> event.getCamera().entity().level().getBlockState(pos)).filter(blockState -> blockState.getBlock() instanceof CoffinBlock).ifPresent(blockState -> {
                if (blockState.getValue(CoffinBlock.VERTICAL)) {
                    event.getCamera().invokeMove(0.2f, -0.2f, 0);
                } else {
                    event.getCamera().invokeMove(0, -0.2f, 0);
                }
            });
        }
    }

    @SubscribeEvent
    public void onClientTick(ClientTickEvent.Pre event) {
        if (level() == null || player() == null || !player().isAlive()) return;
        VampirePlayer vampire = VampirePlayer.get(player());

        //Vampire biome/village fog
        if (player().tickCount % 10 == 0) {
            if ((ModConfig.client().renderVampireForestFog.get() || ModConfig.server().enforceRenderForestFog.get()) && (Helper.isEntityInArtificalVampireFogArea(player()) || Helper.isEntityInVampireBiome(player()))) {
                insideFog = true;
                vampireBiomeFogDistanceMultiplier = vampire.getLevel() > 0 ? 2 : 1;
                vampireBiomeFogDistanceMultiplier += vampire.getRefinementHandler().isRefinementEquipped(ModRefinements.VISTA) ? ModConfig.balance().vrVistaMod.get().floatValue() : 0;

                vampireBiomeFogDistanceMultiplier = VampirismEventFactory.fireVampireFogEvent(vampireBiomeFogDistanceMultiplier);

            } else {
                insideFog = false;
            }
        }
        if (insideFog) {
            if (vampireBiomeTicks < VAMPIRE_BIOME_FADE_TICKS) {
                vampireBiomeTicks++;
            }
        } else {
            if (vampireBiomeTicks > 0) {
                vampireBiomeTicks--;
            }
        }
    }

    @SubscribeEvent
    public void onRenderFog(ViewportEvent.@NotNull RenderFog event) {
        if (vampireBiomeTicks == 0) return;
        float f = ((float) VAMPIRE_BIOME_FADE_TICKS) / (float) vampireBiomeTicks / 1.5f;
        f *= vampireBiomeFogDistanceMultiplier;

        switch (event.getType()) {
            case ATMOSPHERIC -> {
                event.setNearPlaneDistance(Math.min(event.getNearPlaneDistance() * 0.75f, 6 * f));
                event.setFarPlaneDistance(Math.min(event.getFarPlaneDistance(), 50 * f));
            }
        }
    }

    @SubscribeEvent
    public void onRenderHand(@NotNull RenderHandEvent event) {
        //noinspection ConstantValue
        if (player() != null && player().isAlive() && VampirePlayer.get(player()).getSkillProperties().bat) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onRenderLivingPre(RenderLivingEvent.@NotNull Pre<Player, AvatarRenderState, PlayerModel> event) {
        if (event.getRenderState() instanceof AvatarRenderState avatarRenderState) {
            if (avatarRenderState.vampirism$hunter$isDisguised()) {
                double dist = event.getRenderState().distanceToCameraSq;
                if (dist > 64) {
                    event.setCanceled(true);
                } else if (dist > 16 && avatarRenderState.vampirism$hunter$fullHunterCoat()) {
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public void onRenderFirstPersonHand(@NotNull RenderHandEvent event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null && player.isUsingItem() && player.getUseItemRemainingTicks() > 0 && event.getHand() == player.getUsedItemHand()) {
            if (event.getItemStack().getItem() instanceof CrucifixItem) {
                HumanoidArm humanoidarm = event.getHand() == InteractionHand.MAIN_HAND ? player.getMainArm() : player.getMainArm().getOpposite();
                int i = humanoidarm == HumanoidArm.RIGHT ? 1 : -1;
                event.getPoseStack().translate(((float) -i * 0.56F), -0.0, -0.2F);
            }
        }
    }

    @SubscribeEvent
    public void onRenderPlayer(RenderPlayerEvent.@NotNull Pre<AbstractClientPlayer> event) {
        if (event.getRenderState() instanceof AvatarRenderState avatarRenderState) {
            if (avatarRenderState.vampirism$vampire$isDbno()) {
                event.getPoseStack().translate(1.2, 0, 0);
                PlayerModel m = event.getRenderer().getModel();
                m.rightArm.visible = false;
                m.rightSleeve.visible = false;
                m.leftArm.visible = false;
                m.leftSleeve.visible = false;
                m.rightLeg.visible = false;
                m.leftLeg.visible = false;
                m.rightPants.visible = false;
                m.leftPants.visible = false;
            } else if (avatarRenderState.vampirism$vampire$sleepingInCoffin()) {
                //Shrink player, so they fit into the coffin model
                event.getPoseStack().scale(0.8f, 0.95f, 0.8f);
            }
        }
    }

    @SubscribeEvent
    public void onWorldLoad(LevelEvent.Load event) {
        this.vampireBiomeTicks = 0;
        this.insideFog = false;
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onRenderPlayerPreHigh(RenderPlayerEvent.@NotNull Pre<AbstractClientPlayer> event) {
        var renderState = event.getRenderState();
        if (renderState.vampirism$vampire$invisible()) {
            event.setCanceled(true);
        } else if (renderState.vampirism$vampire$getBat() instanceof Bat bat) {
            event.setCanceled(true);
            float partialTicks = event.getPartialTick();

            bat.tickCount = (int) event.getRenderState().ageInTicks;
            bat.setInvisible(event.getRenderState().isInvisible);
            EntityRenderer<? super Bat, ?> renderer = mc().getEntityRenderDispatcher().getRenderer(bat);
            EntityRenderState batRenderState = renderer.createRenderState(bat, partialTicks);
            mc().getEntityRenderDispatcher().submit(batRenderState, new CameraRenderState(), 0, 0, 0, event.getPoseStack(), event.getSubmitNodeCollector());
        }
    }

    public <I extends LivingEntity, S extends LivingEntityRenderState, U extends EntityModel<S>> void syncOverlays() {
        for (EntityType<?> type : VampirismMod.services().entityRegistry().getConvertibleOverlay().keySet()) {
            LivingEntityRenderer<I, S, U> render = (LivingEntityRenderer<I, S, U>) Minecraft.getInstance().getEntityRenderDispatcher().getRenderers().get(type);
            if (render == null) {
                LOGGER.error("Did not find renderer for {}", type);
                continue;
            }
            if (render.getLayers().stream().noneMatch(s -> s instanceof ConvertedVampireEntityLayer<?, ?>)) {
                render.addLayer(new ConvertedVampireEntityLayer<>(render, true));
            }
        }
    }

}
