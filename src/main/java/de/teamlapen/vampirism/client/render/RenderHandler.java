package de.teamlapen.vampirism.client.render;

import com.google.common.collect.Lists;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import de.teamlapen.vampirism.api.entity.IExtendedCreatureVampirism;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.entity.ExtendedCreature;
import de.teamlapen.vampirism.items.HunterCoatItem;
import de.teamlapen.vampirism.player.hunter.HunterPlayer;
import de.teamlapen.vampirism.player.hunter.HunterPlayerSpecialAttribute;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.player.vampire.actions.VampireActions;
import de.teamlapen.vampirism.tileentity.TotemTileEntity;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.Shader;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.*;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.List;

/**
 * Handle most general rendering related stuff
 */
@OnlyIn(Dist.CLIENT)
public class RenderHandler {
    private static final ResourceLocation saturation1 = new ResourceLocation(REFERENCE.MODID + ":shaders/saturation1.json");
    private final Minecraft mc;
    private final int BLOOD_VISION_FADE_TICKS = 80;

    private final int VAMPIRE_BIOME_FADE_TICKS = 60;
    private final Logger LOGGER = LogManager.getLogger();
    private final List<LivingEntity> renderedEntitiesWithBlood = Lists.newLinkedList();
    private final List<LivingEntity> renderedEntitiesWithoutBlood = Lists.newLinkedList();
    private final List<LivingEntity> renderedEntitiesWithGarlicInfused = Lists.newLinkedList();
    private BatEntity entityBat;
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
    private Framebuffer bloodVisionFrameBuffer1;
    private Framebuffer bloodVisionFrameBuffer2;
    private Framebuffer bloodVisionFrameBuffer3;
    private ShaderGroup bloodVisionShader1;
    private ShaderGroup bloodVisionShader2;
    private ShaderGroup bloodVisionShader3;
    private ShaderGroup blurShader;
    private boolean bloodVisionShaderInit = false;
    private boolean bloodVision1Rendered = false;
    private boolean bloodVision2Rendered = false;
    private boolean bloodVision3Rendered = false;
    private boolean shaderWarning = false;
    private boolean showedShaderWarning = false;
    private boolean doSaturationShader = true;
    private int displayHeight, displayWidth;
    private boolean renderingBloodVision = false;
    private Shader blur1, blur2, blit0, blit1, blit2, blit3;
    /**
     * Temporarily stores if the hunter disguise blend profile has been enabled. (From RenderPlayer.Pre to RenderPlayer.Post)
     */
    private boolean hunterDisguiseEnabled;

    public RenderHandler(Minecraft mc) {
        this.mc = mc;
        this.displayHeight = mc.getMainWindow().getHeight();
        this.displayWidth = mc.getMainWindow().getWidth();
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (mc.world == null || mc.player == null || !mc.player.isAlive()) return;
        if (event.phase == TickEvent.Phase.END) return;
        lastBloodVisionTicks = bloodVisionTicks;

        VampirePlayer vampire = VampirePlayer.get(mc.player);
        if (vampire.getSpecialAttributes().blood_vision && !VampirePlayer.get(mc.player).isGettingSundamage(mc.player.world)) {

            if (bloodVisionTicks < BLOOD_VISION_FADE_TICKS) {
                bloodVisionTicks++;

            }
        } else {
            if (bloodVisionTicks > 0) {
                bloodVisionTicks -= 2;
                if (bloodVisionTicks <= 0) {
                    disableBloodVision();
                }
            }
            if (vampireBiomeTicks > 10 && bloodVisionTicks == 15) {
                bloodVisionTicks = 0;
                disableBloodVision();
            }
        }
        if (mc.player.ticksExisted % 10 == 0) {
            if ((VampirismConfig.CLIENT.renderVampireForestFog.get() || VampirismConfig.SERVER.enforceRenderForestFog.get()) && (Helper.isEntityInVampireBiome(mc.player) || TotemTileEntity.isInsideVampireAreaCached(mc.world.getDimension(), mc.player.getPosition()))) {
                insideFog = true;
                vampireBiomeFogDistanceMultiplier = vampire.getSpecialAttributes().increasedVampireFogDistance ? 2 : 1;
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

        if (doSaturationShader) {
            if (mc.player != null && mc.player.getRNG().nextInt(10) == 3) {
                EffectInstance pe = mc.player.getActivePotionEffect(ModEffects.saturation);
                boolean active = pe != null && pe.getAmplifier() >= 2;
                GameRenderer renderer = mc.gameRenderer;
                if (active && renderer.getShaderGroup() == null) {
                    renderer.loadShader(saturation1);
                } else if (!active && renderer.getShaderGroup() != null && renderer.getShaderGroup().getShaderGroupName().equals(saturation1.toString())) {
                    renderer.stopUseShader();
                }
            }
        }

        if (shaderWarning && mc.player != null) {
            shaderWarning = false;
            showedShaderWarning = true;
            mc.player.sendMessage(new StringTextComponent("Blood vision does not work on your hardware, because shaders are not supported"));
            mc.player.sendMessage(new StringTextComponent("If you are running on recent hardware and use updated drivers, but this still shows up, please contact the author of Vampirism"));
        }


    }


    @SubscribeEvent
    public void onRenderHand(RenderHandEvent event) {
        if (mc.player.isAlive() && VampirePlayer.get(mc.player).getActionHandler().isActionActive(VampireActions.bat)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onRenderLivingPost(RenderLivingEvent.Post event) {
        if (bloodVisionTicks > 0 && !renderingBloodVision) {
            Entity entity = event.getEntity();

            boolean flag = true;
            if (entity instanceof PlayerEntity && HunterCoatItem.isFullyEquipped((PlayerEntity) entity)) flag = false;

            if (mc.player.getDistanceSq(entity) > VampirismConfig.BALANCE.vsBloodVisionDistSQ.get()) {
                flag = false;
            }
            if (flag) {
                IExtendedCreatureVampirism creature = entity instanceof CreatureEntity && entity.isAlive() ? ExtendedCreature.getUnsafe((CreatureEntity) entity) : null;
                if (creature != null && creature.getBlood() > 0 && !creature.hasPoisonousBlood()) {
                    renderedEntitiesWithBlood.add(event.getEntity());

                } else if (VampirePlayer.getOpt(mc.player).map(VampirePlayer::getSpecialAttributes).map(s -> s.blood_vision_garlic).orElse(false) && ((creature != null && creature.hasPoisonousBlood()) || Helper.isHunter(entity))) {
                    renderedEntitiesWithGarlicInfused.add(event.getEntity());

                } else {
                    renderedEntitiesWithoutBlood.add(event.getEntity());
                }
            }

        }
    }

    @SubscribeEvent
    public void onRenderLivingPre(RenderLivingEvent.Pre<PlayerEntity, PlayerModel<PlayerEntity>> event) {
        LivingEntity entity = event.getEntity();
        if (entity instanceof PlayerEntity && HunterPlayer.getOpt((PlayerEntity) entity).map(HunterPlayer::getSpecialAttributes).map(HunterPlayerSpecialAttribute::isDisguised).orElse(false)) {
            if (this.mc.player != null && entity.getDistanceSq(this.mc.player) > 4) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onRenderFog(EntityViewRenderEvent.RenderFogEvent event) {
        if (vampireBiomeTicks == 0) return;
        float f = ((float) VAMPIRE_BIOME_FADE_TICKS) / (float) vampireBiomeTicks / 1.5f;
        f *= vampireBiomeFogDistanceMultiplier;
        float fogStart = Math.min(event.getFarPlaneDistance() * 0.75f, 6 * f);
        float fogEnd = Math.min(event.getFarPlaneDistance(), 50 * f);
        RenderSystem.fogStart(event.getType() == FogRenderer.FogType.FOG_SKY ? 0 : fogStart); //TODO maybe invert
        RenderSystem.fogEnd(fogEnd);
    }

    @SubscribeEvent
    public void onRenderPlayerPost(RenderPlayerEvent.Post event) {
        if (hunterDisguiseEnabled) {
            disableProfile(Profile.HUNTER_DISGUISE);
        }
    }

    @SubscribeEvent
    public void onRenderPlayer(RenderPlayerEvent.Pre event) {
        PlayerEntity player = event.getPlayer();

        if (VampirePlayer.getOpt(player).map(VampirePlayer::getSpecialAttributes).map(s -> s.bat).orElse(false)) {
            event.setCanceled(true);
            if (entityBat == null) {
                entityBat = EntityType.BAT.create(event.getEntity().getEntityWorld());
                entityBat.setIsBatHanging(false);
            }

            float partialTicks = event.getPartialRenderTick();

            // Copy values
            entityBat.prevRenderYawOffset = player.prevRenderYawOffset;
            entityBat.renderYawOffset = player.renderYawOffset;
            entityBat.ticksExisted = player.ticksExisted;
            entityBat.rotationPitch = player.rotationPitch;
            entityBat.rotationYaw = player.rotationYaw;
            entityBat.rotationYawHead = player.rotationYawHead;
            entityBat.prevRotationYaw = player.prevRotationYaw;
            entityBat.prevRotationPitch = player.prevRotationPitch;
            entityBat.prevRotationYawHead = player.prevRotationYawHead;
            entityBat.setInvisible(player.isInvisible());

            // Calculate render parameter
            double d0 = MathHelper.lerp(partialTicks, entityBat.lastTickPosX, entityBat.getPosX());
            double d1 = MathHelper.lerp(partialTicks, entityBat.lastTickPosY, entityBat.getPosY());
            double d2 = MathHelper.lerp(partialTicks, entityBat.lastTickPosZ, entityBat.getPosZ());
            float f = MathHelper.lerp(partialTicks, entityBat.prevRotationYaw, entityBat.rotationYaw);
            mc.getRenderManager().renderEntityStatic(entityBat, d0, d1, d2, f, partialTicks, event.getMatrixStack(), mc.getRenderTypeBuffers().getBufferSource(), mc.getRenderManager().getPackedLight(entityBat, partialTicks));

        } else if (HunterPlayer.getOpt(player).map(HunterPlayer::getSpecialAttributes).map(HunterPlayerSpecialAttribute::isDisguised).orElse(false)) {
            if (!player.equals(this.mc.player)) {
                double distSq = player.getDistanceSq(this.mc.player);
                if (distSq > VampirismConfig.BALANCE.haDisguiseInvisibleSQ.get()) {
                    event.setCanceled(true);
                } else {
                    hunterDisguiseEnabled = true;
                    enableProfile(Profile.HUNTER_DISGUISE, MathHelper.clamp((float) (distSq / VampirismConfig.BALANCE.haDisguiseInvisibleSQ.get() * 25), 0, 1) * HunterPlayer.getOpt(player).map(HunterPlayer::getSpecialAttributes).map(HunterPlayerSpecialAttribute::getDisguiseProgress).orElse(0f));
                }
            }


        }
    }

    private void adjustBloodVisionShaders(float progress) {
        progress = MathHelper.clamp(progress, 0, 1);

        blit0.getShaderManager().getShaderUniform("ColorModulate").set((1 - 0.8F * progress), (1 - 0.9F * progress), (1 - 0.7F * progress), 1);
        blur1.getShaderManager().getShaderUniform("Radius").set(Math.round(10 * progress) / 1F);
        blur2.getShaderManager().getShaderUniform("Radius").set(Math.round(10 * progress) / 1F);
        blit1.getShaderManager().getShaderUniform("ColorModulate").set(1F, 0.1F, 0.1F, (1F * progress));
        blit2.getShaderManager().getShaderUniform("ColorModulate").set(0.1F, 0.1F, 0.2F, (0.7F * progress));
        blit3.getShaderManager().getShaderUniform("ColorModulate").set(0.1F, 0.1F, 1F, (1F * progress));

    }

    private void disableBloodVision() {
        renderedEntitiesWithoutBlood.clear();
        renderedEntitiesWithBlood.clear();
        renderedEntitiesWithGarlicInfused.clear();
    }

    private void disableProfile(Profile profile) {
        profile.clean();
    }

    private void enableProfile(Profile profile) {
        profile.apply(1);
    }

    private void enableProfile(Profile profile, float progress) {
        progress = MathHelper.clamp(progress, 0, 1);
        profile.apply(progress);
    }

    private boolean isRenderEntityOutlines() {
        return this.bloodVisionFrameBuffer1 != null && this.bloodVisionShader1 != null && this.bloodVisionFrameBuffer2 != null && this.bloodVisionShader2 != null && this.bloodVisionFrameBuffer3 != null && this.bloodVisionShader3 != null && this.mc.player != null;
    }


//    private void renderBloodVisionFog(int ticks) {
//        float f = ((float) BLOOD_VISION_FADE_TICKS) / (float) ticks;
//
//        GlStateManager.pushMatrix();
//        boolean fog = GL11.glIsEnabled(GL11.GL_FOG);
//        if (!fog)
//            GlStateManager.enableFog();
//        GlStateManager.setFog(GlStateManager.FogMode.LINEAR);
//        GlStateManager.setFogStart(4.0F * f);
//        GlStateManager.setFogEnd(5.5F * f);
//        GlStateManager.glNormal3f(0F, -1F, 0F);
//        GlStateManager.color(1F, 1F, 1F, 1F);
//
//        GlStateManager.setFogDensity(1);
//        if (!fog)
//            GlStateManager.disableFog();
//        GlStateManager.popMatrix();
//    }

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        if (mc.world == null) return;

        if (bloodVisionTicks > 0) {

            renderBloodVisionOutlines((bloodVisionTicks + (bloodVisionTicks - lastBloodVisionTicks) * event.getPartialTicks()) / (float) BLOOD_VISION_FADE_TICKS, event.getPartialTicks());

        }

        if (displayHeight != mc.getMainWindow().getHeight() || displayWidth != mc.getMainWindow().getWidth()) {
            this.displayHeight = mc.getMainWindow().getHeight();
            this.displayWidth = mc.getMainWindow().getWidth();
            if (isRenderEntityOutlines()) {
                blurShader.createBindFramebuffers(displayWidth, displayHeight);
                bloodVisionShader1.createBindFramebuffers(displayWidth, displayHeight);
                bloodVisionShader2.createBindFramebuffers(displayWidth, displayHeight);
                bloodVisionShader3.createBindFramebuffers(displayWidth, displayHeight);


            }
        }


    }

    private void makeBloodVisionShader() {

            ResourceLocation resourcelocationOutline = new ResourceLocation(REFERENCE.MODID, "shaders/blood_vision_outline.json");
            ResourceLocation resourcelocationBlur = new ResourceLocation(REFERENCE.MODID, "shaders/blank.json");

            try {
                this.blurShader = new ShaderGroup(this.mc.getTextureManager(), this.mc.getResourceManager(), this.mc.getFramebuffer(), resourcelocationBlur);
                Framebuffer swap = this.blurShader.getFramebufferRaw("swap");

                blit0 = blurShader.addShader("blit", swap, this.mc.getFramebuffer());
                blur1 = blurShader.addShader("blur", this.mc.getFramebuffer(), swap);
                blur1.getShaderManager().getShaderUniform("BlurDir").set(1F, 0F);
                blur2 = blurShader.addShader("blur", swap, this.mc.getFramebuffer());
                blur2.getShaderManager().getShaderUniform("BlurDir").set(0F, 1F);


                this.bloodVisionShader1 = new ShaderGroup(this.mc.getTextureManager(), this.mc.getResourceManager(), this.mc.getFramebuffer(), resourcelocationOutline);
                swap = this.bloodVisionShader1.getFramebufferRaw("swap");
                this.bloodVisionFrameBuffer1 = this.bloodVisionShader1.getFramebufferRaw("final");

                blit1 = bloodVisionShader1.addShader("blit", swap, bloodVisionFrameBuffer1);

                this.bloodVisionShader2 = new ShaderGroup(this.mc.getTextureManager(), this.mc.getResourceManager(), this.mc.getFramebuffer(), resourcelocationOutline);
                swap = this.bloodVisionShader2.getFramebufferRaw("swap");
                this.bloodVisionFrameBuffer2 = this.bloodVisionShader2.getFramebufferRaw("final");

                blit2 = bloodVisionShader2.addShader("blit", swap, bloodVisionFrameBuffer2);

                this.bloodVisionShader3 = new ShaderGroup(this.mc.getTextureManager(), this.mc.getResourceManager(), this.mc.getFramebuffer(), resourcelocationOutline);
                swap = this.bloodVisionShader3.getFramebufferRaw("swap");
                this.bloodVisionFrameBuffer3 = this.bloodVisionShader3.getFramebufferRaw("final");

                blit3 = bloodVisionShader3.addShader("blit", swap, bloodVisionFrameBuffer3);


                this.blurShader.createBindFramebuffers(this.mc.getMainWindow().getFramebufferWidth(), this.mc.getMainWindow().getFramebufferHeight());
                this.bloodVisionShader1.createBindFramebuffers(this.mc.getMainWindow().getFramebufferWidth(), this.mc.getMainWindow().getFramebufferHeight());
                this.bloodVisionShader2.createBindFramebuffers(this.mc.getMainWindow().getFramebufferWidth(), this.mc.getMainWindow().getFramebufferHeight());
                this.bloodVisionShader3.createBindFramebuffers(this.mc.getMainWindow().getFramebufferWidth(), this.mc.getMainWindow().getFramebufferHeight());

            } catch (IOException | JsonSyntaxException ioexception) {

                LOGGER.error("Failed to load shader", ioexception);
                this.bloodVisionShader1 = null;
                this.bloodVisionFrameBuffer1 = null;
                this.bloodVisionShader2 = null;
                this.bloodVisionFrameBuffer2 = null;
                this.bloodVisionShader3 = null;
                this.bloodVisionFrameBuffer3 = null;
            }

    }

    private boolean renderEntityOutlines(List<? extends Entity> entities, ShaderGroup shader, Framebuffer framebuffer, float partialTicks) {
        EntityRendererManager renderManager = mc.getRenderManager();
        mc.world.getProfiler().startSection("bloodVision");
        framebuffer.framebufferClear(Minecraft.IS_RUNNING_ON_MAC);
        boolean flag = !entities.isEmpty();
        if (flag) {
            //TODO 1.15
//            renderingBloodVision = true;
//
//            this.mc.gameRenderer.enableLightmap();
//
//            RenderSystem.depthFunc(519);
//            RenderSystem.disableFog();
//
//            framebuffer.bindFramebuffer(false);
//
//            RenderHelper.disableStandardItemLighting();
//
//            renderManager.setRenderOutlines(true);
//            for (Entity entity1 : entities) {
//                renderManager.renderEntityStatic(entity1, partialTicks, false);
//            }
//
//            renderManager.setRenderOutlines(false);
//
//            RenderHelper.enableStandardItemLighting();
//
//            RenderSystem.depthMask(false);
//            shader.render(partialTicks);
//            RenderSystem.enableLighting();
//            RenderSystem.depthMask(true);
//            RenderSystem.enableBlend();
//
//            RenderSystem.enableColorMaterial();
//            RenderSystem.depthFunc(515);
//            RenderSystem.enableDepthTest();
//            this.mc.gameRenderer.disableLightmap();
//            renderingBloodVision = false;
        }

        this.mc.getFramebuffer().bindFramebuffer(false);

        mc.world.getProfiler().endSection();

        return flag;
    }

    private void renderBloodVisionOutlines(float progress, float partialTicks) {
        if (!bloodVisionShaderInit) {

            makeBloodVisionShader();
            bloodVisionShaderInit = true;
        }
        if (!isRenderEntityOutlines()) {
            if (!showedShaderWarning) {
                shaderWarning = true;
            }
            return;
        }

        adjustBloodVisionShaders(progress);

        blurShader.render(partialTicks);


        if (!renderedEntitiesWithBlood.isEmpty() || this.bloodVision1Rendered) {
            bloodVision1Rendered = renderEntityOutlines(renderedEntitiesWithBlood, bloodVisionShader1, bloodVisionFrameBuffer1, partialTicks);
        }
        renderedEntitiesWithBlood.clear();

        if (!renderedEntitiesWithoutBlood.isEmpty() || this.bloodVision2Rendered) {
            bloodVision2Rendered = renderEntityOutlines(renderedEntitiesWithoutBlood, bloodVisionShader2, bloodVisionFrameBuffer2, partialTicks);
        }

        renderedEntitiesWithoutBlood.clear();

        if (!renderedEntitiesWithGarlicInfused.isEmpty() || this.bloodVision3Rendered) {
            bloodVision3Rendered = renderEntityOutlines(renderedEntitiesWithGarlicInfused, bloodVisionShader3, bloodVisionFrameBuffer3, partialTicks);
        }

        renderedEntitiesWithGarlicInfused.clear();

        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);


        this.bloodVisionFrameBuffer1.framebufferRenderExt(this.mc.getMainWindow().getFramebufferWidth(), this.mc.getMainWindow().getFramebufferHeight(), false);
        //this.mc.getFramebuffer().bindFramebuffer(false);
        this.bloodVisionFrameBuffer2.framebufferRenderExt(this.mc.getMainWindow().getFramebufferWidth(), this.mc.getMainWindow().getFramebufferHeight(), false);
        this.bloodVisionFrameBuffer3.framebufferRenderExt(this.mc.getMainWindow().getFramebufferWidth(), this.mc.getMainWindow().getFramebufferHeight(), false);

        this.mc.getFramebuffer().bindFramebuffer(false);


        RenderSystem.disableBlend();


    }


    private enum Profile {
        HUNTER_DISGUISE {
            @Override
            public void apply(float progress) {
                RenderSystem.color4f(1F, 1F, 1F, 1 - progress * 0.8F);
                if (progress >= 1F) {
                    RenderSystem.depthMask(false);
                }
                RenderSystem.enableBlend();
                RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                RenderSystem.alphaFunc(516, 0.003921569F);
            }

            @Override
            public void clean() {
                RenderSystem.disableBlend();
                RenderSystem.alphaFunc(516, 0.1F);
                RenderSystem.depthMask(true);
                RenderSystem.color4f(1F, 1F, 1F, 1F);
            }
        };

        Profile() {
        }

        /**
         * @param progress If dynamic this can be a value between 0 and 1 to fade the effect.
         */
        public abstract void apply(float progress);

        public abstract void clean();
    }

}
