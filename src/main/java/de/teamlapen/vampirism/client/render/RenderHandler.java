package de.teamlapen.vampirism.client.render;

import com.google.common.collect.Lists;
import com.google.gson.JsonSyntaxException;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.config.Configs;
import de.teamlapen.vampirism.core.ModPotions;
import de.teamlapen.vampirism.entity.ExtendedCreature;
import de.teamlapen.vampirism.items.ItemHunterCoat;
import de.teamlapen.vampirism.player.hunter.HunterPlayer;
import de.teamlapen.vampirism.player.hunter.HunterPlayerSpecialAttribute;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.player.vampire.VampirePlayerSpecialAttributes;
import de.teamlapen.vampirism.player.vampire.actions.VampireActions;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.REFERENCE;
import de.teamlapen.vampirism.util.SRGNAMES;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.Shader;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.client.shader.ShaderLinkHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.*;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Handle most general rendering related stuff
 */
@SideOnly(Side.CLIENT)
public class RenderHandler {
    private static final ResourceLocation saturation1 = new ResourceLocation(REFERENCE.MODID + ":shaders/saturation1.json");
    private final Minecraft mc;
    private final int BLOOD_VISION_FADE_TICKS = 80;

    private final int VAMPIRE_BIOME_FADE_TICKS = 160;
    private final String TAG = "RenderHandler";
    private final List<EntityLivingBase> renderedEntitiesWithBlood = Lists.newLinkedList();
    private final List<EntityLivingBase> renderedEntitiesWithoutBlood = Lists.newLinkedList();
    private EntityBat entityBat;
    private int vampireBiomeTicks = 0;
    private int bloodVisionTicks = 0;
    private int lastBloodVisionTicks = 0;
    private float vampireBiomeFogDistanceMultiplier = 1;
    private Framebuffer bloodVisionFrameBuffer1;
    private Framebuffer bloodVisionFrameBuffer2;
    private ShaderGroup bloodVisionShader1;
    private ShaderGroup bloodVisionShader2;
    private ShaderGroup blurShader;
    private boolean bloodVisionShaderInit = false;
    private boolean bloodVision1Rendered = false;
    private boolean bloodVision2Rendered = false;
    private boolean shaderWarning = false;
    private boolean showedShaderWarning = false;
    private boolean doSaturationShader = true;
    private int displayHeight, displayWidth;
    private boolean renderingBloodVision = false;
    private Shader blur1, blur2, blit0, blit1, blit2;
    /**
     * Temporarily stores if the hunter disguise blend profile has been enabled. (From RenderPlayer.Pre to RenderPlayer.Post)
     */
    private boolean hunterDisguiseEnabled;

    public RenderHandler(Minecraft mc) {
        this.mc = mc;
        this.displayHeight = mc.displayHeight;
        this.displayWidth = mc.displayWidth;
        if (OpenGlHelper.areShadersSupported()) {
            VampirismMod.log.w(TAG, "Shaders are not supported, Blood vision won't work");
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (mc.world == null) return;
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

        if (Configs.renderVampireForestFog && VReference.castleDimId != mc.world.provider.getDimension() && Helper.isEntityInVampireBiome(mc.player)) {
            if (vampireBiomeTicks < VAMPIRE_BIOME_FADE_TICKS) {
                vampireBiomeTicks++;
            }
            vampireBiomeFogDistanceMultiplier = vampire.getSpecialAttributes().increasedVampireFogDistance ? 2 : 1;
        } else {
            if (vampireBiomeTicks > 0) {
                vampireBiomeTicks--;
            }
        }

        if (OpenGlHelper.areShadersSupported() && doSaturationShader) {
            if (mc.player != null && mc.player.getRNG().nextInt(10) == 3) {
                PotionEffect pe = mc.player.getActivePotionEffect(ModPotions.saturation);
                boolean active = pe != null && pe.getAmplifier() >= 2;
                EntityRenderer renderer = mc.entityRenderer;
                if (active && !renderer.isShaderActive()) {
                    //Load saturation shader if not active
                    try {
                        Method method = ReflectionHelper.findMethod(EntityRenderer.class, renderer, new String[]{"loadShader", SRGNAMES.EntityRenderer_loadShader}, ResourceLocation.class);
                        method.invoke(renderer, saturation1);
                    } catch (Exception e) {
                        doSaturationShader = false;
                        VampirismMod.log.e("RenderHandler", e, "Failed to activate saturation shader");
                    }
                } else if (!active && renderer.isShaderActive() && renderer.getShaderGroup().getShaderGroupName().equals(saturation1.toString())) {
                    renderer.stopUseShader();
                }
            }
        }

        if (shaderWarning) {
            shaderWarning = false;
            showedShaderWarning = true;
            mc.player.sendMessage(new TextComponentString("Blood vision does not work on your hardware, because shaders are not supported"));
            mc.player.sendMessage(new TextComponentString("If you are running on recent hardware and use updated drivers, but this still shows up, please contact the author of Vampirism"));
        }


    }

    @SubscribeEvent
    public void onFogDensity(EntityViewRenderEvent.FogDensity event) {
        if (event.getEntity() instanceof EntityPlayer) {
            if (vampireBiomeTicks > 10 || bloodVisionTicks > 0) {
                event.setDensity(1.0F);
                event.setCanceled(true);
            }
        }

    }

    @SubscribeEvent
    public void onRenderHand(RenderHandEvent event) {
        if (VampirePlayer.get(mc.player).getActionHandler().isActionActive(VampireActions.batAction)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onRenderLivingPost(RenderLivingEvent.Post event) {
        if (bloodVisionTicks > 0 && !renderingBloodVision) {
            Entity entity = event.getEntity();

            boolean flag = true;
            if (entity instanceof EntityPlayer && ItemHunterCoat.isFullyEquipped((EntityPlayer) entity)) flag = false;

            if (mc.player.getDistanceSqToEntity(entity) > Balance.vps.BLOOD_VISION_DISTANCE_SQUARED) {
                flag = false;
            }
            if (flag) {
                if (entity instanceof EntityCreature && ExtendedCreature.get((EntityCreature) entity).getBlood() > 0) {
                    renderedEntitiesWithBlood.add(event.getEntity());

                } else {
                    renderedEntitiesWithoutBlood.add(event.getEntity());
                }
            }

        }
    }

    @SubscribeEvent
    public void onRenderLivingSpecialPre(RenderLivingEvent.Specials.Pre event) {
        EntityLivingBase entity = event.getEntity();
        if (entity instanceof EntityPlayer && HunterPlayer.get((EntityPlayer) entity).getSpecialAttributes().isDisguised()) {
            if (entity.getDistanceSqToEntity(this.mc.player) > 4) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onRenderPlayer(RenderPlayerEvent.Pre event) {
        EntityPlayer player = event.getEntityPlayer();
        VampirePlayerSpecialAttributes vampireAttributes = VampirePlayer.get(player).getSpecialAttributes();
        HunterPlayerSpecialAttribute hunterAttributes = HunterPlayer.get(player).getSpecialAttributes();
        if (vampireAttributes.bat) {
            event.setCanceled(true);
            if (entityBat == null) {
                entityBat = new EntityBat(event.getEntity().getEntityWorld());
                entityBat.setIsBatHanging(false);
            }

            float parTick = event.getPartialRenderTick();
            Render renderer = mc.getRenderManager().getEntityRenderObject(entityBat);

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
            mc.getRenderManager().doRenderEntity(entityBat, d0 - d3, d1 - d4, d2 - d5, f1, event.getPartialRenderTick(), false);

        } else if (hunterAttributes.isDisguised()) {
            if (!player.equals(this.mc.player)) {
                double distSq = player.getDistanceSqToEntity(this.mc.player);
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
        if (displayHeight != mc.displayHeight || displayWidth != mc.displayWidth) {
            this.displayHeight = mc.displayHeight;
            this.displayWidth = mc.displayWidth;
            if (OpenGlHelper.areShadersSupported() && isRenderEntityOutlines()) {
                blurShader.createBindFramebuffers(displayWidth, displayHeight);
                bloodVisionShader1.createBindFramebuffers(displayWidth, displayHeight);
                bloodVisionShader2.createBindFramebuffers(displayWidth, displayHeight);


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

    }

    private void disableBloodVision() {
        renderedEntitiesWithoutBlood.clear();
        renderedEntitiesWithBlood.clear();
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
        return this.bloodVisionFrameBuffer1 != null && this.bloodVisionShader1 != null && this.bloodVisionFrameBuffer2 != null && this.bloodVisionShader2 != null && this.mc.player != null;
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
        if (OpenGlHelper.shadersSupported) {
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


                this.blurShader.createBindFramebuffers(this.mc.displayWidth, this.mc.displayHeight);
                this.bloodVisionShader1.createBindFramebuffers(this.mc.displayWidth, this.mc.displayHeight);
                this.bloodVisionShader2.createBindFramebuffers(this.mc.displayWidth, this.mc.displayHeight);

            } catch (IOException | JsonSyntaxException ioexception) {

                VampirismMod.log.e(TAG, ioexception, "Failed to load shader: {%s}", resourcelocationOutline);
                this.bloodVisionShader1 = null;
                this.bloodVisionFrameBuffer1 = null;
                this.bloodVisionShader2 = null;
                this.bloodVisionFrameBuffer2 = null;
            }
        } else {
            this.bloodVisionShader1 = null;
            this.bloodVisionFrameBuffer1 = null;
            this.bloodVisionShader2 = null;
            this.bloodVisionFrameBuffer2 = null;
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

        blurShader.loadShaderGroup(partialTicks);


        if (!renderedEntitiesWithBlood.isEmpty() || this.bloodVision1Rendered) {
            bloodVision1Rendered = renderEntityOutlines(renderedEntitiesWithBlood, bloodVisionShader1, bloodVisionFrameBuffer1, partialTicks);
        }
        renderedEntitiesWithBlood.clear();

        if (!renderedEntitiesWithoutBlood.isEmpty() || this.bloodVision2Rendered) {
            bloodVision2Rendered = renderEntityOutlines(renderedEntitiesWithoutBlood, bloodVisionShader2, bloodVisionFrameBuffer2, partialTicks);
        }

        renderedEntitiesWithoutBlood.clear();


        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);


        this.bloodVisionFrameBuffer1.framebufferRenderExt(this.mc.displayWidth, this.mc.displayHeight, false);
        //this.mc.getFramebuffer().bindFramebuffer(false);
        this.bloodVisionFrameBuffer2.framebufferRenderExt(this.mc.displayWidth, this.mc.displayHeight, false);

        this.mc.getFramebuffer().bindFramebuffer(false);


        GlStateManager.disableBlend();


    }

    private boolean renderEntityOutlines(List<? extends Entity> entities, ShaderGroup shader, Framebuffer framebuffer, float partialTicks) {
        RenderManager renderManager = mc.getRenderManager();
        mc.world.theProfiler.startSection("bloodVision");
        framebuffer.framebufferClear();
        boolean flag = !entities.isEmpty();
        if (flag) {
            renderingBloodVision = true;
            Entity entity = this.mc.getRenderViewEntity();
            double d3 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double) partialTicks;
            double d4 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double) partialTicks;
            double d5 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double) partialTicks;

            renderManager.setRenderPosition(d3, d4, d5);
            this.mc.entityRenderer.enableLightmap();

            GlStateManager.depthFunc(519);
            GlStateManager.disableLighting();
            GlStateManager.disableColorMaterial();
            GlStateManager.disableDepth();

            framebuffer.bindFramebuffer(false);

            RenderHelper.disableStandardItemLighting();

            renderManager.setRenderOutlines(true);
            for (int j = 0; j < entities.size(); ++j) {


                renderManager.renderEntityStatic(entities.get(j), partialTicks, false);

            }

            renderManager.setRenderOutlines(false);

            RenderHelper.enableStandardItemLighting();

            GlStateManager.depthMask(false);
            shader.loadShaderGroup(partialTicks);
            GlStateManager.depthMask(true);

            //GlStateManager.enableLighting();
            //GlStateManager.enableBlend();
            GlStateManager.enableColorMaterial();
            GlStateManager.depthFunc(515);
            GlStateManager.enableDepth();
            //GlStateManager.enableAlpha();
            this.mc.entityRenderer.disableLightmap();
            renderingBloodVision = false;
        }

        this.mc.getFramebuffer().bindFramebuffer(false);

        mc.world.theProfiler.endSection();

        return flag;
    }

    private void renderVampireBiomeFog(int ticks) {

        float f = ((float) VAMPIRE_BIOME_FADE_TICKS) / (float) ticks / 1.5F;
        f *= vampireBiomeFogDistanceMultiplier;
        GlStateManager.pushMatrix();
        boolean fog = GL11.glIsEnabled(GL11.GL_FOG);
        if (!fog)
            GlStateManager.enableFog();
        GlStateManager.setFog(GlStateManager.FogMode.LINEAR);
        GlStateManager.setFogStart(6.0F * f);
        GlStateManager.setFogEnd(75F * f);
        GlStateManager.glNormal3f(0F, -1F, 0F);
        GlStateManager.color(1F, 1F, 1F, 1F);
        GlStateManager.setFogDensity(1);
        if (!fog)
            GlStateManager.disableFog();
        GlStateManager.popMatrix();

    }

    private enum Profile {
        HUNTER_DISGUISE {
            @Override
            public void apply(float progress) {
                GlStateManager.color(1F, 1F, 1F, 1 - progress * 0.8F);
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
                GlStateManager.color(1F, 1F, 1F, 1F);
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
