package de.teamlapen.vampirism.client.render;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import de.teamlapen.lib.util.OptifineHandler;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.entity.IExtendedCreatureVampirism;
import de.teamlapen.vampirism.api.entity.hunter.IHunterMob;
import de.teamlapen.vampirism.api.items.IItemWithTier;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModRefinements;
import de.teamlapen.vampirism.entity.ExtendedCreature;
import de.teamlapen.vampirism.player.VampirismPlayerAttributes;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.player.vampire.VampirePlayerSpecialAttributes;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.MixinHooks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.OutlineBufferSource;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.PostPass;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.*;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Handle most general rendering related stuff
 */
@OnlyIn(Dist.CLIENT)
public class RenderHandler implements ResourceManagerReloadListener {
    private static final int ENTITY_NEAR_SQ_DISTANCE = 100;
    @Nonnull
    private final Minecraft mc;
    private final int BLOOD_VISION_FADE_TICKS = 80;

    private final int VAMPIRE_BIOME_FADE_TICKS = 60;
    private final Logger LOGGER = LogManager.getLogger();
    @Nullable
    private OutlineBufferSource bloodVisionBuffer;
    private Bat entityBat;
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

    public RenderHandler(@Nonnull Minecraft mc) {
        this.mc = mc;
    }

    @SubscribeEvent
    public void onCameraSetup(EntityViewRenderEvent.CameraSetup event) {
        if (shouldRenderBloodVision()) {
            reducedBloodVision = OptifineHandler.isShaders();
            if (!reducedBloodVision) {
                if (displayHeight != mc.getWindow().getHeight() || displayWidth != mc.getWindow().getWidth()) {
                    this.displayHeight = mc.getWindow().getHeight();
                    this.displayWidth = mc.getWindow().getWidth();
                    this.updateFramebufferSize(this.displayWidth, this.displayHeight);
                }
                adjustBloodVisionShaders(getBloodVisionProgress((float) event.getPartialTicks()));
            } else {
                MixinHooks.enforcingGlowing_bloodVision = true;
            }
        }
        if (VampirismConfig.SERVER.preventRenderingDebugBoundingBoxes.get()) {
            Minecraft.getInstance().getEntityRenderDispatcher().setRenderHitBoxes(false);
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (mc.level == null || mc.player == null || !mc.player.isAlive()) return;
        if (event.phase == TickEvent.Phase.END) return;
        lastBloodVisionTicks = bloodVisionTicks;
        @Nullable
        VampirePlayer vampire = VampirePlayer.getOpt(mc.player).resolve().orElse(null);
        if(vampire != null){
            if (vampire.getSpecialAttributes().blood_vision && !VampirismConfig.CLIENT.disableBloodVisionRendering.get() && !vampire.isGettingSundamage(mc.player.level)) {
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
        }

        //Vampire biome/village fog
        if (mc.player.tickCount % 10 == 0) {
            if ((VampirismConfig.CLIENT.renderVampireForestFog.get() || VampirismConfig.SERVER.enforceRenderForestFog.get()) && (Helper.isEntityInArtificalVampireFogArea(mc.player) || Helper.isEntityInVampireBiome(mc.player))) {
                insideFog = true;
                vampireBiomeFogDistanceMultiplier = vampire != null && vampire.getLevel() > 0 ? 2 : 1;
                vampireBiomeFogDistanceMultiplier += vampire != null && vampire.getSkillHandler().isRefinementEquipped(ModRefinements.vista) ? VampirismConfig.BALANCE.vrVistaMod.get() : 0;

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

        //Saturation shader BUGGED this renderes the hotbar white
//        if (mc.player != null && mc.player.getRNG().nextInt(10) == 3) {
//            EffectInstance pe = mc.player.getActivePotionEffect(ModEffects.saturation.get());
//            boolean active = pe != null && pe.getAmplifier() >= 2;
//            GameRenderer renderer = mc.gameRenderer;
//            if (active && renderer.getShaderGroup() == null) {
//                renderer.loadShader(saturation1);
//            } else if (!active && renderer.getShaderGroup() != null && renderer.getShaderGroup().getShaderGroupName().equals(saturation1.toString())) {
//                renderer.stopUseShader();
//            }
//        }


    }

    @SubscribeEvent
    public void onRenderFog(EntityViewRenderEvent.RenderFogEvent event) {
        if (vampireBiomeTicks == 0) return;
        float f = ((float) VAMPIRE_BIOME_FADE_TICKS) / (float) vampireBiomeTicks / 1.5f;
        f *= vampireBiomeFogDistanceMultiplier;
        float fogStart = Math.min(event.getFarPlaneDistance() * 0.75f, 6 * f);
        float fogEnd = Math.min(event.getFarPlaneDistance(), 50 * f);
        RenderSystem.setShaderFogStart(event.getMode() == FogRenderer.FogMode.FOG_SKY ? 0 : fogStart); //maybe invert
        RenderSystem.setShaderFogEnd(fogEnd);
    }

    @SubscribeEvent
    public void onRenderHand(RenderHandEvent event) {
        if (mc.player != null && mc.player.isAlive() && VampirismPlayerAttributes.get(mc.player).getVampSpecial().bat) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onRenderLivingPost(RenderLivingEvent.Post<?,?> event) {
        if (!isInsideBloodVisionRendering && shouldRenderBloodVision() && !reducedBloodVision) {
            Entity entity = event.getEntity();

            boolean flag = !(entity instanceof Player) || VampirismPlayerAttributes.get((Player) entity).getHuntSpecial().fullHunterCoat == null;
            double dist = mc.player.distanceToSqr(entity);
            if (dist > VampirismConfig.BALANCE.vsBloodVisionDistanceSq.get()) {
                flag = false;
            }
            if (flag) {
                int color;
                LazyOptional<IExtendedCreatureVampirism> opt = entity instanceof PathfinderMob && entity.isAlive() ? ExtendedCreature.getSafe(entity) : LazyOptional.empty();
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
    public void onRenderLivingPre(RenderLivingEvent.Pre<Player, PlayerModel<Player>> event) {
        LivingEntity entity = event.getEntity();
        if (entity instanceof Player && VampirismPlayerAttributes.get((Player) entity).getHuntSpecial().isDisguised()) {
            double dist = this.mc.player == null ? 0 : entity.distanceToSqr(this.mc.player);
            if (dist > 64) {
                event.setCanceled(true);
            } else if (dist > 16) {
                IItemWithTier.TIER hunterCoatTier = VampirismPlayerAttributes.get((Player) entity).getHuntSpecial().fullHunterCoat;
                if (hunterCoatTier == IItemWithTier.TIER.ENHANCED || hunterCoatTier == IItemWithTier.TIER.ULTIMATE) {
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public void onRenderPlayer(RenderPlayerEvent.Pre event) {
        Player player = event.getPlayer();
        VampirePlayerSpecialAttributes vAtt = VampirismPlayerAttributes.get(player).getVampSpecial();
        if (vAtt.invisible) {
            event.setCanceled(true);
        } else if (vAtt.bat) {
            event.setCanceled(true);
            if (entityBat == null) {
                entityBat = EntityType.BAT.create(event.getEntity().getCommandSenderWorld());
                entityBat.setResting(false);
            }

            float partialTicks = event.getPartialTick();

            // Copy values
            entityBat.yBodyRotO = player.yBodyRotO;
            entityBat.yBodyRot = player.yBodyRot;
            entityBat.tickCount = player.tickCount;
            entityBat.setXRot(player.getXRot());
            entityBat.setYRot(player.getYRot());
            entityBat.yHeadRot = player.yHeadRot;
            entityBat.yRotO = player.yRotO;
            entityBat.xRotO = player.xRotO;
            entityBat.yHeadRotO = player.yHeadRotO;
            entityBat.setInvisible(player.isInvisible());

            // Calculate render parameter
            double d0 = Mth.lerp(partialTicks, entityBat.xOld, entityBat.getX());
            double d1 = Mth.lerp(partialTicks, entityBat.yOld, entityBat.getY());
            double d2 = Mth.lerp(partialTicks, entityBat.zOld, entityBat.getZ());
            float f = Mth.lerp(partialTicks, entityBat.yRotO, entityBat.getYRot());
            mc.getEntityRenderDispatcher().render(entityBat, d0, d1, d2, f, partialTicks, event.getPoseStack(), mc.renderBuffers().bufferSource(), mc.getEntityRenderDispatcher().getPackedLightCoords(entityBat, partialTicks));

        } else if (vAtt.isDBNO) {
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
        }

    }

    @SubscribeEvent
    public void onRenderWorldLast(RenderLevelLastEvent event) {
        MixinHooks.enforcingGlowing_bloodVision = false;
        if (mc.level == null) return;

        /*
         * DO NOT USE partial ticks from event. They are bugged: https://github.com/MinecraftForge/MinecraftForge/issues/6380
         */
        float partialTicks = mc.getFrameTime();


        if (shouldRenderBloodVision() && !reducedBloodVision) {
            this.blurShader.process(partialTicks);
            if (this.bloodVisionBuffer != null) this.bloodVisionBuffer.endOutlineBatch();
        }
    }

    @Override
    public void onResourceManagerReload(@Nonnull ResourceManager resourceManager) {
        this.reMakeBloodVisionShader();
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        this.bloodVisionTicks = 0;//Reset blood vision on world load
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
