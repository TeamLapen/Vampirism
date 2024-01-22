package de.teamlapen.vampirism.client.renderer;

import com.mojang.blaze3d.pipeline.RenderTarget;
import de.teamlapen.lib.util.OptifineHandler;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.entity.IExtendedCreatureVampirism;
import de.teamlapen.vampirism.api.entity.hunter.IHunterMob;
import de.teamlapen.vampirism.api.items.IItemWithTier;
import de.teamlapen.vampirism.blocks.CoffinBlock;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModAttachments;
import de.teamlapen.vampirism.core.ModRefinements;
import de.teamlapen.vampirism.entity.ExtendedCreature;
import de.teamlapen.vampirism.entity.player.VampirismPlayerAttributes;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayerSpecialAttributes;
import de.teamlapen.vampirism.items.CrucifixItem;
import de.teamlapen.vampirism.mixin.client.accessor.CameraAccessor;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.MixinHooks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.OutlineBufferSource;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.PostPass;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.event.TickEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Handle most general rendering related stuff
 */
public class RenderHandler implements ResourceManagerReloadListener {
    private static final int ENTITY_NEAR_SQ_DISTANCE = 100;
    @NotNull
    private final Minecraft mc;
    private final int BLOOD_VISION_FADE_TICKS = 80;

    private final int VAMPIRE_BIOME_FADE_TICKS = 60;
    private final Logger LOGGER = LogManager.getLogger();
    @Nullable
    private OutlineBufferSource bloodVisionBuffer;
    /**
     * Fog fade counter
     * Between 0 and {@link #BLOOD_VISION_FADE_TICKS}
     * Updated every tick if {@link #insideFog}
     */
    private int vampireBiomeTicks = 0;
    /**
     * If inside a foggy area.
     * Only updated every n ticks
     */
    private boolean insideFog = false;
    private int bloodVisionTicks = 0;
    private int lastBloodVisionTicks = 0;
    private float vampireBiomeFogDistanceMultiplier = 1;
    @Nullable
    private PostChain blurShader;
    /**
     * Store the last used framebuffer size to be able to rebind shader buffer when size changes
     */
    private int displayHeight, displayWidth;
    private boolean reducedBloodVision = false;

    @Nullable
    private PostPass blur1, blur2, blit0;
    private boolean isInsideBloodVisionRendering = false;

    public RenderHandler(@NotNull Minecraft mc) {
        this.mc = mc;
    }

    @SubscribeEvent
    public void onCameraSetup(ViewportEvent.@NotNull ComputeCameraAngles event) {
        if (shouldRenderBloodVision()) {
            reducedBloodVision = OptifineHandler.isShaders();
            if (!reducedBloodVision) {
                if (displayHeight != mc.getWindow().getHeight() || displayWidth != mc.getWindow().getWidth()) {
                    this.displayHeight = mc.getWindow().getHeight();
                    this.displayWidth = mc.getWindow().getWidth();
                    this.updateFramebufferSize(this.displayWidth, this.displayHeight);
                }
                adjustBloodVisionShaders(getBloodVisionProgress((float) event.getPartialTick()));
            } else {
                MixinHooks.enforcingGlowing_bloodVision = true;
            }
        }
        if (VampirismConfig.SERVER.preventRenderingDebugBoundingBoxes.get()) {
            Minecraft.getInstance().getEntityRenderDispatcher().setRenderHitBoxes(false);
        }
        if (event.getCamera().getEntity() instanceof LivingEntity && ((LivingEntity) event.getCamera().getEntity()).isSleeping()) {
            ((LivingEntity) event.getCamera().getEntity()).getSleepingPos().map(pos -> event.getCamera().getEntity().level().getBlockState(pos)).filter(blockState -> blockState.getBlock() instanceof CoffinBlock).ifPresent(blockState -> {
                if (blockState.getValue(CoffinBlock.VERTICAL)) {
                    ((CameraAccessor) event.getCamera()).invoke_move(0.2, -0.2, 0);
                } else {
                    ((CameraAccessor) event.getCamera()).invoke_move(0, -0.2, 0);
                }
            });
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.@NotNull ClientTickEvent event) {
        if (mc.level == null || mc.player == null || !mc.player.isAlive()) return;
        if (event.phase == TickEvent.Phase.END) return;
        lastBloodVisionTicks = bloodVisionTicks;
        VampirePlayer vampire = VampirePlayer.get(mc.player);
        if (vampire.getSpecialAttributes().blood_vision && !VampirismConfig.CLIENT.disableBloodVisionRendering.get() && !vampire.isGettingSundamage(mc.player.level())) {
            if (bloodVisionTicks < BLOOD_VISION_FADE_TICKS) {
                bloodVisionTicks++;

            }
        } else {
            if (bloodVisionTicks > 0) {
                bloodVisionTicks -= 2;
            }
            if (vampireBiomeTicks > 10 && bloodVisionTicks == 15) {
                bloodVisionTicks = 0;
            }
        }

        //Vampire biome/village fog
        if (mc.player.tickCount % 10 == 0) {
            if ((VampirismConfig.CLIENT.renderVampireForestFog.get() || VampirismConfig.SERVER.enforceRenderForestFog.get()) && (Helper.isEntityInArtificalVampireFogArea(mc.player) || Helper.isEntityInVampireBiome(mc.player))) {
                insideFog = true;
                vampireBiomeFogDistanceMultiplier = vampire.getLevel() > 0 ? 2 : 1;
                vampireBiomeFogDistanceMultiplier += vampire.getSkillHandler().isRefinementEquipped(ModRefinements.VISTA.get()) ? VampirismConfig.BALANCE.vrVistaMod.get().floatValue() : 0;

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
        event.setNearPlaneDistance(switch (event.getMode()) {
            case FOG_TERRAIN -> Math.min(event.getFarPlaneDistance() * 0.75f, 6 * f);
            case FOG_SKY -> 0;
        });
        event.setFarPlaneDistance(Math.min(event.getFarPlaneDistance(), 50 * f));
        event.setCanceled(true);
    }

    @SubscribeEvent
    public void onRenderHand(@NotNull RenderHandEvent event) {
        if (mc.player != null && mc.player.isAlive() && VampirismPlayerAttributes.get(mc.player).getVampSpecial().bat) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onRenderLivingPost(RenderLivingEvent.@NotNull Post<?, ?> event) {
        if (!isInsideBloodVisionRendering && shouldRenderBloodVision() && !reducedBloodVision) {
            Entity entity = event.getEntity();

            boolean flag = !(entity instanceof Player) || VampirismPlayerAttributes.get((Player) entity).getHuntSpecial().fullHunterCoat == null;
            double dist = mc.player.distanceToSqr(entity);
            if (dist > VampirismConfig.BALANCE.vsBloodVisionDistanceSq.get()) {
                flag = false;
            }
            if (flag) {
                int color;
                Optional<ExtendedCreature> opt = entity instanceof PathfinderMob && entity.isAlive() ? ExtendedCreature.getSafe(entity) : Optional.empty();
                if (opt.map(creature -> creature.getBlood() > 0 && !creature.hasPoisonousBlood()).orElse(false)) {
                    color = 0xFF0000;
                } else if (VampirismPlayerAttributes.get(mc.player).getVampSpecial().blood_vision_garlic && ((opt.map(IExtendedCreatureVampirism::hasPoisonousBlood).orElse(false)) || entity instanceof IHunterMob)) {
                    color = 0x07FF07;
                } else {
                    color = 0xA0A0A0;
                }
                EntityRenderDispatcher renderManager = mc.getEntityRenderDispatcher();
                if (bloodVisionBuffer == null) {
                    bloodVisionBuffer = new OutlineBufferSource(mc.renderBuffers().bufferSource());
                }
                int r = color >> 16 & 255;
                int g = color >> 8 & 255;
                int b = color & 255;
                int alpha = (int) ((dist > ENTITY_NEAR_SQ_DISTANCE ? 50 : (dist / (double) ENTITY_NEAR_SQ_DISTANCE * 50d)) * getBloodVisionProgress(event.getPartialTick()));
                bloodVisionBuffer.setColor(r, g, b, alpha);
                float f = Mth.lerp(event.getPartialTick(), entity.yRotO, entity.getYRot());
                isInsideBloodVisionRendering = true;
                EntityRenderer<? super Entity> entityrenderer = renderManager.getRenderer(entity);
                entityrenderer.render(entity, f, event.getPartialTick(), event.getPoseStack(), bloodVisionBuffer, renderManager.getPackedLightCoords(entity, event.getPartialTick()));
                mc.getMainRenderTarget().bindWrite(false);
                isInsideBloodVisionRendering = false;

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
    public void onRenderPlayer(RenderPlayerEvent.@NotNull Pre event) {
        Player player = event.getEntity();
        VampirePlayerSpecialAttributes vAtt = VampirismPlayerAttributes.get(player).getVampSpecial();
        if (vAtt.isDBNO) {
            event.getPoseStack().translate(1.2, 0, 0);
            PlayerModel<?> m = event.getRenderer().getModel();
            m.rightArm.visible = false;
            m.rightSleeve.visible = false;
            m.leftArm.visible = false;
            m.leftSleeve.visible = false;
            m.rightLeg.visible = false;
            m.leftLeg.visible = false;
            m.rightPants.visible = false;
            m.leftPants.visible = false;
        } else if (player.getSleepingPos().map(pos -> player.level().getBlockState(pos)).map(state -> state.getBlock() instanceof CoffinBlock).orElse(false)) {
            //Shrink player, so they fit into the coffin model
            event.getPoseStack().scale(0.8f, 0.95f, 0.8f);
        } else if (player.isUsingItem() && player.getUseItemRemainingTicks() > 0 && player.getUseItem().getItem() instanceof CrucifixItem) {
            boolean main = player.getUsedItemHand() == InteractionHand.MAIN_HAND;
            if ((main ? player.getMainArm() : player.getMainArm().getOpposite()) == HumanoidArm.RIGHT) {
                event.getRenderer().getModel().rightArmPose = HumanoidModel.ArmPose.BLOCK;
            } else {
                event.getRenderer().getModel().leftArmPose = HumanoidModel.ArmPose.BLOCK;
            }
        }
    }

    @SubscribeEvent
    public void onRenderWorldLast(@NotNull RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_WEATHER) {
            MixinHooks.enforcingGlowing_bloodVision = false;
            if (mc.level == null) return;

            if (shouldRenderBloodVision() && !reducedBloodVision) {
                this.blurShader.process(mc.getFrameTime());
                this.mc.getMainRenderTarget().bindWrite(false);
            }
        }
    }

    public void endBloodVisionBatch() {
        if (shouldRenderBloodVision() && !reducedBloodVision) {
            if (this.bloodVisionBuffer != null) this.bloodVisionBuffer.endOutlineBatch();
        }
    }

    @Override
    public void onResourceManagerReload(@NotNull ResourceManager resourceManager) {
        this.reMakeBloodVisionShader();
    }

    @SubscribeEvent
    public void onWorldLoad(LevelEvent.Load event) {
        this.bloodVisionTicks = 0;
        this.vampireBiomeTicks = 0;
        this.insideFog = false;
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onRenderPlayerPreHigh(RenderPlayerEvent.@NotNull Pre event) {
        Player player = event.getEntity();
        VampirePlayerSpecialAttributes vAtt = VampirismPlayerAttributes.get(player).getVampSpecial();
        if (vAtt.invisible) {
            event.setCanceled(true);
        } else if (vAtt.bat) {
            event.setCanceled(true);
            var bat = player.getData(ModAttachments.VAMPIRE_BAT);

            float partialTicks = event.getPartialTick();

            // Copy values
            bat.yBodyRotO = player.yBodyRotO;
            bat.yBodyRot = player.yBodyRot;
            bat.tickCount = player.tickCount;
            bat.setXRot(player.getXRot());
            bat.setYRot(player.getYRot());
            bat.yHeadRot = player.yHeadRot;
            bat.yRotO = player.yRotO;
            bat.xRotO = player.xRotO;
            bat.yHeadRotO = player.yHeadRotO;
            bat.setInvisible(player.isInvisible());

            // Calculate render parameter
            double d0 = Mth.lerp(partialTicks, bat.xOld, bat.getX());
            double d1 = Mth.lerp(partialTicks, bat.yOld, bat.getY());
            double d2 = Mth.lerp(partialTicks, bat.zOld, bat.getZ());
            float f = Mth.lerp(partialTicks, bat.yRotO, bat.getYRot());
            mc.getEntityRenderDispatcher().render(bat, d0, d1, d2, f, partialTicks, event.getPoseStack(), mc.renderBuffers().bufferSource(), mc.getEntityRenderDispatcher().getPackedLightCoords(bat, partialTicks));
        }
    }

    public boolean shouldRenderBloodVision() {
        return this.bloodVisionTicks > 0 && this.blurShader != null && this.mc.player != null;
    }

    private void adjustBloodVisionShaders(float progress) {
        if (blit0 == null || blur1 == null || blur2 == null) return;
        progress = Mth.clamp(progress, 0, 1);
        blit0.getEffect().safeGetUniform("ColorModulate").set((1 - 0.4F * progress), (1 - 0.5F * progress), (1 - 0.3F * progress), 1);
        blur1.getEffect().safeGetUniform("Radius").set((float) Math.round(15 * progress)); //Round, because fractional values cause issues, but use float method.
        blur2.getEffect().safeGetUniform("Radius").set((float) Math.round(15 * progress));

    }

    private float getBloodVisionProgress(float partialTicks) {
        return (bloodVisionTicks + (bloodVisionTicks - lastBloodVisionTicks) * partialTicks) / (float) BLOOD_VISION_FADE_TICKS;
    }

    private void reMakeBloodVisionShader() {
        if (this.blurShader != null) {
            this.blurShader.close();
        }
        ResourceLocation resourcelocationBlur = new ResourceLocation(REFERENCE.MODID, "shaders/blank.json");
        try {
            this.blurShader = new PostChain(this.mc.getTextureManager(), this.mc.getResourceManager(), this.mc.getMainRenderTarget(), resourcelocationBlur);
            RenderTarget swap = this.blurShader.getTempTarget("swap");

            blit0 = blurShader.addPass("blit", swap, this.mc.getMainRenderTarget());
            blur1 = blurShader.addPass("blur", this.mc.getMainRenderTarget(), swap);
            blur1.getEffect().safeGetUniform("BlurDir").set(1F, 0F);
            blur2 = blurShader.addPass("blur", swap, this.mc.getMainRenderTarget());
            blur2.getEffect().safeGetUniform("BlurDir").set(0F, 1F);

            this.blurShader.resize(this.mc.getWindow().getWidth(), this.mc.getWindow().getHeight());

        } catch (Exception e) {
            LOGGER.warn("Failed to load blood vision blur shader", e);
            this.blurShader = null;
        }
    }

    private void updateFramebufferSize(int width, int height) {
        if (this.blurShader != null) {
            this.blurShader.resize(width, height);
        }
    }

}
