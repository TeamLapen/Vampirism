package de.teamlapen.vampirism.client.gui.overlay;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import de.teamlapen.lib.lib.client.gui.ExtendedGui;
import de.teamlapen.lib.lib.util.FluidLib;
import de.teamlapen.lib.util.OptifineHandler;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.entity.IBiteableEntity;
import de.teamlapen.vampirism.api.entity.IExtendedCreatureVampirism;
import de.teamlapen.vampirism.api.entity.hunter.IHunterMob;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.vampire.IVampireMob;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.core.ModFluids;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.entity.ExtendedCreature;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.items.StakeItem;
import de.teamlapen.vampirism.modcompat.IMCHandler;
import de.teamlapen.vampirism.player.VampirismPlayerAttributes;
import de.teamlapen.vampirism.player.hunter.HunterPlayer;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.player.vampire.actions.VampireActions;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

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
    private boolean addTempPoison;
    private MobEffectInstance addedTempPoison;

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

        @Nullable IFactionPlayer<?> player = FactionPlayerHandler.getOpt(mc.player).resolve().flatMap(FactionPlayerHandler::getCurrentFactionPlayer).orElse(null);
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
    public void onRenderCrosshair(RenderGameOverlayEvent.PreLayer event) {

        if (event.getOverlay() != ForgeIngameGui.CROSSHAIR_ELEMENT || mc.player == null || !mc.player.isAlive()) {
            return;
        }

        HitResult p = Minecraft.getInstance().hitResult;

        if (p != null && p.getType() == HitResult.Type.ENTITY) {
            Entity entity = ((EntityHitResult) p).getEntity();
            if (!entity.isInvisible()) {
                VampirismPlayerAttributes atts = VampirismPlayerAttributes.get(mc.player);
                if (atts.vampireLevel > 0 && !mc.player.isSpectator() && !atts.getVampSpecial().bat) {
                    VampirePlayer.getOpt(mc.player).ifPresent(player -> {
                        LazyOptional<? extends IBiteableEntity> biteableOpt = LazyOptional.empty();
                        if (entity instanceof IBiteableEntity) {
                            biteableOpt = LazyOptional.of(() -> (IBiteableEntity) entity);
                        } else if (entity instanceof PathfinderMob && entity.isAlive()) {
                            biteableOpt = ExtendedCreature.getSafe(entity);
                        } else if (entity instanceof Player) {
                            biteableOpt = VampirePlayer.getOpt((Player) entity);
                        }
                        biteableOpt.filter(iBiteableEntity -> iBiteableEntity.canBeBitten(player)).ifPresent(biteable -> {
                            int color = 0xFF0000;
                            if (entity instanceof IHunterMob || ExtendedCreature.getSafe(entity).map(IExtendedCreatureVampirism::hasPoisonousBlood).orElse(false))
                                color = 0x099022;
                            renderBloodFangs(event.getMatrixStack(), this.mc.getWindow().getGuiScaledWidth(), this.mc.getWindow().getGuiScaledHeight(), Mth.clamp(biteable.getBloodLevelRelative(), 0.2F, 1F), color);
                            event.setCanceled(true);
                        });
                    });

                }
                if (atts.hunterLevel > 0 && !mc.player.isSpectator() && mc.player.getMainHandItem().getItem() == ModItems.stake.get()) {
                    if (entity instanceof LivingEntity && entity instanceof IVampireMob) {
                        if (StakeItem.canKillInstant((LivingEntity) entity, mc.player)) {
                            if (((LivingEntity) entity).getHealth() > 0) {
                                this.renderStakeInstantKill(event.getMatrixStack(), this.mc.getWindow().getGuiScaledWidth(), this.mc.getWindow().getGuiScaledHeight());
                                event.setCanceled(true);
                            }
                        }
                    }
                }
            }

        } else if (p != null && p.getType() == HitResult.Type.BLOCK) {
            BlockState block = Minecraft.getInstance().level.getBlockState(((BlockHitResult) p).getBlockPos());
            if (ModBlocks.blood_container.get().equals(block.getBlock())) {
                if (VampirePlayer.getOpt(mc.player).map(VampirePlayer::wantsBlood).orElse(false)) {
                    BlockEntity tile = Minecraft.getInstance().level.getBlockEntity(((BlockHitResult) p).getBlockPos());
                    if (tile != null) {
                        tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).ifPresent(handler -> {
                            if (FluidLib.getFluidAmount(handler, ModFluids.blood.get()) > 0) {
                                renderBloodFangs(event.getMatrixStack(), this.mc.getWindow().getGuiScaledWidth(), this.mc.getWindow().getGuiScaledHeight(), 1, 0xFF0000);
                                event.setCanceled(true);
                            }
                        });
                    }
                }
            }
        }
        //Render blood feed progress
        Options gamesettings = this.mc.options;
        if (gamesettings.getCameraType().isFirstPerson() && this.mc.gameMode.getPlayerMode() != GameType.SPECTATOR) {

            float progress = VampirePlayer.getOpt(mc.player).map(VampirePlayer::getFeedProgress).orElse(0f);
            if (progress > 0) {
                RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                if (progress <= 1.0F) {
                    int x = this.mc.getWindow().getGuiScaledWidth() / 2 - 8;
                    int y = this.mc.getWindow().getGuiScaledHeight() / 2 - 7 + 16;
                    RenderSystem.setShaderTexture(0, icons);

                    int l = (int) (progress * 14.0F) + 2;

                    this.blit(event.getMatrixStack(), x, y, 0, 19, 16, 2);
                    this.blit(event.getMatrixStack(), x, y, 16, 19, l, 2);
                }

            }
        }
    }

    @SubscribeEvent
    public void onRenderFoodBar(RenderGameOverlayEvent.PreLayer event) {
        //disable foodbar if bloodbar is rendered
        if (event.getOverlay() == ForgeIngameGui.FOOD_LEVEL_ELEMENT && mc.player != null && Helper.isVampire(mc.player) && !IMCHandler.requestedToDisableBloodbar && mc.gameMode.hasExperience() && mc.player.isAlive()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onRenderGameOverlay(RenderGameOverlayEvent.Pre event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) return;
        if ((screenPercentage > 0 || screenBottomPercentage > 0) && VampirismConfig.CLIENT.renderScreenOverlay.get()) {
            PoseStack stack = event.getMatrixStack();
            stack.pushPose();
            int w = (this.mc.getWindow().getGuiScaledWidth());
            int h = (this.mc.getWindow().getGuiScaledHeight());
            if (fullScreen) {
                // Render a see through colored square over the whole screen
                float r = (float) (screenColor >> 16 & 255) / 255.0F;
                float g = (float) (screenColor >> 8 & 255) / 255.0F;
                float b = (float) (screenColor & 255) / 255.0F;
                float a = (screenPercentage / 100f) * (screenColor >> 24 & 255) / 255F;

                RenderSystem.disableTexture();
                RenderSystem.enableBlend();
                //RenderSystem.disableAlphaTest();
                RenderSystem.blendFuncSeparate(770, 771, 1, 0);
                Tesselator tessellator = Tesselator.getInstance();
                Matrix4f matrix = stack.last().pose();
                BufferBuilder worldrenderer = tessellator.getBuilder();
                worldrenderer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
                worldrenderer.vertex(matrix, 0, h, this.getBlitOffset()).color(r, g, b, a).endVertex();
                worldrenderer.vertex(matrix, w, h, this.getBlitOffset()).color(r, g, b, a).endVertex();
                worldrenderer.vertex(matrix, w, 0, this.getBlitOffset()).color(r, g, b, a).endVertex();
                worldrenderer.vertex(matrix, 0, 0, this.getBlitOffset()).color(r, g, b, a).endVertex();

                tessellator.end();
                RenderSystem.disableBlend();
                //RenderSystem.enableAlphaTest();
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
            stack.popPose();
        }
    }

    @SubscribeEvent
    public void onRenderHealthBarPost(RenderGameOverlayEvent.PostLayer event) {
        if (event.getOverlay() != ForgeIngameGui.PLAYER_HEALTH_ELEMENT) {
            return;
        }
        if (addTempPoison) {
            mc.player.activeEffects.remove(MobEffects.POISON);
        }


    }

    @SubscribeEvent
    public void onRenderHealthBarPre(RenderGameOverlayEvent.PreLayer event) {
        if (event.getOverlay() != ForgeIngameGui.PLAYER_HEALTH_ELEMENT) {
            return;
        }
        addTempPoison = mc.player.hasEffect(ModEffects.poison.get()) && !mc.player.activeEffects.containsKey(MobEffects.POISON);

        if (addTempPoison) { //Add temporary dummy potion effect to trick renderer
            if (addedTempPoison == null) {
                addedTempPoison = new MobEffectInstance(MobEffects.POISON, 100);
            }
            mc.player.activeEffects.put(MobEffects.POISON, addedTempPoison);
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
        if (vampire.getActionHandler().isActionActive(VampireActions.vampire_rage.get())) {
            screenPercentage = 100;
            screenColor = 0xfff00000;
            fullScreen = false;
        } else if ((screenPercentage = vampire.getTicksInSun() / 2) > 0) {
            MobEffectInstance effect = mc.player.getEffect(ModEffects.sunscreen.get());
            if (effect == null || effect.getAmplifier() < 5) {
                screenColor = 0xfffff755;
                fullScreen = false;
                if (vampire.getRepresentingPlayer().getAbilities().instabuild || (effect != null && effect.getAmplifier() >= 3)) {
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
        if (vampire.getActionHandler().isActionActive(VampireActions.bat.get())) {
            float batPercentage = vampire.getActionHandler().getPercentageForAction(VampireActions.bat.get());
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

    private void renderBloodFangs(PoseStack stack, int width, int height, float perc, int color) {

        float r = ((color & 0xFF0000) >> 16) / 256f;
        float g = ((color & 0xFF00) >> 8) / 256f;
        float b = (color & 0xFF) / 256f;
        RenderSystem.setShaderTexture(0, icons);
        int left = width / 2 - 8;
        int top = height / 2 - 4;
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1f, 1f, 1f, 0.7F);
        blit(stack, left, top, 27, 0, 16, 10);
        RenderSystem.setShaderColor(r, g, b, 0.8F);
        int percHeight = (int) (10 * perc);
        blit(stack, left, top + (10 - percHeight), 27, 10 - percHeight, 16, percHeight);
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        RenderSystem.disableBlend();

    }

    private void renderStakeInstantKill(PoseStack mStack, int width, int height) {
        if (this.mc.options.getCameraType().isFirstPerson() && this.mc.gameMode.getPlayerMode() != GameType.SPECTATOR) {
            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR.value, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR.value, GlStateManager.SourceFactor.ONE.value, GlStateManager.DestFactor.ZERO.value);
            RenderSystem.setShaderTexture(0, GuiComponent.GUI_ICONS_LOCATION);
            RenderSystem.setShaderColor(158f / 256, 0, 0, 1);
            this.blit(mStack, (width - 15) / 2, (height - 15) / 2, 0, 0, 15, 15);
            int j = height / 2 - 7 + 16;
            int k = width / 2 - 8;
            this.blit(mStack, k, j, 68, 94, 16, 16);
            this.blit(mStack, k, j, 36, 94, 16, 4);
            this.blit(mStack, k, j, 52, 94, 17, 4);
        }
    }

}
