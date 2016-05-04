package de.teamlapen.vampirism.client.gui;

import de.teamlapen.lib.lib.client.gui.ExtendedGui;
import de.teamlapen.vampirism.api.entity.IBiteableEntity;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.config.Configs;
import de.teamlapen.vampirism.entity.ExtendedCreature;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.player.vampire.BloodStats;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.entity.player.vampire.actions.VampireActions;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.opengl.GL11;

/**
 * Handles general Overlay thingies
 */
public class VampirismHUDOverlay extends ExtendedGui {

    private final Minecraft mc;
    private final ResourceLocation icons = new ResourceLocation(REFERENCE.MODID + ":textures/gui/icons.png");

    private int screenColor = 0;
    private int screenPercentage = 0;
    private boolean fullScreen = false;
    private int renderFullTick = 0;
    private int rederFullOn, renderFullOff, renderFullColor;

    public VampirismHUDOverlay(Minecraft mc) {
        this.mc = mc;
    }

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
        if (mc.thePlayer == null) return;
        if (event.phase == TickEvent.Phase.END) return;
        IFactionPlayer player = FactionPlayerHandler.get(mc.thePlayer).getCurrentFactionPlayer();
        if (player != null && player instanceof IVampirePlayer) {
            if (((IVampirePlayer) player).getActionHandler().isActionActive(VampireActions.rageAction)) {
                screenPercentage = 100;
                screenColor = 0xfff00000;
                fullScreen = false;
            } else if ((screenPercentage = ((IVampirePlayer) player).getTicksInSun()) > 0) {
                screenColor = 0xffffe700;
                fullScreen = false;
            }
        } else {
            screenPercentage = 0;
        }
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

        RayTraceResult p = Minecraft.getMinecraft().objectMouseOver;

        if (p != null && p.typeOfHit == RayTraceResult.Type.ENTITY && p.entityHit != null) {
            IVampirePlayer player = VampirePlayer.get(mc.thePlayer);
            if (player.getLevel() > 0) {
                Entity entity = p.entityHit;
                IBiteableEntity biteable = null;
                if (entity instanceof EntityCreature) {
                    biteable = ExtendedCreature.get((EntityCreature) entity);
                } else if (entity instanceof IBiteableEntity) {
                    biteable = (IBiteableEntity) entity;
                } else if (entity instanceof EntityPlayer) {
                    biteable = VampirePlayer.get((EntityPlayer) entity);
                }
                if (biteable != null && biteable.canBeBitten(player)) {
                    this.mc.getTextureManager().bindTexture(icons);
                    int left = event.getResolution().getScaledWidth() / 2 - 8;
                    int top = event.getResolution().getScaledHeight() / 2 - 4;
                    GL11.glEnable(GL11.GL_BLEND);
                    GL11.glColor4f(1F, 0F, 0F, 0.8F);
                    drawTexturedModalRect(left, top, 27, 0, 16, 16);
                    GL11.glColor4f(1F, 1F, 1F, 1F);
                    GL11.glDisable(GL11.GL_BLEND);
                    event.setCanceled(true);
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
        IPlayableFaction faction = FactionPlayerHandler.get(mc.thePlayer).getCurrentFaction();
        if (mc.playerController.gameIsSurvivalOrAdventure() && faction != null && faction.renderLevel()) {
            // boolean flag1 = false;
            int color = faction.getColor();
            String text = "" + FactionPlayerHandler.get(mc.thePlayer).getCurrentLevel();
            int x = (event.getResolution().getScaledWidth() - mc.fontRendererObj.getStringWidth(text)) / 2 + Configs.gui_level_offset_x;
            int y = event.getResolution().getScaledHeight() - Configs.gui_level_offset_y;
            mc.fontRendererObj.drawString(text, x + 1, y, 0);
            mc.fontRendererObj.drawString(text, x - 1, y, 0);
            mc.fontRendererObj.drawString(text, x, y + 1, 0);
            mc.fontRendererObj.drawString(text, x, y - 1, 0);
            mc.fontRendererObj.drawString(text, x, y, color);
        }
    }

    @SubscribeEvent
    public void onRenderFoodBar(RenderGameOverlayEvent.Pre event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.FOOD) {
            return;
        }

        if (Helper.isVampire(mc.thePlayer)) {
            event.setCanceled(true);

            if (mc.playerController.gameIsSurvivalOrAdventure()) {
                BloodStats stats = VampirePlayer.get(mc.thePlayer).getBloodStats();

                GlStateManager.enableBlend();

                this.mc.getTextureManager().bindTexture(icons);
                int left = event.getResolution().getScaledWidth() / 2 + 91;
                int top = event.getResolution().getScaledHeight() - GuiIngameForge.right_height;
                GuiIngameForge.right_height += 10;

                for (int i = 0; i < 10; ++i) {
                    int idx = i * 2 + 1;
                    int x = left - i * 8 - 9;

                    // Draw Background
                    drawTexturedModalRect(x, top, 0, 0, 9, 9);

                    if (idx < stats.getBloodLevel()) {
                        drawTexturedModalRect(x, top, 9, 0, 9, 9);
                    } else if (idx == stats.getBloodLevel()) {
                        drawTexturedModalRect(x, top, 18, 0, 9, 9);
                    }
                }
                this.mc.getTextureManager().bindTexture(Gui.ICONS);
                GlStateManager.disableBlend();
            }
        }
    }

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        if (screenPercentage > 0) {
            //Set the working matrix/layer to a layer directly on the screen/in front of the player
            ScaledResolution scaledresolution = new ScaledResolution(this.mc);
            // int factor=scaledresolution.getScaleFactor();
            GlStateManager.clear(GL11.GL_DEPTH_BUFFER_BIT);
            GlStateManager.matrixMode(GL11.GL_PROJECTION);
            GlStateManager.loadIdentity();
            GlStateManager.ortho(0.0D, scaledresolution.getScaledWidth_double(), scaledresolution.getScaledHeight_double(), 0.0D, 1D, -1D);
            GlStateManager.matrixMode(GL11.GL_MODELVIEW);
            GlStateManager.loadIdentity();
            GlStateManager.pushMatrix();
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            int w = (scaledresolution.getScaledWidth());
            int h = (scaledresolution.getScaledHeight());
            if (fullScreen) {


                // Render a see through colored square over the whole screen
                float r = (float) (screenColor >> 16 & 255) / 255.0F;
                float g = (float) (screenColor >> 8 & 255) / 255.0F;
                float b = (float) (screenColor & 255) / 255.0F;
                float a = (screenPercentage / 100f) * (screenColor >> 24 & 255) / 255F;

                GlStateManager.disableTexture2D();
                GlStateManager.enableBlend();
                GlStateManager.disableAlpha();
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                GlStateManager.shadeModel(7425);
                Tessellator tessellator = Tessellator.getInstance();
                VertexBuffer worldrenderer = tessellator.getBuffer();
                worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
                worldrenderer.pos(0, h, (double) this.zLevel).color(r, g, b, a).endVertex();
                worldrenderer.pos(w, h, (double) this.zLevel).color(r, g, b, a).endVertex();
                worldrenderer.pos(w, 0, (double) this.zLevel).color(r, g, b, a).endVertex();
                worldrenderer.pos(0, 0, (double) this.zLevel).color(r, g, b, a).endVertex();
                tessellator.draw();
                GlStateManager.shadeModel(7424);
                GlStateManager.disableBlend();
                GlStateManager.enableAlpha();
                GlStateManager.enableTexture2D();

				/*
                 * Try later this.drawGradientRect(0, 0, w,Math.round(h/(2/renderRed)), 0xfff00000, 0x000000); this.drawGradientRect(0, h-Math.round(h/(2/renderRed)), w, h, 0x00000000, 0xfff00000);
				 * this.drawGradientRect2(0, 0, w/6, h, 0x000000, 0xfff00000); this.drawGradientRect2(w-w/6, 0, w, h, 0xfff00000, 0x000000);
				 */
            } else {
                int bw = 0;
                int bh = 0;

                bh = Math.round(h / (float) 4 * screenPercentage / 100);
                bw = Math.round(w / (float) 8 * screenPercentage / 100);

                this.drawGradientRect(0, 0, w, bh, screenColor, 0x000);
                this.drawGradientRect(0, h - bh, w, h, 0x00000000, screenColor);
                this.drawGradientRect2(0, 0, bw, h, 0x000000, screenColor);
                this.drawGradientRect2(w - bw, 0, w, h, screenColor, 0x00);

            }
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GlStateManager.popMatrix();
        }
    }


}
