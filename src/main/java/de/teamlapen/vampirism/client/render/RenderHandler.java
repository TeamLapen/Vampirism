package de.teamlapen.vampirism.client.render;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
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
import net.minecraft.client.renderer.OutlineLayerBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
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
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.*;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.resource.IResourceType;
import net.minecraftforge.resource.ISelectiveResourceReloadListener;
import net.minecraftforge.resource.VanillaResourceType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

import static org.lwjgl.opengl.GL11.GL_QUAD_STRIP;

/**
 * Handle most general rendering related stuff
 */
@OnlyIn(Dist.CLIENT)
public class RenderHandler implements ISelectiveResourceReloadListener {
    private static final ResourceLocation saturation1 = new ResourceLocation(REFERENCE.MODID + ":shaders/saturation1.json");
    private final Minecraft mc;
    private OutlineLayerBuffer bloodVisionBuffer;
    private final int BLOOD_VISION_FADE_TICKS = 80;

    private final int VAMPIRE_BIOME_FADE_TICKS = 60;
    private static final int ENTITY_NEAR_SQ_DISTANCE = 100;
    private final int ENTITY_RADIUS = 100;
    private final int ENTITY_MIN_SQ_RADIUS = 10;
    private int entityDisplayListId = 0;
    private int entitySphereListId = 0;
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

    private ShaderGroup bloodVisionShader;
    private Framebuffer bloodVisionFramebuffer;
    private ShaderGroup blurShader;


    private final boolean doSaturationShader = true;
    /**
     * Store the last used framebuffer size to be able to rebind shader buffer when size changes
     */
    private int displayHeight, displayWidth;
    private boolean renderingBloodVision = false;
    /**
     * Temporarily stores if the hunter disguise blend profile has been enabled. (From RenderPlayer.Pre to RenderPlayer.Post)
     */
    private boolean hunterDisguiseEnabled;

    public RenderHandler(Minecraft mc) {
        this.mc = mc;
        entityDisplayListId = GL11.glGenLists(2);
        entitySphereListId = entityDisplayListId + 1;
        this.buildEntitySphere();
    }

    @Nullable
    @Override
    public IResourceType getResourceType() {
        return VanillaResourceType.SHADERS;
    }


    @SubscribeEvent
    public void onRenderHand(RenderHandEvent event) {
        if (mc.player.isAlive() && VampirePlayer.get(mc.player).getActionHandler().isActionActive(VampireActions.bat)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onCameraSetup(EntityViewRenderEvent.CameraSetup event) {
//        if (this.isRenderEntityOutlines()) {
//            this.bloodVisionFramebuffer.framebufferClear(Minecraft.IS_RUNNING_ON_MAC);
//            this.mc.getFramebuffer().bindFramebuffer(false);
//        }
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

//    private void adjustBloodVisionShaders(float progress) {
//        progress = MathHelper.clamp(progress, 0, 1);
//
//        blit0.getShaderManager().getShaderUniform("ColorModulate").set((1 - 0.8F * progress), (1 - 0.9F * progress), (1 - 0.7F * progress), 1);
//        blur1.getShaderManager().getShaderUniform("Radius").set(Math.round(10 * progress) / 1F);
//        blur2.getShaderManager().getShaderUniform("Radius").set(Math.round(10 * progress) / 1F);
//        blit1.getShaderManager().getShaderUniform("ColorModulate").set(1F, 0.1F, 0.1F, (1F * progress));
//        blit2.getShaderManager().getShaderUniform("ColorModulate").set(0.1F, 0.1F, 0.2F, (0.7F * progress));
//        blit3.getShaderManager().getShaderUniform("ColorModulate").set(0.1F, 0.1F, 1F, (1F * progress));
//
//    }

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

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (mc.world == null || mc.player == null || !mc.player.isAlive()) return;
        if (event.phase == TickEvent.Phase.END) return;
        lastBloodVisionTicks = bloodVisionTicks;
        this.compileEntitys();
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

//        if (shaderWarning && mc.player != null) {
//            shaderWarning = false;
//            showedShaderWarning = true;
//            mc.player.sendMessage(new StringTextComponent("Blood vision does not work on your hardware, because shaders are not supported"));
//            mc.player.sendMessage(new StringTextComponent("If you are running on recent hardware and use updated drivers, but this still shows up, please contact the author of Vampirism"));
//        }


    }

    private Shader blur1, blur2, blit0;

    /**
     * Compiles a render list of the entitys nearby
     */
    private void compileEntitys() {
        GL11.glNewList(entityDisplayListId, GL11.GL_COMPILE);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);

        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glShadeModel(GL11.GL_SMOOTH);

        World world = this.mc.world;

        PlayerEntity player = this.mc.player;
        if ((world == null) || (player == null))
            return;

        List list = world.getEntitiesWithinAABBExcludingEntity(player, player.getBoundingBox().expand(ENTITY_RADIUS, ENTITY_RADIUS, ENTITY_RADIUS));
        for (Object o : list) {
            if (o instanceof LivingEntity) {
                LivingEntity e = (LivingEntity) o;
                int distance = (int) e.getDistanceSq(player);
                if (distance <= ENTITY_NEAR_SQ_DISTANCE && (distance >= ENTITY_MIN_SQ_RADIUS)) {
                    // ||!player.canEntityBeSeen(e)
                    renderEntity(e, (((float) distance - ENTITY_MIN_SQ_RADIUS) / (ENTITY_NEAR_SQ_DISTANCE - ENTITY_MIN_SQ_RADIUS)));
                } else if (distance > ENTITY_NEAR_SQ_DISTANCE) {
                    renderEntity(e, 1F);
                }

            }
        }

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEndList();
    }

    /**
     * Renders the sphere around the give entity
     *
     * @param entity
     * @param f
     */
    private void renderEntity(LivingEntity entity, float f) {
        float red = 1.0F;
        float green = 0.0F;
        float blue = 0.0F;

//        if (entity instanceof EntityCreature) {
//            if (VampireMob.get((EntityCreature) entity).isVampire()) {
//                red = 0.23127F;
//                green = 0.04313F;
//                blue = 0.04313F;
//
//            } else if (!(VampireMob.get((EntityCreature) entity).getBlood() > 0)) {
//                red = 0.039215F;
//                green = 0.0745F;
//                blue = 0.1647F;
//            }
//        } else if (entity instanceof EntityPlayer) {
//            if (VampirePlayer.get((EntityPlayer) entity).getLevel() > 0) {
//                red = 0.039215F;
//                green = 0.0745F;
//                blue = 0.1647F;
//            }
//        }
        GL11.glPushMatrix();
        GL11.glTranslated(entity.getPosX(), entity.getPosY() + entity.getHeight() / 2, entity.getPosZ());
        GL11.glScalef(entity.getWidth() * 1.5F, entity.getHeight() * 1.5F, entity.getWidth() * 1.5F);
        GL11.glColor4f(red, green, blue, 0.5F * f);
        GL11.glCallList(entitySphereListId);
        GL11.glPopMatrix();

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
    public void onRenderLivingPost(RenderLivingEvent.Post event) {
        if (!renderingBloodVision && isRenderEntityOutlines()) {
            Entity entity = event.getEntity();

            boolean flag = true;
            if (entity instanceof PlayerEntity && HunterCoatItem.isFullyEquipped((PlayerEntity) entity)) flag = false;
            double dist = mc.player.getDistanceSq(entity);
            if (dist > VampirismConfig.BALANCE.vsBloodVisionDistSQ.get()) {
                flag = false;
            }
            if (flag) {
                int color;
                LazyOptional<IExtendedCreatureVampirism> opt = entity instanceof CreatureEntity && entity.isAlive() ? ExtendedCreature.getSafe(entity) : LazyOptional.empty();
                if (opt.map(creature -> creature.getBlood() > 0 && creature.hasPoisonousBlood()).orElse(false)) {
                    renderedEntitiesWithBlood.add(event.getEntity());

                } else if (VampirePlayer.getOpt(mc.player).map(VampirePlayer::getSpecialAttributes).map(s -> s.blood_vision_garlic).orElse(false) && ((opt.map(IExtendedCreatureVampirism::hasPoisonousBlood).orElse(false)) || Helper.isHunter(entity))) {
                    renderedEntitiesWithGarlicInfused.add(event.getEntity());

                } else {
                    renderedEntitiesWithoutBlood.add(event.getEntity());
                }
                IExtendedCreatureVampirism creature = entity instanceof CreatureEntity && entity.isAlive() ? ExtendedCreature.getUnsafe((CreatureEntity) entity) : null;
                if (creature != null && creature.getBlood() > 0 && !creature.hasPoisonousBlood()) {
                    color = 0xFF0000;

                } else if (VampirePlayer.getOpt(mc.player).map(VampirePlayer::getSpecialAttributes).map(s -> s.blood_vision_garlic).orElse(false) && ((creature != null && creature.hasPoisonousBlood()) || Helper.isHunter(entity))) {
                    color = 0xFFFFFF;

                } else {
                    color = 0xA0A0A0;
                }
                EntityRendererManager renderManager = mc.getRenderManager();
                Vec3d view = mc.gameRenderer.getActiveRenderInfo().getProjectedView();
                if (bloodVisionBuffer == null) {
                    bloodVisionBuffer = new OutlineLayerBuffer(mc.getRenderTypeBuffers().getBufferSource());
                }
                int r = color >> 16 & 255;
                int g = color >> 8 & 255;
                int b = color & 255;
                int alpha = dist > 100 ? 50 : (int) (dist / 100d * 50d);
                bloodVisionBuffer.setColor(r, g, b, alpha);
                double d0 = MathHelper.lerp(event.getPartialRenderTick(), entity.lastTickPosX, entity.getPosX());
                double d1 = MathHelper.lerp(event.getPartialRenderTick(), entity.lastTickPosY, entity.getPosY());
                double d2 = MathHelper.lerp(event.getPartialRenderTick(), entity.lastTickPosZ, entity.getPosZ());
                float f = MathHelper.lerp(event.getPartialRenderTick(), entity.prevRotationYaw, entity.rotationYaw);
                renderingBloodVision = true;
                bloodVisionFramebuffer.bindFramebuffer(false);
                EntityRenderer<? super Entity> entityrenderer = renderManager.getRenderer(entity);
                entityrenderer.render(entity, f, event.getPartialRenderTick(), event.getMatrixStack(), bloodVisionBuffer, renderManager.getPackedLight(entity, event.getPartialRenderTick()));
//                event.getMatrixStack().pop();
//                renderManager.renderEntityStatic(entity, d0 - view.x, d1 - view.y, d2 - view.z, f, event.getPartialRenderTick(), event.getMatrixStack(), bloodVisionBuffer, renderManager.getPackedLight(entity, event.getPartialRenderTick()));
//                event.getMatrixStack().push();
                mc.getFramebuffer().bindFramebuffer(false);
                renderingBloodVision = false;

            }

        }
    }

    /**
     * Builds and saves a sphere for entitys
     */
    private void buildEntitySphere() {
        GL11.glNewList(entitySphereListId, GL11.GL_COMPILE);

        int lats = 32, longs = 32;
        double r = 0.5;
        int i, j;
        for (i = 0; i <= lats; i++) {
            double lat0 = Math.PI * (-0.5 + (double) (i - 1) / lats);
            double z0 = Math.sin(lat0);
            double zr0 = Math.cos(lat0);

            double lat1 = Math.PI * (-0.5 + (double) i / lats);
            double z1 = Math.sin(lat1);
            double zr1 = Math.cos(lat1);

            GL11.glBegin(GL_QUAD_STRIP);
            for (j = 0; j <= longs; j++) {
                double lng = 2 * Math.PI * (double) (j - 1) / longs;
                double x = Math.cos(lng);
                double y = Math.sin(lng);

                GL11.glNormal3d(x * zr0, y * zr0, z0);
                GL11.glVertex3d(r * x * zr0, r * y * zr0, r * z0);
                GL11.glNormal3d(x * zr1, y * zr1, z1);
                GL11.glVertex3d(r * x * zr1, r * y * zr1, r * z1);
            }
            GL11.glEnd();
        }


        GL11.glEndList();

    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager, Predicate<IResourceType> resourcePredicate) {
        this.reMakeBloodVisionShader();

    }

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        if (mc.world == null) return;

        /*
         * DO NOT USE partial ticks from event. They are bugged: https://github.com/MinecraftForge/MinecraftForge/issues/6380
         */
        float partialTicks = mc.getRenderPartialTicks();


        if (displayHeight != mc.getMainWindow().getFramebufferHeight() || displayWidth != mc.getMainWindow().getFramebufferWidth()) {
            this.displayHeight = mc.getMainWindow().getFramebufferHeight();
            this.displayWidth = mc.getMainWindow().getFramebufferWidth();
            this.updateFramebufferSize(this.displayWidth, this.displayHeight);
        }

        GL11.glPushMatrix();
        GL11.glTranslated(-this.mc.player.getPosX(), -this.mc.player.getPosY(), -this.mc.player.getPosZ());
        GL11.glCallList(entityDisplayListId);
        GL11.glPopMatrix();

        if (bloodVisionTicks > 0 && isRenderEntityOutlines()) {
            adjustBloodVisionShaders((bloodVisionTicks + (bloodVisionTicks - lastBloodVisionTicks) * partialTicks) / (float) BLOOD_VISION_FADE_TICKS);
            this.blurShader.render(partialTicks);
            this.bloodVisionBuffer.finish();
        }

//        if (this.isRenderEntityOutlines()) {
//            if (bloodVisionBuffer != null) {
//                bloodVisionBuffer.finish();
//            }
//            this.bloodVisionShader.render(partialTicks);
//            this.mc.getFramebuffer().bindFramebuffer(false);
//        }
//
//        if (isRenderEntityOutlines()) {
//            this.renderBloodVisionFramebuffer();
//            RenderSystem.disableBlend();
//            RenderSystem.disableDepthTest();
//            RenderSystem.disableAlphaTest();
//            RenderSystem.enableTexture();
//            RenderSystem.matrixMode(5890);
//            RenderSystem.pushMatrix();
//            RenderSystem.loadIdentity();
//            this.blurShader.render(partialTicks);
//            RenderSystem.popMatrix();
//        }


    }

    private boolean isRenderEntityOutlines() {
        return this.bloodVisionTicks > 0 && this.bloodVisionShader != null && this.bloodVisionFramebuffer != null && this.blurShader != null && this.mc.player != null;
    }

    private void adjustBloodVisionShaders(float progress) {
        progress = MathHelper.clamp(progress, 0, 1);
        //blit0.getShaderManager().getShaderUniform("ColorModulate").set((1f ), (1f ), (1f ), 1);
        blit0.getShaderManager().getShaderUniform("ColorModulate").set((1 - 0.4F * progress), (1 - 0.5F * progress), (1 - 0.3F * progress), 1);
        blur1.getShaderManager().getShaderUniform("Radius").set(Math.round(15 * progress) / 1F);
        blur2.getShaderManager().getShaderUniform("Radius").set(Math.round(15 * progress) / 1F);

    }

    private void renderBloodVisionFramebuffer() {
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
        this.bloodVisionFramebuffer.framebufferRenderExt(this.mc.getMainWindow().getFramebufferWidth(), this.mc.getMainWindow().getFramebufferHeight(), false);
        RenderSystem.disableBlend();
    }

    private void renderEntityOutlines(List<? extends Entity> entities, int color, MatrixStack matrixStack, float partialTicks) {
        EntityRendererManager renderManager = mc.getRenderManager();
        Vec3d view = mc.gameRenderer.getActiveRenderInfo().getProjectedView();
        for (Entity e : renderedEntitiesWithBlood) {
            OutlineLayerBuffer outlinelayerbuffer = mc.getRenderTypeBuffers().getOutlineBufferSource();
            int r = color >> 16 & 255;
            int g = color >> 8 & 255;
            int b = color & 255;
            outlinelayerbuffer.setColor(r, g, b, 255);
            double d0 = MathHelper.lerp(partialTicks, e.lastTickPosX, e.getPosX());
            double d1 = MathHelper.lerp(partialTicks, e.lastTickPosY, e.getPosY());
            double d2 = MathHelper.lerp(partialTicks, e.lastTickPosZ, e.getPosZ());
            float f = MathHelper.lerp(partialTicks, e.prevRotationYaw, e.rotationYaw);
            renderManager.renderEntityStatic(e, d0 - view.x, d1 - view.y, d2 - view.z, f, partialTicks, matrixStack, outlinelayerbuffer, renderManager.getPackedLight(e, partialTicks));
        }
    }

//    private void makeBloodVisionShader() {
//
//            ResourceLocation resourcelocationOutline = new ResourceLocation(REFERENCE.MODID, "shaders/blood_vision_outline.json");
//            ResourceLocation resourcelocationBlur = new ResourceLocation(REFERENCE.MODID, "shaders/blank.json");
//
//            try {
//                this.blurShader = new ShaderGroup(this.mc.getTextureManager(), this.mc.getResourceManager(), this.mc.getFramebuffer(), resourcelocationBlur);
//                Framebuffer swap = this.blurShader.getFramebufferRaw("swap");
//
//                blit0 = blurShader.addShader("blit", swap, this.mc.getFramebuffer());
//                blur1 = blurShader.addShader("blur", this.mc.getFramebuffer(), swap);
//                blur1.getShaderManager().getShaderUniform("BlurDir").set(1F, 0F);
//                blur2 = blurShader.addShader("blur", swap, this.mc.getFramebuffer());
//                blur2.getShaderManager().getShaderUniform("BlurDir").set(0F, 1F);
//
//
//                this.bloodVisionShader1 = new ShaderGroup(this.mc.getTextureManager(), this.mc.getResourceManager(), this.mc.getFramebuffer(), resourcelocationOutline);
//                swap = this.bloodVisionShader1.getFramebufferRaw("swap");
//                this.bloodVisionFrameBuffer1 = this.bloodVisionShader1.getFramebufferRaw("final");
//
//                blit1 = bloodVisionShader1.addShader("blit", swap, bloodVisionFrameBuffer1);
//
//                this.bloodVisionShader2 = new ShaderGroup(this.mc.getTextureManager(), this.mc.getResourceManager(), this.mc.getFramebuffer(), resourcelocationOutline);
//                swap = this.bloodVisionShader2.getFramebufferRaw("swap");
//                this.bloodVisionFrameBuffer2 = this.bloodVisionShader2.getFramebufferRaw("final");
//
//                blit2 = bloodVisionShader2.addShader("blit", swap, bloodVisionFrameBuffer2);
//
//                this.bloodVisionShader3 = new ShaderGroup(this.mc.getTextureManager(), this.mc.getResourceManager(), this.mc.getFramebuffer(), resourcelocationOutline);
//                swap = this.bloodVisionShader3.getFramebufferRaw("swap");
//                this.bloodVisionFrameBuffer3 = this.bloodVisionShader3.getFramebufferRaw("final");
//
//                blit3 = bloodVisionShader3.addShader("blit", swap, bloodVisionFrameBuffer3);
//
//
//                this.blurShader.createBindFramebuffers(this.mc.getMainWindow().getFramebufferWidth(), this.mc.getMainWindow().getFramebufferHeight());
//                this.bloodVisionShader1.createBindFramebuffers(this.mc.getMainWindow().getFramebufferWidth(), this.mc.getMainWindow().getFramebufferHeight());
//                this.bloodVisionShader2.createBindFramebuffers(this.mc.getMainWindow().getFramebufferWidth(), this.mc.getMainWindow().getFramebufferHeight());
//                this.bloodVisionShader3.createBindFramebuffers(this.mc.getMainWindow().getFramebufferWidth(), this.mc.getMainWindow().getFramebufferHeight());
//
//            } catch (IOException | JsonSyntaxException ioexception) {
//
//                LOGGER.error("Failed to load shader", ioexception);
//                this.bloodVisionShader1 = null;
//                this.bloodVisionFrameBuffer1 = null;
//                this.bloodVisionShader2 = null;
//                this.bloodVisionFrameBuffer2 = null;
//                this.bloodVisionShader3 = null;
//                this.bloodVisionFrameBuffer3 = null;
//            }}

    private void updateFramebufferSize(int width, int height) {
        if (this.blurShader != null) {
            this.blurShader.createBindFramebuffers(width, height);
        }
        if (this.bloodVisionShader != null) {
            this.bloodVisionShader.createBindFramebuffers(width, height);
        }
    }

    private void reMakeBloodVisionShader() {
        if (this.bloodVisionShader != null) {
            this.bloodVisionShader.close();
        }
        if (this.blurShader != null) {
            this.blurShader.close();
        }
        ResourceLocation resourcelocationOutline = new ResourceLocation(REFERENCE.MODID, "shaders/blood_vision_outline.json");
        ResourceLocation resourcelocationBlur = new ResourceLocation(REFERENCE.MODID, "shaders/blank.json");
        try {
            this.bloodVisionShader = new ShaderGroup(this.mc.getTextureManager(), this.mc.getResourceManager(), this.mc.getFramebuffer(), resourcelocationOutline);
            this.bloodVisionShader.createBindFramebuffers(this.mc.getMainWindow().getFramebufferWidth(), this.mc.getMainWindow().getFramebufferHeight());
            this.bloodVisionFramebuffer = this.bloodVisionShader.getFramebufferRaw("final");
        } catch (Exception e) {
            LOGGER.warn("Failed to load blood vision shader", e);
            this.bloodVisionShader = null;
            this.bloodVisionFramebuffer = null;
        }
        try {
            this.blurShader = new ShaderGroup(this.mc.getTextureManager(), this.mc.getResourceManager(), this.mc.getFramebuffer(), resourcelocationBlur);
            Framebuffer swap = this.blurShader.getFramebufferRaw("swap");

            blit0 = blurShader.addShader("blit", swap, this.mc.getFramebuffer());
            blur1 = blurShader.addShader("blur", this.mc.getFramebuffer(), swap);
            blur1.getShaderManager().getShaderUniform("BlurDir").set(1F, 0F);
            blur2 = blurShader.addShader("blur", swap, this.mc.getFramebuffer());
            blur2.getShaderManager().getShaderUniform("BlurDir").set(0F, 1F);

            this.blurShader.createBindFramebuffers(this.mc.getMainWindow().getFramebufferWidth(), this.mc.getMainWindow().getFramebufferHeight());

        } catch (Exception e) {
            LOGGER.warn("Failed to load blood vision blur shader", e);
            this.blurShader = null;
        }
    }


//    private void renderBloodVisionOutlines(MatrixStack matrixStack,float progress, float partialTicks) {
//        if (!bloodVisionShaderInit) {
//
//            makeBloodVisionShader();
//            bloodVisionShaderInit = true;
//        }
//        if (!isRenderEntityOutlines()) {
//            if (!showedShaderWarning) {
//                shaderWarning = true;
//            }
//            return;
//        }
//
//        adjustBloodVisionShaders(progress);
//
//        blurShader.render(partialTicks);
//
//
//        if (!renderedEntitiesWithBlood.isEmpty() || this.bloodVision1Rendered) {
//            bloodVision1Rendered = renderEntityOutlines(renderedEntitiesWithBlood,matrixStack,  bloodVisionFrameBuffer1, partialTicks);
//        }
//        renderedEntitiesWithBlood.clear();
//
//        if (!renderedEntitiesWithoutBlood.isEmpty() || this.bloodVision2Rendered) {
//            bloodVision2Rendered = renderEntityOutlines(renderedEntitiesWithoutBlood, matrixStack, bloodVisionFrameBuffer2, partialTicks);
//        }
//
//        renderedEntitiesWithoutBlood.clear();
//
//        if (!renderedEntitiesWithGarlicInfused.isEmpty() || this.bloodVision3Rendered) {
//            bloodVision3Rendered = renderEntityOutlines(renderedEntitiesWithGarlicInfused, matrixStack, bloodVisionFrameBuffer3, partialTicks);
//        }
//
//        renderedEntitiesWithGarlicInfused.clear();
//
//        RenderSystem.enableBlend();
//        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
//
//
//        this.bloodVisionFrameBuffer1.framebufferRenderExt(this.mc.getMainWindow().getFramebufferWidth(), this.mc.getMainWindow().getFramebufferHeight(), false);
//        //this.mc.getFramebuffer().bindFramebuffer(false);
//        this.bloodVisionFrameBuffer2.framebufferRenderExt(this.mc.getMainWindow().getFramebufferWidth(), this.mc.getMainWindow().getFramebufferHeight(), false);
//        this.bloodVisionFrameBuffer3.framebufferRenderExt(this.mc.getMainWindow().getFramebufferWidth(), this.mc.getMainWindow().getFramebufferHeight(), false);
//
//        this.mc.getFramebuffer().bindFramebuffer(false);
//
//
//        RenderSystem.disableBlend();
//
//
//    }


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
