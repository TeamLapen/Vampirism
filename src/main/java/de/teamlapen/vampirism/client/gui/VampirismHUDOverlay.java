package de.teamlapen.vampirism.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import de.teamlapen.lib.lib.client.gui.ExtendedGui;
import de.teamlapen.lib.lib.util.FluidLib;
import de.teamlapen.lib.util.OptifineHandler;
import de.teamlapen.vampirism.api.entity.IBiteableEntity;
import de.teamlapen.vampirism.api.entity.IExtendedCreatureVampirism;
import de.teamlapen.vampirism.api.entity.factions.IFactionPlayerHandler;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.vampire.IBloodStats;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.api.entity.vampire.IVampireMob;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.core.ModFluids;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.entity.ExtendedCreature;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.hunter.HunterBaseEntity;
import de.teamlapen.vampirism.items.StakeItem;
import de.teamlapen.vampirism.modcompat.IMCHandler;
import de.teamlapen.vampirism.player.hunter.HunterPlayer;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.player.vampire.actions.VampireActions;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.block.BlockState;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.world.GameType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;

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

        if (mc.player == null || !mc.player.isAlive()) {
            renderFullTick = 0;
            screenPercentage = 0;
            return;
        }
        if (event.phase == TickEvent.Phase.END)
            return;

        @Nullable IFactionPlayer player = FactionPlayerHandler.get(mc.player).getCurrentFactionPlayer().orElse(null);
        if (player instanceof VampirePlayer) {
            handleScreenColorVampire((VampirePlayer) player);
        } else if (player instanceof HunterPlayer) {
            handleScreenColorHunter((HunterPlayer) player);
        } else {
            screenPercentage = 0;
            screenBottomPercentage = 0;
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

        if (event.getType() != RenderGameOverlayEvent.ElementType.CROSSHAIRS || mc.player == null || !mc.player.isAlive()) {
            return;
        }

        RayTraceResult p = Minecraft.getInstance().objectMouseOver;

        if (p != null && p.getType() == RayTraceResult.Type.ENTITY) {
            IVampirePlayer player = VampirePlayer.get(mc.player);
            if (player.getLevel() > 0 && !mc.player.isSpectator() && !player.getActionHandler().isActionActive(VampireActions.bat)) {
                Entity entity = ((EntityRayTraceResult) p).getEntity();
                LazyOptional<? extends IBiteableEntity> biteableOpt = LazyOptional.empty();
                if (entity instanceof IBiteableEntity) {
                    biteableOpt = LazyOptional.of(() -> (IBiteableEntity) entity);
                } else if (entity instanceof CreatureEntity && entity.isAlive()) {
                    biteableOpt = ExtendedCreature.getSafe(entity);
                } else if (entity instanceof PlayerEntity) {
                    biteableOpt = VampirePlayer.getOpt((PlayerEntity) entity);
                }
                biteableOpt.filter(iBiteableEntity -> iBiteableEntity.canBeBitten(player)).ifPresent(biteable -> {
                    int color = 0xFF0000;
                    if (entity instanceof HunterBaseEntity || ExtendedCreature.getSafe(entity).map(IExtendedCreatureVampirism::hasPoisonousBlood).orElse(false))
                        color = 0x099022;
                    renderBloodFangs(event.getMatrixStack(), this.mc.getMainWindow().getScaledWidth(), this.mc.getMainWindow().getScaledHeight(), MathHelper.clamp(biteable.getBloodLevelRelative(), 0.2F, 1F), color);
                    event.setCanceled(true);
                });
            }
            IHunterPlayer hunterPlayer = HunterPlayer.get(mc.player);
            if(hunterPlayer.getLevel() > 0 && !mc.player.isSpectator() && hunterPlayer.getRepresentingPlayer().getHeldItemMainhand().getItem() == ModItems.stake) {
                Entity entity = ((EntityRayTraceResult) p).getEntity();
                if (entity instanceof LivingEntity && entity instanceof IVampireMob) {
                    if (StakeItem.canKillInstant((LivingEntity) entity, mc.player)) {
                        if(((LivingEntity)entity).getHealth() > 0) {
                            this.renderStakeInstantKill(event.getMatrixStack(), this.mc.getMainWindow().getScaledWidth(), this.mc.getMainWindow().getScaledHeight());
                            event.setCanceled(true);
                        }
                    }
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
                                renderBloodFangs(event.getMatrixStack(), this.mc.getMainWindow().getScaledWidth(), this.mc.getMainWindow().getScaledHeight(), 1, 0xFF0000);
                                event.setCanceled(true);
                            }
                        });
                    }
                }
            }
        }
        //Render blood feed progress
        GameSettings gamesettings = this.mc.gameSettings;
        if (gamesettings.getPointOfView().func_243192_a() && this.mc.playerController.getCurrentGameType() != GameType.SPECTATOR) {

            float progress = VampirePlayer.getOpt(mc.player).map(VampirePlayer::getFeedProgress).orElse(0f);
            if (progress > 0) {
                RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                if (progress <= 1.0F) {
                    int x = this.mc.getMainWindow().getScaledWidth() / 2 - 8;
                    int y = this.mc.getMainWindow().getScaledHeight() / 2 - 7 + 16;
                    this.mc.getTextureManager().bindTexture(icons);

                    int l = (int) (progress * 14.0F) + 2;

                    this.blit(event.getMatrixStack(), x, y, 0, 19, 16, 2);
                    this.blit(event.getMatrixStack(), x, y, 16, 19, l, 2);
                }

            }
        }
    }

    /**
     * Render Level
     */
    @SubscribeEvent
    public void onRenderExperienceBar(RenderGameOverlayEvent.Post event) {

        if (event.getType() != RenderGameOverlayEvent.ElementType.EXPERIENCE || mc.player == null || !mc.player.isAlive()) {
            return;
        }
        MatrixStack stack = event.getMatrixStack();
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(GL11.GL_LIGHTING);
        IFactionPlayerHandler handler = FactionPlayerHandler.get(mc.player);
        IPlayableFaction<?> faction = handler.getCurrentFaction();
        if (mc.playerController.gameIsSurvivalOrAdventure() && faction != null && faction.renderLevel()) {
            // boolean flag1 = false;
            int color = faction.getColor().getRGB();
            int lord = handler.getLordLevel();
            String text;
            if (lord > 0) {
                String title = handler.getLordTitle().getString();
                text = title.substring(0, Math.min(3, title.length()));
            } else {
                text = "" + handler.getCurrentLevel();
            }
            int x = (this.mc.getMainWindow().getScaledWidth() - mc.fontRenderer.getStringWidth(text)) / 2 + VampirismConfig.CLIENT.guiLevelOffsetX.get();
            int y = this.mc.getMainWindow().getScaledHeight() - VampirismConfig.CLIENT.guiLevelOffsetY.get();
            mc.fontRenderer.drawString(stack, text, x + 1, y, 0);
            mc.fontRenderer.drawString(stack, text, x - 1, y, 0);
            mc.fontRenderer.drawString(stack, text, x, y + 1, 0);
            mc.fontRenderer.drawString(stack, text, x, y - 1, 0);
            mc.fontRenderer.drawString(stack, text, x, y, color);
        }
    }

    private boolean addTempPoison;
    private EffectInstance addedTempPoison;

    @SubscribeEvent
    public void onRenderHealthBarPost(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.HEALTH) {
            return;
        }
        if (addTempPoison) {
            mc.player.activePotionsMap.remove(Effects.POISON);
        }


    }

    @SubscribeEvent
    public void onRenderHealthBarPre(RenderGameOverlayEvent.Pre event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.HEALTH) {
            return;
        }
        addTempPoison = mc.player.isPotionActive(ModEffects.poison) && !mc.player.activePotionsMap.containsKey(Effects.POISON);

        if (addTempPoison) { //Add temporary dummy potion effect to trick renderer
            if (addedTempPoison == null) {
                addedTempPoison = new EffectInstance(Effects.POISON, 100);
            }
            mc.player.activePotionsMap.put(Effects.POISON, addedTempPoison);
        }

    }

    @SubscribeEvent
    public void onRenderFoodBar(RenderGameOverlayEvent.Pre event) {

        if (event.getType() != RenderGameOverlayEvent.ElementType.FOOD) {
            return;
        }

        if (Helper.isVampire(mc.player) && !IMCHandler.requestedToDisableBloodbar) {
            event.setCanceled(true);

            if (mc.playerController.gameIsSurvivalOrAdventure()) {
                IBloodStats stats = VampirePlayer.get(mc.player).getBloodStats();

                GlStateManager.enableBlend();

                this.mc.getTextureManager().bindTexture(icons);
                int left = this.mc.getMainWindow().getScaledWidth() / 2 + 91;
                int top = this.mc.getMainWindow().getScaledHeight() - ForgeIngameGui.right_height;
                ForgeIngameGui.right_height += 10;
                int blood = stats.getBloodLevel();
                int maxBlood = stats.getMaxBlood();
                int blood2 = blood - 20;
                int maxBlood2 = maxBlood - 20;
                for (int i = 0; i < 10; ++i) {
                    int idx = i * 2 + 1;
                    int x = left - i * 8 - 9;

                    // Draw Background
                    blit(event.getMatrixStack(), x, top, 0, idx <= maxBlood2 ? 9 : 0, 9, 9);

                    if (idx < blood) {
                        blit(event.getMatrixStack(), x, top, 9, idx < blood2 ? 9 : 0, 9, 9);
                        if (idx == blood2) {
                            blit(event.getMatrixStack(), x, top, 18, 9, 9, 9);
                        }
                    } else if (idx == blood) {
                        blit(event.getMatrixStack(), x, top, 18, 0, 9, 9);
                    }
                }
                this.mc.getTextureManager().bindTexture(AbstractGui.GUI_ICONS_LOCATION);
                GlStateManager.disableBlend();
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onRenderGameOverlay(RenderGameOverlayEvent.Pre event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) return;
        if ((screenPercentage > 0 || screenBottomPercentage > 0) && VampirismConfig.CLIENT.renderScreenOverlay.get()) {
            MatrixStack stack = event.getMatrixStack();
            stack.push();
            int w = (this.mc.getMainWindow().getScaledWidth());
            int h = (this.mc.getMainWindow().getScaledHeight());
            if (fullScreen) {
                // Render a see through colored square over the whole screen
                float r = (float) (screenColor >> 16 & 255) / 255.0F;
                float g = (float) (screenColor >> 8 & 255) / 255.0F;
                float b = (float) (screenColor & 255) / 255.0F;
                float a = (screenPercentage / 100f) * (screenColor >> 24 & 255) / 255F;

                RenderSystem.disableTexture();
                RenderSystem.enableBlend();
                RenderSystem.disableAlphaTest();
                RenderSystem.blendFuncSeparate(770, 771, 1, 0);
                RenderSystem.shadeModel(7425);
                Tessellator tessellator = Tessellator.getInstance();
                Matrix4f matrix = stack.getLast().getMatrix();
                BufferBuilder worldrenderer = tessellator.getBuffer();
                worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
                worldrenderer.pos(matrix, 0, h, this.getBlitOffset()).color(r, g, b, a).endVertex();
                worldrenderer.pos(matrix, w, h, this.getBlitOffset()).color(r, g, b, a).endVertex();
                worldrenderer.pos(matrix, w, 0, this.getBlitOffset()).color(r, g, b, a).endVertex();
                worldrenderer.pos(matrix, 0, 0, this.getBlitOffset()).color(r, g, b, a).endVertex();

                tessellator.draw();
                RenderSystem.shadeModel(7424);
                RenderSystem.disableBlend();
                RenderSystem.enableAlphaTest();
                RenderSystem.enableTexture();

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

                    this.fillGradient(stack, 0, 0, w, bh, screenColor, 0x000);
                    if (!OptifineHandler.isShaders())
                        this.fillGradient(stack, 0, h - bh, w, h, 0x00000000, screenColor);
                    this.fillGradient2(stack, 0, 0, bw, h, 0x000000, screenColor);
                    this.fillGradient2(stack, w - bw, 0, w, h, screenColor, 0x00);
                } else { //If here screenBottomPercentage has to be >0

                    // batmode border
                    int hh = 0;

                    hh = Math.round(h / (float) 4 * screenBottomPercentage / 100);

                    this.fillGradient(event.getMatrixStack(), 0, h - hh, w, h, 0x00000000, screenBottomColor);
                }

            }
            stack.pop();
        }
    }

    private void handleScreenColorHunter(HunterPlayer hunter) {
        if (hunter.getSpecialAttributes().isDisguised()) {
            screenPercentage = (int) (100 * hunter.getSpecialAttributes().getDisguiseProgress());
            screenColor = 0xff111111;
            fullScreen = false;
        } else if (hunter.getSpecialAttributes().isVampireNearby()) {
            screenPercentage = (int) (70 * hunter.getSpecialAttributes().getVampireNearbyProgress());
            screenColor = 0x80ba1836;
            fullScreen = false;
        } else {
            screenPercentage = (int) Math.max(0F, (screenPercentage / 20F) * 10F); //Fade out any (previously applied) screen overlay until we hit 0
        }
    }

    private void handleScreenColorVampire(VampirePlayer vampire) {

        //Main area/borders
        if (vampire.getActionHandler().isActionActive(VampireActions.vampire_rage)) {
            screenPercentage = 100;
            screenColor = 0xfff00000;
            fullScreen = false;
        } else if ((screenPercentage = vampire.getTicksInSun() / 2) > 0) {
            EffectInstance effect = mc.player.getActivePotionEffect(ModEffects.sunscreen);
            if (effect == null || effect.getAmplifier() < 5) {
                screenColor = 0xfffff755;
                fullScreen = false;
                if (vampire.getRepresentingPlayer().abilities.isCreativeMode || (effect != null && effect.getAmplifier() >= 3)) {
                    screenPercentage = Math.min(20, screenPercentage);
                }
                screenPercentage = Math.min(screenPercentage, VampirismConfig.BALANCE.vpMaxYellowBorderPercentage.get());
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

    private void renderBloodFangs(MatrixStack stack, int width, int height, float perc, int color) {

        float r = ((color & 0xFF0000) >> 16) / 256f;
        float g = ((color & 0xFF00) >> 8) / 256f;
        float b = (color & 0xFF) / 256f;
        this.mc.getTextureManager().bindTexture(icons);
        int left = width / 2 - 8;
        int top = height / 2 - 4;
        GL11.glEnable(GL11.GL_BLEND);
        GlStateManager.color4f(1f, 1f, 1f, 0.7F);
        blit(stack, left, top, 27, 0, 16, 10);
        GlStateManager.color4f(r, g, b, 0.8F);
        int percHeight = (int) (10 * perc);
        blit(stack, left, top + (10 - percHeight), 27, 10 - percHeight, 16, percHeight);
        GlStateManager.color4f(1F, 1F, 1F, 1F);
        GL11.glDisable(GL11.GL_BLEND);

    }

    private void renderStakeInstantKill(MatrixStack mStack, int width, int height) {
        if (this.mc.gameSettings.getPointOfView().func_243192_a() && this.mc.playerController.getCurrentGameType() != GameType.SPECTATOR) {
            GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR.param, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR.param, GlStateManager.SourceFactor.ONE.param, GlStateManager.DestFactor.ZERO.param);
            this.mc.textureManager.bindTexture(AbstractGui.GUI_ICONS_LOCATION);
            GlStateManager.color4f(158f / 256, 0, 0, 1);
            this.blit(mStack, (width - 15) / 2, (height - 15) / 2, 0, 0, 15, 15);
            int j = height / 2 - 7 + 16;
            int k = width / 2 - 8;
            this.blit(mStack, k, j, 68, 94, 16, 16);
            this.blit(mStack, k, j, 36, 94, 16, 4);
            this.blit(mStack, k, j, 52, 94, 17, 4);
        }
    }

}
