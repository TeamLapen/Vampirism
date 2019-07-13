package de.teamlapen.vampirism.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;

import de.teamlapen.lib.lib.client.gui.ExtendedGui;
import de.teamlapen.lib.lib.util.FluidLib;
import de.teamlapen.vampirism.api.entity.IBiteableEntity;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.vampire.IBloodStats;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.config.Configs;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModFluids;
import de.teamlapen.vampirism.core.ModPotions;
import de.teamlapen.vampirism.entity.ExtendedCreature;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.hunter.HunterBaseEntity;
import de.teamlapen.vampirism.player.hunter.HunterPlayer;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.player.vampire.actions.VampireActions;
import de.teamlapen.vampirism.util.HalloweenSpecial;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeIngameGui;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.opengl.GL11;

/**
 * Handles general Overlay thingies TODO change batmode color
 */
@OnlyIn(Dist.CLIENT)
public class VampirismHUDOverlay extends ExtendedGui {

    private final Minecraft mc;
    private final ResourceLocation icons = new ResourceLocation(REFERENCE.MODID + ":textures/gui/icons.png");

    private int screenColor = 0;
    private int screenPercentage = 0;
    private boolean fullScreen = false;
    private int renderFullTick = 0;
    private int rederFullOn, renderFullOff, renderFullColor;
    private int screenBottomColor = 0;
    private int screenBottomPercentage = 0;

    public VampirismHUDOverlay(Minecraft mc) {
        this.mc = mc;
    }

    /**
     * Tint the entire screen in a certain color. Blends in and out
     *
     * @param on    Blend in duration
     * @param off   Blend out duration
     * @param color Color
     */
    public void makeRenderFullColor(int on, int off, int color) {
        this.rederFullOn = on;
        this.renderFullOff = off;
        this.renderFullTick = on + off;
        if ((color >> 24 & 255) == 0) {
            color |= 0xFF000000;
        }
        this.renderFullColor = color;
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {

        if (mc.player == null)
            return;
        if (event.phase == TickEvent.Phase.END)
            return;
        IFactionPlayer player = FactionPlayerHandler.get(mc.player).getCurrentFactionPlayer();
        if (player instanceof VampirePlayer) {
            handleScreenColorVampire((VampirePlayer) player);
        } else if (player instanceof HunterPlayer) {
            handleScreenColorHunter((HunterPlayer) player);
        } else {
            screenPercentage = 0;
            screenBottomPercentage =0;
        }

        //If we are supposed to render fullscreen, we overwrite the other values and only render fullscreen
        if (renderFullTick > 0) {
            screenColor = renderFullColor;
            fullScreen = true;
            if (renderFullTick > renderFullOff) {
                screenPercentage = (int) (100 * (1 - (renderFullTick - renderFullOff) / (float) rederFullOn));
            } else {
                screenPercentage = (int) (100 * renderFullTick / (float) renderFullOff);
            }
            renderFullTick--;
        }

    }

    @SubscribeEvent
    public void onRenderCrosshair(RenderGameOverlayEvent.Pre event) {

        if (event.getType() != RenderGameOverlayEvent.ElementType.CROSSHAIRS) {
            return;
        }

        RayTraceResult p = Minecraft.getInstance().objectMouseOver;

        if (p != null && p.getType() == RayTraceResult.Type.ENTITY) {
            IVampirePlayer player = VampirePlayer.get(mc.player);
            if (player.getLevel() > 0 && !mc.player.isSpectator() && !player.getActionHandler().isActionActive(VampireActions.bat)) {
                Entity entity = ((EntityRayTraceResult) p).getEntity();
                IBiteableEntity biteable = null;
                if (entity instanceof IBiteableEntity) {
                    biteable = (IBiteableEntity) entity;
                } else if (entity instanceof CreatureEntity) {
                    biteable = ExtendedCreature.get((CreatureEntity) entity);
                } else if (entity instanceof PlayerEntity) {
                    biteable = VampirePlayer.get((PlayerEntity) entity);
                }
                if (biteable != null && biteable.canBeBitten(player)) {
                    int color = 0xFF0000;
                    if (entity instanceof HunterBaseEntity || (entity instanceof CreatureEntity && ExtendedCreature.get((CreatureEntity) entity).hasPoisonousBlood()))
                        color = 0x099022;
                    renderBloodFangs(this.mc.mainWindow.getScaledWidth(), this.mc.mainWindow.getScaledHeight(), MathHelper.clamp(biteable.getBloodLevelRelative(), 0.2F, 1F), color);
                    event.setCanceled(true);
                }
            }
        } else if (p != null && p.getType() == RayTraceResult.Type.BLOCK) {
            BlockState block = Minecraft.getInstance().world.getBlockState(((BlockRayTraceResult) p).getPos());
            if (ModBlocks.blood_container.equals(block.getBlock())) {
                IVampirePlayer player = VampirePlayer.get(mc.player);
                if (player.wantsBlood()) {
                    TileEntity tile = Minecraft.getInstance().world.getTileEntity(((BlockRayTraceResult) p).getPos());
                    if (tile != null) {
                        tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).ifPresent(handler -> {
                            if (FluidLib.getFluidAmount(handler, ModFluids.blood) > 0) {
                                renderBloodFangs(this.mc.mainWindow.getScaledWidth(), this.mc.mainWindow.getScaledHeight(), 1, 0xFF0000);
                                event.setCanceled(true);
                            }
                        });
                    }
                }
            }
        }
    }

    /**
     * Render Level
     */
    @SubscribeEvent
    public void onRenderExperienceBar(RenderGameOverlayEvent.Post event) {

        if (event.getType() != RenderGameOverlayEvent.ElementType.EXPERIENCE) {
            return;
        }
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(GL11.GL_LIGHTING);
        IPlayableFaction faction = FactionPlayerHandler.get(mc.player).getCurrentFaction();
        if (mc.playerController.gameIsSurvivalOrAdventure() && faction != null && faction.renderLevel()) {
            // boolean flag1 = false;
            int color = faction.getColor();
            String text = "" + FactionPlayerHandler.get(mc.player).getCurrentLevel();
            int x = (this.mc.mainWindow.getScaledWidth() - mc.fontRenderer.getStringWidth(text)) / 2 + Configs.gui_level_offset_x;
            int y = this.mc.mainWindow.getScaledHeight() - Configs.gui_level_offset_y;
            mc.fontRenderer.drawString(text, x + 1, y, 0);
            mc.fontRenderer.drawString(text, x - 1, y, 0);
            mc.fontRenderer.drawString(text, x, y + 1, 0);
            mc.fontRenderer.drawString(text, x, y - 1, 0);
            mc.fontRenderer.drawString(text, x, y, color);
        }
    }

    @SubscribeEvent
    public void onRenderFoodBar(RenderGameOverlayEvent.Pre event) {

        if (event.getType() != RenderGameOverlayEvent.ElementType.FOOD) {
            return;
        }

        if (Helper.isVampire(mc.player)) {
            event.setCanceled(true);

            if (mc.playerController.gameIsSurvivalOrAdventure()) {
                IBloodStats stats = VampirePlayer.get(mc.player).getBloodStats();

                GlStateManager.enableBlend();

                this.mc.getTextureManager().bindTexture(icons);
                int left = this.mc.mainWindow.getScaledWidth() / 2 + 91;
                int top = this.mc.mainWindow.getScaledHeight() - ForgeIngameGui.right_height;
                ForgeIngameGui.right_height += 10;
                int blood = stats.getBloodLevel();
                int maxBlood = stats.getMaxBlood();
                int blood2 = blood - 20;
                int maxBlood2 = maxBlood - 20;
                for (int i = 0; i < 10; ++i) {
                    int idx = i * 2 + 1;
                    int x = left - i * 8 - 9;

                    // Draw Background
                    blit(x, top, 0, idx <= maxBlood2 ? 9 : 0, 9, 9);

                    if (idx < blood) {
                        blit(x, top, 9, idx < blood2 ? 9 : 0, 9, 9);
                        if (idx == blood2) {
                            blit(x, top, 18, 9, 9, 9);
                        }
                    } else if (idx == blood) {
                        blit(x, top, 18, 0, 9, 9);
                    }
                }
                this.mc.getTextureManager().bindTexture(AbstractGui.GUI_ICONS_LOCATION);
                GlStateManager.disableBlend();
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onRenderWorldLast(RenderWorldLastEvent event) {

        if ((screenPercentage > 0 || screenBottomPercentage > 0) && !Configs.disable_screen_overlay) {
            // Set the working matrix/layer to a layer directly on the screen/in front of
            // the player
            // int factor=scaledresolution.getScaleFactor();
            GlStateManager.clear(GL11.GL_DEPTH_BUFFER_BIT, Minecraft.IS_RUNNING_ON_MAC);
            GlStateManager.matrixMode(GL11.GL_PROJECTION);
            GlStateManager.loadIdentity();
            GlStateManager.ortho(0.0D, this.mc.mainWindow.getScaledWidth(), this.mc.mainWindow.getScaledHeight(), 0.0D, 1D, -1D);
            GlStateManager.matrixMode(GL11.GL_MODELVIEW);
            GlStateManager.loadIdentity();
            GlStateManager.pushMatrix();
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            int w = (this.mc.mainWindow.getScaledWidth());
            int h = (this.mc.mainWindow.getScaledHeight());
            if (fullScreen) {
                // Render a see through colored square over the whole screen
                float r = (float) (screenColor >> 16 & 255) / 255.0F;
                float g = (float) (screenColor >> 8 & 255) / 255.0F;
                float b = (float) (screenColor & 255) / 255.0F;
                float a = (screenPercentage / 100f) * (screenColor >> 24 & 255) / 255F;

                GlStateManager.disableTexture();
                GlStateManager.enableBlend();
                GlStateManager.disableAlphaTest();
                GlStateManager.blendFuncSeparate(770, 771, 1, 0);
                GlStateManager.shadeModel(7425);
                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder worldrenderer = tessellator.getBuffer();
                worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
                worldrenderer.pos(0, h, (double) this.blitOffset).color(r, g, b, a).endVertex();
                worldrenderer.pos(w, h, (double) this.blitOffset).color(r, g, b, a).endVertex();
                worldrenderer.pos(w, 0, (double) this.blitOffset).color(r, g, b, a).endVertex();
                worldrenderer.pos(0, 0, (double) this.blitOffset).color(r, g, b, a).endVertex();

                tessellator.draw();
                GlStateManager.shadeModel(7424);
                GlStateManager.disableBlend();
                GlStateManager.enableAlphaTest();
                GlStateManager.enableTexture();

                /*
                 * Try later this.drawGradientRect(0, 0, w,Math.round(h/(2/renderRed)),
                 * 0xfff00000, 0x000000); this.drawGradientRect(0,
                 * h-Math.round(h/(2/renderRed)), w, h, 0x00000000, 0xfff00000);
                 * this.fillGradient2(0, 0, w/6, h, 0x000000, 0xfff00000);
                 * this.fillGradient2(w-w/6, 0, w, h, 0xfff00000, 0x000000);
                 */

            } else {

                if (screenPercentage > 0) {
                    // sun border
                    int bw = 0;
                    int bh = 0;

                    bh = Math.round(h / (float) 4 * screenPercentage / 100);
                    bw = Math.round(w / (float) 8 * screenPercentage / 100);

                    this.fillGradient(0, 0, w, bh, screenColor, 0x000);
                    this.fillGradient(0, h - bh, w, h, 0x00000000, screenColor);
                    this.fillGradient2(0, 0, bw, h, 0x000000, screenColor);
                    this.fillGradient2(w - bw, 0, w, h, screenColor, 0x00);
                } else { //If here screenBottomPercentage has to be >0

                    // batmode border
                    int hh = 0;

                    hh = Math.round(h / (float) 4 * screenBottomPercentage / 100);

                    this.fillGradient(0, h - hh, w, h, 0x00000000, screenBottomColor);
                }

            }
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GlStateManager.popMatrix();
        }

        if (HalloweenSpecial.shouldRenderOverlay()) {
            renderHalloweenOverlay();
        }
    }

    private void handleScreenColorHunter(HunterPlayer hunter) {
        if (hunter.getSpecialAttributes().isDisguised()) {
            screenPercentage = (int) (100 * hunter.getSpecialAttributes().getDisguiseProgress());
            screenColor = 0xff111111;
            fullScreen = false;
        } else if (hunter.getSpecialAttributes().isVampireNearby()) {
            screenPercentage = (int) (100 * hunter.getSpecialAttributes().getVampireNearbyProgress());
            screenColor = 0x80ba1836;
            fullScreen = false;
        } else {
            screenPercentage = (int) Math.max(0F, (screenPercentage / 20F) * 19F);
        }
    }

    private void handleScreenColorVampire(VampirePlayer vampire) {

        //Main area/borders
        if (vampire.getActionHandler().isActionActive(VampireActions.vampire_rage)) {
            screenPercentage = 100;
            screenColor = 0xfff00000;
            fullScreen = false;
        } else if ((screenPercentage = vampire.getTicksInSun() / 2) > 0) {
            EffectInstance effect = mc.player.getActivePotionEffect(ModPotions.sunscreen);
            if (effect == null || effect.getAmplifier() < 5) {
                screenColor = 0xfffff755;
                fullScreen = false;
                if (vampire.getRepresentingPlayer().abilities.isCreativeMode) {
                    screenPercentage = Math.min(20, screenPercentage);
                }
            } else {
                screenPercentage = 0;
            }
        } else {
            screenPercentage = 0;
        }

        //Bottom Area
        if (vampire.getActionHandler().isActionActive(VampireActions.bat)) {
            float batPercentage = vampire.getActionHandler().getPercentageForAction(VampireActions.bat);
            if (batPercentage < 0.2F && batPercentage > 0.0F) {
                screenBottomColor = 0xcc7067f9; // change color
                screenBottomPercentage = (int) ((0.2F - batPercentage) * 1000);
                fullScreen = false;
            } else {
                screenBottomPercentage = 0;
            }
        } else {
            screenBottomPercentage = 0;
        }
    }

    private void renderBloodFangs(int width, int height, float perc, int color) {

        float r = ((color & 0xFF0000) >> 16) / 256f;
        float g = ((color & 0xFF00) >> 8) / 256f;
        float b = (color & 0xFF) / 256f;
        this.mc.getTextureManager().bindTexture(icons);
        int left = width / 2 - 8;
        int top = height / 2 - 4;
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glColor4f(1f, 1f, 1f, 0.7F);
        blit(left, top, 27, 0, 16, 10);
        GL11.glColor4f(r, g, b, 0.8F);
        int percHeight = (int) (10 * perc);
        blit(left, top + (10 - percHeight), 27, 10 - percHeight, 16, percHeight);
        GL11.glColor4f(1F, 1F, 1F, 1F);
        GL11.glDisable(GL11.GL_BLEND);

    }

    /**
     * Halloween (temporary) overlay
     */
    private void renderHalloweenOverlay() {

        GlStateManager.pushMatrix();
        // Set the working matrix/layer to a layer directly on the screen/in front of
        // the player
        // int factor=scaledresolution.getScaleFactor();
        int w = (this.mc.mainWindow.getScaledWidth());
        int h = (this.mc.mainWindow.getScaledHeight());

        this.mc.mainWindow.loadGUIRenderMatrix(Minecraft.IS_RUNNING_ON_MAC);

        this.mc.textureManager.bindTexture(new ResourceLocation(REFERENCE.MODID, "textures/gui/special_halloween.png"));

        int width = 507;
        int height = 102;

        int x = (w - width) / 2;
        int y = (h - height) / 2;

        GlStateManager.color4f(1, 1, 1, 1);
        GlStateManager.enableBlend();
        blit(x, y, 0, 0, width, height, width, height);//TODO 1.14 test
        GlStateManager.disableBlend();

        GlStateManager.popMatrix();
    }

}
