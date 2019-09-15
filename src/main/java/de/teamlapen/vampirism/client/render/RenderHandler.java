package de.teamlapen.vampirism.client.render;

import com.google.common.collect.Lists;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;

import de.teamlapen.vampirism.api.entity.IExtendedCreatureVampirism;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.entity.ExtendedCreature;
import de.teamlapen.vampirism.items.HunterCoatItem;
import de.teamlapen.vampirism.player.hunter.HunterPlayer;
import de.teamlapen.vampirism.player.hunter.HunterPlayerSpecialAttribute;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.player.vampire.VampirePlayerSpecialAttributes;
import de.teamlapen.vampirism.player.vampire.actions.VampireActions;
import de.teamlapen.vampirism.tileentity.TotemTile;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.Shader;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.client.shader.ShaderLinkHelper;
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
import org.lwjgl.opengl.GL11;

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

    private final int VAMPIRE_BIOME_FADE_TICKS = 160;
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
        this.displayHeight = mc.mainWindow.getHeight();
        this.displayWidth = mc.mainWindow.getWidth();
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (mc.world == null || mc.player == null || !mc.player.isAlive()) return;
        if (event.phase == TickEvent.Phase.END) return;
        lastBloodVisionTicks = bloodVisionTicks;

        VampirePlayer vampire = VampirePlayer.get(mc.player);
        if (vampire.getSpecialAttributes().blood_vision && !VampirePlayer.get(mc.player).isGettingSundamage()) {

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
            if ((VampirismConfig.CLIENT.renderVampireForestFog.get() || VampirismConfig.SERVER.enforceRenderForestFog.get()) && (Helper.isEntityInVampireBiome(mc.player) || TotemTile.isInsideVampireAreaCached(mc.world.getDimension(), mc.player.getPosition()))) {
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

        if (GLX.isNextGen() && doSaturationShader) {
            if (mc.player != null && mc.player.getRNG().nextInt(10) == 3) {
                EffectInstance pe = mc.player.getActivePotionEffect(ModEffects.saturation);
                boolean active = pe != null && pe.getAmplifier() >= 2;
                GameRenderer renderer = mc.gameRenderer;
                if (active && !renderer.isShaderActive()) {
                    renderer.loadShader(saturation1);
                } else if (!active && renderer.isShaderActive() && renderer.getShaderGroup().getShaderGroupName().equals(saturation1.toString())) {
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
    public void onFogDensity(EntityViewRenderEvent.FogDensity event) {
        if (event.getInfo().getRenderViewEntity() instanceof PlayerEntity) {
            if (vampireBiomeTicks > 10 || bloodVisionTicks > 0) {
                event.setDensity(1.0F);
                event.setCanceled(true);
            }
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
        if (bloodVisionTicks > 0 && !renderingBloodVision) {//TODO 1.14 entity outlines are rendered above the entity and not synced with player sneaking
            Entity entity = event.getEntity();

            boolean flag = true;
            if (entity instanceof PlayerEntity && HunterCoatItem.isFullyEquipped((PlayerEntity) entity)) flag = false;

            if (mc.player.getDistanceSq(entity) > Balance.vps.BLOOD_VISION_DISTANCE_SQUARED) {
                flag = false;
            }
            if (flag) {
                IExtendedCreatureVampirism creature = entity instanceof CreatureEntity ? ExtendedCreature.get((CreatureEntity) entity) : null;
                if (creature != null && creature.getBlood() > 0 && !creature.hasPoisonousBlood()) {
                    renderedEntitiesWithBlood.add(event.getEntity());

                } else if (VampirePlayer.get(mc.player).getSpecialAttributes().blood_vision_garlic && ((creature != null && creature.hasPoisonousBlood()) || Helper.isHunter(entity))) {
                    renderedEntitiesWithGarlicInfused.add(event.getEntity());

                } else {
                    renderedEntitiesWithoutBlood.add(event.getEntity());
                }
            }

        }
    }

    @SubscribeEvent
    public void onRenderLivingSpecialPre(RenderLivingEvent.Specials.Pre event) {
        LivingEntity entity = event.getEntity();
        if (entity instanceof PlayerEntity && HunterPlayer.get((PlayerEntity) entity).getSpecialAttributes().isDisguised()) {
            if (entity.getDistanceSq(this.mc.player) > 4) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onRenderPlayer(RenderPlayerEvent.Pre event) {
        PlayerEntity player = event.getEntityPlayer();
        VampirePlayerSpecialAttributes vampireAttributes = VampirePlayer.get(player).getSpecialAttributes();
        HunterPlayerSpecialAttribute hunterAttributes = HunterPlayer.get(player).getSpecialAttributes();
        if (vampireAttributes.bat) {
            event.setCanceled(true);
            if (entityBat == null) {
                entityBat = EntityType.BAT.create(event.getEntity().getEntityWorld());
                entityBat.setIsBatHanging(false);
            }

            float parTick = event.getPartialRenderTick();
            EntityRenderer renderer = mc.getRenderManager().getRenderer(entityBat);

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
            float f1 = entityBat.prevRotationYaw + (entityBat.rotationYaw - entityBat.prevRotationYaw) * parTick;
            double d0 = player.lastTickPosX + (player.posX - player.lastTickPosX) * parTick;
            double d1 = player.lastTickPosY + (player.posY - player.lastTickPosY) * parTick;
            double d2 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * parTick;
            Entity entity = mc.getRenderViewEntity();
            double d3 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double) parTick;
            double d4 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double) parTick;
            double d5 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double) parTick;
            mc.getRenderManager().renderEntity(entityBat, d0 - d3, d1 - d4, d2 - d5, f1, event.getPartialRenderTick(), false);

        } else if (hunterAttributes.isDisguised()) {
            if (!player.equals(this.mc.player)) {
                double distSq = player.getDistanceSq(this.mc.player);
                if (distSq > Balance.hpa.DISGUISE_DISTANCE_INVISIBLE_SQ) {
                    event.setCanceled(true);
                } else {
                    hunterDisguiseEnabled = true;
                    enableProfile(Profile.HUNTER_DISGUISE, MathHelper.clamp((float) (distSq / Balance.hpa.DISGUISE_DISTANCE_INVISIBLE_SQ * 25), 0, 1) * hunterAttributes.getDisguiseProgress());
                }
            }


        }
    }

    @SubscribeEvent
    public void onRenderPlayerPost(RenderPlayerEvent.Post event) {
        if (hunterDisguiseEnabled) {
            disableProfile(Profile.HUNTER_DISGUISE);
        }
    }

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        if (mc.world == null) return;

        if (bloodVisionTicks > 0) {

            renderBloodVisionOutlines((bloodVisionTicks + (bloodVisionTicks - lastBloodVisionTicks) * event.getPartialTicks()) / (float) BLOOD_VISION_FADE_TICKS, event.getPartialTicks());

        }
        if (vampireBiomeTicks > 0) {
            renderVampireBiomeFog(vampireBiomeTicks);
        }
        if (displayHeight != mc.mainWindow.getHeight() || displayWidth != mc.mainWindow.getWidth()) {
            this.displayHeight = mc.mainWindow.getHeight();
            this.displayWidth = mc.mainWindow.getWidth();
            if (GLX.isNextGen() && isRenderEntityOutlines()) {
                blurShader.createBindFramebuffers(displayWidth, displayHeight);
                bloodVisionShader1.createBindFramebuffers(displayWidth, displayHeight);
                bloodVisionShader2.createBindFramebuffers(displayWidth, displayHeight);
                bloodVisionShader3.createBindFramebuffers(displayWidth, displayHeight);


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

    private void makeBloodVisionShader() {
        if (GLX.isNextGen()) {
            if (ShaderLinkHelper.getStaticShaderLinkHelper() == null) {
                ShaderLinkHelper.setNewStaticShaderLinkHelper();
            }

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


                this.blurShader.createBindFramebuffers(this.mc.mainWindow.getFramebufferWidth(), this.mc.mainWindow.getFramebufferHeight());
                this.bloodVisionShader1.createBindFramebuffers(this.mc.mainWindow.getFramebufferWidth(), this.mc.mainWindow.getFramebufferHeight());
                this.bloodVisionShader2.createBindFramebuffers(this.mc.mainWindow.getFramebufferWidth(), this.mc.mainWindow.getFramebufferHeight());
                this.bloodVisionShader3.createBindFramebuffers(this.mc.mainWindow.getFramebufferWidth(), this.mc.mainWindow.getFramebufferHeight());

            } catch (IOException | JsonSyntaxException ioexception) {

                LOGGER.error("Failed to load shader", ioexception);
                this.bloodVisionShader1 = null;
                this.bloodVisionFrameBuffer1 = null;
                this.bloodVisionShader2 = null;
                this.bloodVisionFrameBuffer2 = null;
                this.bloodVisionShader3 = null;
                this.bloodVisionFrameBuffer3 = null;
            }
        } else {
            this.bloodVisionShader1 = null;
            this.bloodVisionFrameBuffer1 = null;
            this.bloodVisionShader2 = null;
            this.bloodVisionFrameBuffer2 = null;
            this.bloodVisionShader3 = null;
            this.bloodVisionFrameBuffer3 = null;
        }
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

        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);


        this.bloodVisionFrameBuffer1.framebufferRenderExt(this.mc.mainWindow.getFramebufferWidth(), this.mc.mainWindow.getFramebufferHeight(), false);
        //this.mc.getFramebuffer().bindFramebuffer(false);
        this.bloodVisionFrameBuffer2.framebufferRenderExt(this.mc.mainWindow.getFramebufferWidth(), this.mc.mainWindow.getFramebufferHeight(), false);
        this.bloodVisionFrameBuffer3.framebufferRenderExt(this.mc.mainWindow.getFramebufferWidth(), this.mc.mainWindow.getFramebufferHeight(), false);

        this.mc.getFramebuffer().bindFramebuffer(false);


        GlStateManager.disableBlend();


    }

    private boolean renderEntityOutlines(List<? extends Entity> entities, ShaderGroup shader, Framebuffer framebuffer, float partialTicks) {
        EntityRendererManager renderManager = mc.getRenderManager();
        mc.world.getProfiler().startSection("bloodVision");
        framebuffer.framebufferClear(Minecraft.IS_RUNNING_ON_MAC);
        boolean flag = !entities.isEmpty();
        if (flag) {
            renderingBloodVision = true;
            Entity entity = this.mc.getRenderViewEntity();
            double d3 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double) partialTicks;
            double d4 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double) partialTicks;
            double d5 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double) partialTicks;

            renderManager.setRenderPosition(d3, d4, d5);
            this.mc.gameRenderer.enableLightmap();

            GlStateManager.depthFunc(519);
            GlStateManager.disableLighting();
            GlStateManager.disableColorMaterial();
            GlStateManager.disableDepthTest();

            framebuffer.bindFramebuffer(false);

            RenderHelper.disableStandardItemLighting();

            renderManager.setRenderOutlines(true);
            for (Entity entity1 : entities) {


                renderManager.renderEntityStatic(entity1, partialTicks, false);

            }

            renderManager.setRenderOutlines(false);

            RenderHelper.enableStandardItemLighting();

            GlStateManager.depthMask(false);
            shader.render(partialTicks);
            GlStateManager.depthMask(true);

            //GlStateManager.enableLighting();
            //GlStateManager.enableBlend();
            GlStateManager.enableColorMaterial();
            GlStateManager.depthFunc(515);
            GlStateManager.enableDepthTest();
            //GlStateManager.enableAlpha();
            this.mc.gameRenderer.disableLightmap();
            renderingBloodVision = false;
        }

        this.mc.getFramebuffer().bindFramebuffer(false);

        mc.world.getProfiler().endSection();

        return flag;
    }

    private void renderVampireBiomeFog(int ticks) {

        float f = ((float) VAMPIRE_BIOME_FADE_TICKS) / (float) ticks / 1.5F;
        f *= vampireBiomeFogDistanceMultiplier;
        GlStateManager.pushMatrix();
        boolean fog = GL11.glIsEnabled(GL11.GL_FOG);
        if (!fog)
            GlStateManager.enableFog();
        GlStateManager.fogMode(GlStateManager.FogMode.LINEAR);
        GlStateManager.fogStart(6.0F * f);
        GlStateManager.fogEnd(75F * f);
        GlStateManager.normal3f(0F, -1F, 0F);
        GlStateManager.color4f(1F, 1F, 1F, 1F);
        GlStateManager.fogDensity(1);
        if (!fog)
            GlStateManager.disableFog();
        GlStateManager.popMatrix();

    }

    private enum Profile {
        HUNTER_DISGUISE {
            @Override
            public void apply(float progress) {
                GlStateManager.color4f(1F, 1F, 1F, 1 - progress * 0.8F);
                if (progress >= 1F) {
                    GlStateManager.depthMask(false);
                }
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                GlStateManager.alphaFunc(516, 0.003921569F);
            }

            @Override
            public void clean() {
                GlStateManager.disableBlend();
                GlStateManager.alphaFunc(516, 0.1F);
                GlStateManager.depthMask(true);
                GlStateManager.color4f(1F, 1F, 1F, 1F);
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
