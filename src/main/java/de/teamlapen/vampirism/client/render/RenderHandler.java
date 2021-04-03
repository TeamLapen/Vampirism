package de.teamlapen.vampirism.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import de.teamlapen.lib.util.OptifineHandler;
import de.teamlapen.vampirism.api.entity.IExtendedCreatureVampirism;
import de.teamlapen.vampirism.api.items.IItemWithTier;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.entity.ExtendedCreature;
import de.teamlapen.vampirism.items.HunterCoatItem;
import de.teamlapen.vampirism.player.hunter.HunterPlayer;
import de.teamlapen.vampirism.player.hunter.HunterPlayerSpecialAttribute;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.player.vampire.actions.VampireActions;
import de.teamlapen.vampirism.tileentity.TotemHelper;
import de.teamlapen.vampirism.util.ASMHooks;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FogRenderer;
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
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.*;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.resource.IResourceType;
import net.minecraftforge.resource.ISelectiveResourceReloadListener;
import net.minecraftforge.resource.VanillaResourceType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Predicate;

/**
 * Handle most general rendering related stuff
 */
@OnlyIn(Dist.CLIENT)
public class RenderHandler implements ISelectiveResourceReloadListener {
    private static final int ENTITY_NEAR_SQ_DISTANCE = 100;
    @Nonnull
    private final Minecraft mc;
    private final int BLOOD_VISION_FADE_TICKS = 80;

    private final int VAMPIRE_BIOME_FADE_TICKS = 60;
    private final Logger LOGGER = LogManager.getLogger();
    @Nullable
    private OutlineLayerBuffer bloodVisionBuffer;
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
    @Nullable
    private ShaderGroup blurShader;
    /**
     * Store the last used framebuffer size to be able to rebind shader buffer when size changes
     */
    private int displayHeight, displayWidth;
    private boolean reducedBloodVision = false;

    @Nullable
    private Shader blur1, blur2, blit0;
    private boolean isInsideBloodVisionRendering = false;

    public RenderHandler(@Nonnull Minecraft mc) {
        this.mc = mc;
    }

    @Nullable
    @Override
    public IResourceType getResourceType() {
        return VanillaResourceType.SHADERS;
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (mc.world == null || mc.player == null || !mc.player.isAlive()) return;
        if (event.phase == TickEvent.Phase.END) return;
        lastBloodVisionTicks = bloodVisionTicks;
        VampirePlayer vampire = VampirePlayer.get(mc.player);
        //Blood vision
        if (vampire.getSpecialAttributes().blood_vision && !VampirismConfig.CLIENT.disableBloodVisionRendering.get() && !VampirePlayer.get(mc.player).isGettingSundamage(mc.player.world)) {

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
        if (mc.player.ticksExisted % 10 == 0) {
            if ((VampirismConfig.CLIENT.renderVampireForestFog.get() || VampirismConfig.SERVER.enforceRenderForestFog.get()) && (Helper.isEntityInVampireBiome(mc.player) || TotemHelper.isInsideVampireAreaCached(mc.world.getDimensionKey(), mc.player.getPosition()))) {
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

        //Saturation shader BUGGED this renderes the hotbar white
//        if (mc.player != null && mc.player.getRNG().nextInt(10) == 3) {
//            EffectInstance pe = mc.player.getActivePotionEffect(ModEffects.saturation);
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
        RenderSystem.fogStart(event.getType() == FogRenderer.FogType.FOG_SKY ? 0 : fogStart); //maybe invert
        RenderSystem.fogEnd(fogEnd);
    }

    @SubscribeEvent
    public void onRenderHand(RenderHandEvent event) {
        if (mc.player != null && mc.player.isAlive() && VampirePlayer.get(mc.player).getActionHandler().isActionActive(VampireActions.bat)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onCameraSetup(EntityViewRenderEvent.CameraSetup event) {
        if (shouldRenderBloodVision()) {
            reducedBloodVision = OptifineHandler.isShaders();
            if (!reducedBloodVision) {
                if (displayHeight != mc.getMainWindow().getFramebufferHeight() || displayWidth != mc.getMainWindow().getFramebufferWidth()) {
                    this.displayHeight = mc.getMainWindow().getFramebufferHeight();
                    this.displayWidth = mc.getMainWindow().getFramebufferWidth();
                    this.updateFramebufferSize(this.displayWidth, this.displayHeight);
                }
                adjustBloodVisionShaders(getBloodVisionProgress((float) event.getRenderPartialTicks()));
            } else {
                ASMHooks.enforcingGlowing_bloodVision = true;
            }
        }
        if(VampirismConfig.SERVER.preventRenderingDebugBoundingBoxes.get()){
            Minecraft.getInstance().getRenderManager().setDebugBoundingBox(false);
        }
    }

    @SubscribeEvent
    public void onRenderLivingPost(RenderLivingEvent.Post event) {
        if (!isInsideBloodVisionRendering && shouldRenderBloodVision() && !reducedBloodVision) {
            Entity entity = event.getEntity();

            boolean flag = true;
            if (entity instanceof PlayerEntity && HunterCoatItem.isFullyEquipped((PlayerEntity) entity)!=null) flag = false;
            double dist = mc.player.getDistanceSq(entity);
            if (dist > VampirismConfig.BALANCE.vsBloodVisionDistanceSq.get()) {
                flag = false;
            }
            if (flag) {
                int color;
                LazyOptional<IExtendedCreatureVampirism> opt = entity instanceof CreatureEntity && entity.isAlive() ? ExtendedCreature.getSafe(entity) : LazyOptional.empty();
                if (opt.map(creature -> creature.getBlood() > 0 && !creature.hasPoisonousBlood()).orElse(false)) {
                    color = 0xFF0000;
                } else if (VampirePlayer.getOpt(mc.player).map(VampirePlayer::getSpecialAttributes).map(s -> s.blood_vision_garlic).orElse(false) && ((opt.map(IExtendedCreatureVampirism::hasPoisonousBlood).orElse(false)) || Helper.isHunter(entity))) {
                    color = 0x07FF07;
                } else {
                    color = 0xA0A0A0;
                }
                EntityRendererManager renderManager = mc.getRenderManager();
                if (bloodVisionBuffer == null) {
                    bloodVisionBuffer = new OutlineLayerBuffer(mc.getRenderTypeBuffers().getBufferSource());
                }
                int r = color >> 16 & 255;
                int g = color >> 8 & 255;
                int b = color & 255;
                int alpha = (int) ((dist > ENTITY_NEAR_SQ_DISTANCE ? 50 : (dist / (double) ENTITY_NEAR_SQ_DISTANCE * 50d)) * getBloodVisionProgress(event.getPartialRenderTick()));
                bloodVisionBuffer.setColor(r, g, b, alpha);
                float f = MathHelper.lerp(event.getPartialRenderTick(), entity.prevRotationYaw, entity.rotationYaw);
                isInsideBloodVisionRendering = true;
                EntityRenderer<? super Entity> entityrenderer = renderManager.getRenderer(entity);
                entityrenderer.render(entity, f, event.getPartialRenderTick(), event.getMatrixStack(), bloodVisionBuffer, renderManager.getPackedLight(entity, event.getPartialRenderTick()));
                mc.getFramebuffer().bindFramebuffer(false);
                isInsideBloodVisionRendering = false;

            }
        }
    }

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        ASMHooks.enforcingGlowing_bloodVision = false;
        if (mc.world == null) return;

        /*
         * DO NOT USE partial ticks from event. They are bugged: https://github.com/MinecraftForge/MinecraftForge/issues/6380
         */
        float partialTicks = mc.getRenderPartialTicks();


        if (shouldRenderBloodVision() && !reducedBloodVision) {
            this.blurShader.render(partialTicks);
            if (this.bloodVisionBuffer != null) this.bloodVisionBuffer.finish();
        }
    }

    @SubscribeEvent
    public void onRenderLivingPre(RenderLivingEvent.Pre<PlayerEntity, PlayerModel<PlayerEntity>> event) {
        LivingEntity entity = event.getEntity();
        if (entity instanceof PlayerEntity && HunterPlayer.getOpt((PlayerEntity) entity).map(HunterPlayer::getSpecialAttributes).map(HunterPlayerSpecialAttribute::isDisguised).orElse(false)) {
            double dist = this.mc.player == null ? 0 : entity.getDistanceSq(this.mc.player);
            if (dist > 64) {
                event.setCanceled(true);
            }
            else if(dist>16){
                IItemWithTier.TIER hunterCoatTier = HunterCoatItem.isFullyEquipped((PlayerEntity) entity);
                if(hunterCoatTier== IItemWithTier.TIER.ENHANCED||hunterCoatTier == IItemWithTier.TIER.ULTIMATE){
                    event.setCanceled(true);
                }
            }
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

        }
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        this.bloodVisionTicks = 0;//Reset blood vision on world load
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager, Predicate<IResourceType> resourcePredicate) {
        this.reMakeBloodVisionShader();

    }

    private void adjustBloodVisionShaders(float progress) {
        if (blit0 == null || blur1 == null || blur2 == null) return;
        progress = MathHelper.clamp(progress, 0, 1);
        blit0.getShaderManager().getShaderUniform("ColorModulate").set((1 - 0.4F * progress), (1 - 0.5F * progress), (1 - 0.3F * progress), 1);
        blur1.getShaderManager().getShaderUniform("Radius").set(Math.round(15 * progress) / 1F);
        blur2.getShaderManager().getShaderUniform("Radius").set(Math.round(15 * progress) / 1F);

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

    public boolean shouldRenderBloodVision() {
        return this.bloodVisionTicks > 0 && this.blurShader != null && this.mc.player != null;
    }

    private void updateFramebufferSize(int width, int height) {
        if (this.blurShader != null) {
            this.blurShader.createBindFramebuffers(width, height);
        }
    }

}
