package de.teamlapen.vampirism.client.render;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.config.Configs;
import de.teamlapen.vampirism.core.ModPotions;
import de.teamlapen.vampirism.entity.ExtendedCreature;
import de.teamlapen.vampirism.entity.converted.EntityConvertedCreature;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.player.vampire.actions.VampireActions;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.REFERENCE;
import de.teamlapen.vampirism.util.SRGNAMES;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.Sphere;

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
    private final int ENTITY_RADIUS;
    private final int ENTITY_MIN_SQ_RADIUS = 25;
    private final int VAMPIRE_BIOME_FADE_TICKS = 160;
    private final int COMPILE_ENTITY_COOLDOWN;
    private final int ENTITY_NEAR_SQ_DISTANCE = 100;
    private int entityCooldownTicks;
    private int entityDisplayListId = 0;
    private int entitySphereListId = 0;
    private EntityBat entityBat;
    private int vampireBiomeTicks = 0;
    private int bloodVisionTicks = 0;
    private float vampireBiomeFogDistanceMultiplier = 1;

    private boolean doShaders = true;

    public RenderHandler(Minecraft mc) {
        this.mc = mc;
        COMPILE_ENTITY_COOLDOWN = Configs.blood_vision_recompile_ticks;
        ENTITY_RADIUS = Balance.vps.BLOOD_VISION_DISTANCE;
        entityDisplayListId = GlStateManager.glGenLists(2);
        entitySphereListId = entityDisplayListId + 1;
        this.buildEntitySphere();
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (mc.theWorld == null) return;
        if (event.phase == TickEvent.Phase.END) return;
        VampirePlayer vampire = VampirePlayer.get(mc.thePlayer);
        if (vampire.getSpecialAttributes().blood_vision && !VampirePlayer.get(mc.thePlayer).isGettingSundamage()) {

            if (bloodVisionTicks < BLOOD_VISION_FADE_TICKS) {
                bloodVisionTicks++;

            }
            entityCooldownTicks--;

            if (entityCooldownTicks < 1) {
                this.compileEntitys();
                entityCooldownTicks = COMPILE_ENTITY_COOLDOWN;
            }
        } else {
            if (bloodVisionTicks > 0) {
                bloodVisionTicks -= 2;
            }
            if (vampireBiomeTicks > 10 && bloodVisionTicks == 15) {
                bloodVisionTicks = 0;
            }
        }

        if (Configs.renderVampireForestFog && VReference.castleDimId != mc.theWorld.provider.getDimension() && Helper.isEntityInVampireBiome(mc.thePlayer)) {
            if (vampireBiomeTicks < VAMPIRE_BIOME_FADE_TICKS) {
                vampireBiomeTicks++;
            }
            vampireBiomeFogDistanceMultiplier = vampire.getSpecialAttributes().increasedVampireFogDistance ? 2 : 1;
        } else {
            if (vampireBiomeTicks > 0) {
                vampireBiomeTicks--;
            }
        }

        if (OpenGlHelper.areShadersSupported() && doShaders) {
            if (mc.thePlayer != null && mc.thePlayer.getRNG().nextInt(10) == 3) {
                PotionEffect pe = mc.thePlayer.getActivePotionEffect(ModPotions.saturation);
                boolean active = pe != null && pe.getAmplifier() >= 2;
                EntityRenderer renderer = mc.entityRenderer;
                if (active && !renderer.isShaderActive()) {
                    //Load saturation shader if not active
                    try {
                        Method method = ReflectionHelper.findMethod(EntityRenderer.class, renderer, new String[]{"loadShader", SRGNAMES.EntityRenderer_loadShader}, ResourceLocation.class);
                        method.invoke(renderer, saturation1);
                    } catch (Exception e) {
                        doShaders = false;
                        VampirismMod.log.e("RenderHandler", e, "Failed to activate saturation shader");
                    }
                } else if (!active && renderer.isShaderActive() && renderer.getShaderGroup().getShaderGroupName().equals(saturation1.toString())) {
                    renderer.stopUseShader();
                }
            }
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
        if (VampirePlayer.get(mc.thePlayer).getActionHandler().isActionActive(VampireActions.batAction)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onRenderPlayer(RenderPlayerEvent.Pre event) {
        if (VampirePlayer.get(event.getEntityPlayer()).getActionHandler().isActionActive(VampireActions.batAction)) {
            event.setCanceled(true);
            if (entityBat == null) {
                entityBat = new EntityBat(event.getEntity().worldObj);
                entityBat.setIsBatHanging(false);
            }
            EntityPlayer player = event.getEntityPlayer();

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

        }
    }

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        if (mc.theWorld == null) return;
        if (bloodVisionTicks > BLOOD_VISION_FADE_TICKS / 2) {
            double doubleX = this.mc.thePlayer.lastTickPosX + (this.mc.thePlayer.posX - this.mc.thePlayer.lastTickPosX) * event.getPartialTicks();

            double doubleY = this.mc.thePlayer.lastTickPosY + (this.mc.thePlayer.posY - this.mc.thePlayer.lastTickPosY) * event.getPartialTicks();

            double doubleZ = this.mc.thePlayer.lastTickPosZ + (this.mc.thePlayer.posZ - this.mc.thePlayer.lastTickPosZ) * event.getPartialTicks();

            GlStateManager.pushMatrix();
            GlStateManager.translate(-doubleX, -doubleY, -doubleZ);
            GlStateManager.callList(entityDisplayListId);
            GlStateManager.popMatrix();

        }
        if (bloodVisionTicks > 0) {
            renderBloodVisionFog(bloodVisionTicks);
        }
        if (vampireBiomeTicks > 0) {
            renderVampireBiomeFog(vampireBiomeTicks);
        }

    }

    /**
     * Builds and saves a sphere for entitys
     */
    private void buildEntitySphere() {
        Sphere sphere = new Sphere();
        sphere.setDrawStyle(GLU.GLU_FILL);
        // GLU_SMOOTH will try to smoothly apply lighting
        sphere.setNormals(GLU.GLU_SMOOTH);
        sphere.setOrientation(GLU.GLU_OUTSIDE);

        // Create a new list to hold our sphere data.
        GlStateManager.glNewList(entitySphereListId, GL11.GL_COMPILE);
        // binds the texture
        // ResourceLocation rL = new ResourceLocation(MagicBeans.MODID+":textures/entities/sphere.png");
        // Minecraft.getMinecraft().getTextureManager().bindTexture(rL);

        sphere.draw(0.5F, 32, 32);
        GL11.glEndList();

    }

//    @SubscribeEvent
//    public void onEntityJoinWorld(EntityJoinWorldEvent event){
//        if(event.entity instanceof EntityPlayer){
//            VampirismMod.log.t("Player"); //TODO check
//            if(event.entity.equals(mc.thePlayer)){
//                VampirismMod.log.t("this");
//                if(Helper.isEntityInVampireBiome(mc.thePlayer)){
//                    VampirismMod.log.t("that");
//                    vampireBiomeTicks=VAMPIRE_BIOME_FADE_TICKS;
//                }
//                else{
//                    VampirismMod.log.t(""+event.entity.worldObj.provider.getWorldChunkManager().getBiomeGenAt(null,event.entity.getPosition().getX(),event.entity.getPosition().getZ(),1,1,false)[0]);
//                    vampireBiomeTicks=0;
//                }
//            }
//        }
//    }

    /**
     * Compiles a render list of the entitys nearby
     */
    private void compileEntitys() {
        GlStateManager.glNewList(entityDisplayListId, GL11.GL_COMPILE);
        GlStateManager.disableTexture2D();
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GlStateManager.enableBlend();

        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);

        WorldClient world = this.mc.theWorld;

        EntityPlayerSP player = this.mc.thePlayer;
        if ((world == null) || (player == null))
            return;

        List list = world.getEntitiesWithinAABBExcludingEntity(player, player.getEntityBoundingBox().expand(ENTITY_RADIUS, ENTITY_RADIUS, ENTITY_RADIUS));
        for (Object o : list) {
            if (o instanceof EntityCreature || o instanceof EntityPlayer) {
                EntityLivingBase e = (EntityLivingBase) o;
                int distance = (int) e.getDistanceSqToEntity(player);
                if (distance <= ENTITY_NEAR_SQ_DISTANCE && (distance >= ENTITY_MIN_SQ_RADIUS)) {
                    // ||!player.canEntityBeSeen(e)
                    renderEntitySphere(e, (((float) distance - ENTITY_MIN_SQ_RADIUS) / (ENTITY_NEAR_SQ_DISTANCE - ENTITY_MIN_SQ_RADIUS)));
                } else if (distance > ENTITY_NEAR_SQ_DISTANCE) {
                    renderEntitySphere(e, 1F);
                }

            }
        }
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GlStateManager.glEndList();
    }

    private void renderBloodVisionFog(int ticks) {
        float f = ((float) BLOOD_VISION_FADE_TICKS) / (float) ticks;

        GlStateManager.pushMatrix();
        boolean fog = GL11.glIsEnabled(GL11.GL_FOG);
        if (!fog)
            GlStateManager.enableFog();
        GlStateManager.setFog(GlStateManager.FogMode.LINEAR);
        GlStateManager.setFogStart(4.0F * f);
        GlStateManager.setFogEnd(5.5F * f);
        GlStateManager.glNormal3f(0F, -1F, 0F);
        GlStateManager.color(1F, 1F, 1F, 1F);

        GlStateManager.setFogDensity(1);
        if (!fog)
            GlStateManager.disableFog();
        GlStateManager.popMatrix();
    }

    /**
     * Renders the sphere around the give entity
     *
     * @param entity
     * @param f
     */
    private void renderEntitySphere(EntityLivingBase entity, float f) {
        float red = 1.0F;
        float green = 0.0F;
        float blue = 0.0F;

        if (entity instanceof EntityCreature) {
            if (entity instanceof EntityConvertedCreature) {
                red = 0.23127F;
                green = 0.04313F;
                blue = 0.04313F;

            } else if (!(ExtendedCreature.get((EntityCreature) entity).getBlood() > 0)) {
                red = 0.039215F;
                green = 0.0745F;
                blue = 0.1647F;
            }
        } else if (entity instanceof EntityPlayer) {
            if (VampirePlayer.get((EntityPlayer) entity).getLevel() > 0) {
                red = 0.039215F;
                green = 0.0745F;
                blue = 0.1647F;
            }
        }
        GlStateManager.pushMatrix();
        GlStateManager.translate(entity.posX, entity.posY + entity.height / 2, entity.posZ);

        GlStateManager.scale(entity.width * 1.5F, entity.height * 1.5F, entity.width * 1.5F);
        GlStateManager.color(red, green, blue, 0.5F * f);
        GlStateManager.callList(entitySphereListId);
        GlStateManager.popMatrix();

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

}
