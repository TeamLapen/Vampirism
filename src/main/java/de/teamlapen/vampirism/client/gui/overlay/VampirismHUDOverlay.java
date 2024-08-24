package de.teamlapen.vampirism.client.gui.overlay;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.teamlapen.lib.lib.util.FluidLib;
import de.teamlapen.vampirism.api.entity.IBiteableEntity;
import de.teamlapen.vampirism.api.entity.IExtendedCreatureVampirism;
import de.teamlapen.vampirism.api.entity.hunter.IHunterMob;
import de.teamlapen.vampirism.api.entity.vampire.IVampireMob;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.core.ModFluids;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.entity.ExtendedCreature;
import de.teamlapen.vampirism.entity.player.VampirismPlayerAttributes;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.items.StakeItem;
import de.teamlapen.vampirism.mixin.accessor.LivingEntityAccessor;
import de.teamlapen.vampirism.modcompat.IMCHandler;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
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
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import java.util.Optional;

public class VampirismHUDOverlay {

    private final Minecraft mc;
    protected static final ResourceLocation CROSSHAIR_SPRITE = VResourceLocation.mc("hud/crosshair");
    protected static final ResourceLocation CROSSHAIR_ATTACK_INDICATOR_FULL_SPRITE = VResourceLocation.mc("hud/crosshair_attack_indicator_full");
    protected static final ResourceLocation CROSSHAIR_ATTACK_INDICATOR_BACKGROUND_SPRITE = VResourceLocation.mc("hud/crosshair_attack_indicator_background");
    protected static final ResourceLocation CROSSHAIR_ATTACK_INDICATOR_PROGRESS_SPRITE = VResourceLocation.mc("hud/crosshair_attack_indicator_progress");
    public static final ResourceLocation FANG_SPRITE = VResourceLocation.mod("fang/fang");
    public static final ResourceLocation PROGRESS_BACKGROUND_SPRITE = VResourceLocation.mod("fang/progress_background");
    public static final ResourceLocation PROGRESS_FOREGROUND_SPRITE = VResourceLocation.mod("fang/progress_foreground");

    private int screenColor = 0;
    private int screenPercentage = 0;
    private int renderFullTick = 0;
    private int rederFullOn, renderFullOff, renderFullColor;
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
    public void onClientTick(ClientTickEvent.Pre event) {
        if (mc.player == null || !mc.player.isAlive()) {
            renderFullTick = 0;
            screenPercentage = 0;
            return;
        }

        //If we are supposed to render fullscreen, we overwrite the other values and only render fullscreen
        if (renderFullTick > 0) {
            screenColor = renderFullColor;
            if (renderFullTick > renderFullOff) {
                screenPercentage = (int) (100 * (1 - (renderFullTick - renderFullOff) / (float) rederFullOn));
            } else {
                screenPercentage = (int) (100 * renderFullTick / (float) renderFullOff);
            }
            renderFullTick--;
        }

    }

    @SubscribeEvent
    public void onRenderCrosshair(RenderGuiLayerEvent.@NotNull Pre event) {

        if (event.getName() != VanillaGuiLayers.CROSSHAIR || mc.player == null || !mc.player.isAlive()) {
            return;
        }

        HitResult p = Minecraft.getInstance().hitResult;

        if (p != null && p.getType() == HitResult.Type.ENTITY) {
            Entity entity = ((EntityHitResult) p).getEntity();
            if (!entity.isInvisible()) {
                VampirismPlayerAttributes atts = VampirismPlayerAttributes.get(mc.player);
                if (atts.vampireLevel > 0 && !mc.player.isSpectator() && !atts.getVampSpecial().bat) {
                    VampirePlayer vampire = VampirePlayer.get(mc.player);
                    Optional<? extends IBiteableEntity> biteableOpt = Optional.empty();
                    if (entity instanceof IBiteableEntity) {
                        biteableOpt = Optional.of((IBiteableEntity) entity);
                    } else if (entity instanceof PathfinderMob && entity.isAlive()) {
                        biteableOpt = ExtendedCreature.getSafe(entity);
                    } else if (entity instanceof Player) {
                        biteableOpt = Optional.of(VampirePlayer.get((Player) entity));
                    }
                    biteableOpt.filter(iBiteableEntity -> iBiteableEntity.canBeBitten(vampire)).ifPresent(biteable -> {
                        int color = 0xFF0000;
                        if (entity instanceof IHunterMob || ExtendedCreature.getSafe(entity).map(IExtendedCreatureVampirism::hasPoisonousBlood).orElse(false)) {
                            color = 0x099022;
                        }
                        renderBloodFangs(event.getGuiGraphics(), this.mc.getWindow().getGuiScaledWidth(), this.mc.getWindow().getGuiScaledHeight(), Mth.clamp(biteable.getBloodLevelRelative(), 0.2F, 1F), color);
                        event.setCanceled(true);
                    });

                }
                if (atts.hunterLevel > 0 && !mc.player.isSpectator() && mc.player.getMainHandItem().getItem() == ModItems.STAKE.get()) {
                    if (entity instanceof LivingEntity && entity instanceof IVampireMob) {
                        if (StakeItem.canKillInstant((LivingEntity) entity, mc.player)) {
                            if (((LivingEntity) entity).getHealth() > 0) {
                                this.renderStakeInstantKill(event.getGuiGraphics(), this.mc.getWindow().getGuiScaledWidth(), this.mc.getWindow().getGuiScaledHeight());
                                event.setCanceled(true);
                            }
                        }
                    }
                }
            }

        } else if (p != null && p.getType() == HitResult.Type.BLOCK) {
            BlockState block = Minecraft.getInstance().level.getBlockState(((BlockHitResult) p).getBlockPos());
            if (ModBlocks.BLOOD_CONTAINER.get() == block.getBlock()) {
                if (VampirePlayer.get(mc.player).wantsBlood()) {
                    BlockEntity tile = Minecraft.getInstance().level.getBlockEntity(((BlockHitResult) p).getBlockPos());
                    if (tile != null) {
                        Optional.ofNullable(Minecraft.getInstance().level.getCapability(Capabilities.FluidHandler.BLOCK, ((BlockHitResult) p).getBlockPos(), block, tile, null)).ifPresent(handler -> {
                            if (FluidLib.getFluidAmount(handler, ModFluids.BLOOD.get()) > 0) {
                                renderBloodFangs(event.getGuiGraphics(), this.mc.getWindow().getGuiScaledWidth(), this.mc.getWindow().getGuiScaledHeight(), 1, 0xFF0000);
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

            float progress = VampirePlayer.get(mc.player).getFeedProgress();
            if (progress > 0) {
                RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                if (progress <= 1.0F) {
                    int x = this.mc.getWindow().getGuiScaledWidth() / 2 - 8;
                    int y = this.mc.getWindow().getGuiScaledHeight() / 2 - 7 + 16;

                    int l = (int) (progress * 14.0F) + 2;

                    event.getGuiGraphics().blitSprite(PROGRESS_BACKGROUND_SPRITE, x, y, 16, 2);
                    event.getGuiGraphics().blitSprite(PROGRESS_FOREGROUND_SPRITE, 16, 2, 0, 0, x, y, l, 2);
                }
            }
        }
    }

    @SubscribeEvent
    public void onRenderFoodBar(RenderGuiLayerEvent.@NotNull Pre event) {
        if (mc.player == null || !mc.player.isAlive() || !Helper.isVampire(mc.player)) return;
        //disable foodbar if bloodbar is rendered
        if (event.getName() == VanillaGuiLayers.FOOD_LEVEL && !IMCHandler.requestedToDisableBloodbar && mc.gameMode.hasExperience()) {
            event.setCanceled(true);
        }
        if (event.getName().equals(VanillaGuiLayers.AIR_LEVEL)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onRenderGameOverlay(RenderGuiEvent.@NotNull Pre event) {
        if ((screenPercentage > 0) && VampirismConfig.CLIENT.renderScreenOverlay.get()) {
            PoseStack stack = event.getGuiGraphics().pose();
            stack.pushPose();
            int w = (event.getGuiGraphics().guiWidth());
            int h = (event.getGuiGraphics().guiHeight());
            // Render a see through colored square over the whole screen
            float r = (float) (screenColor >> 16 & 255) / 255.0F;
            float g = (float) (screenColor >> 8 & 255) / 255.0F;
            float b = (float) (screenColor & 255) / 255.0F;
            float a = (screenPercentage / 100f) * (screenColor >> 24 & 255) / 255F;

            Matrix4f matrix = stack.last().pose();
            VertexConsumer buffer = event.getGuiGraphics().bufferSource().getBuffer(RenderType.guiOverlay());
            buffer.addVertex(matrix, 0, h, 0).setColor(r, g, b, a);
            buffer.addVertex(matrix, w, h, 0).setColor(r, g, b, a);
            buffer.addVertex(matrix, w, 0, 0).setColor(r, g, b, a);
            buffer.addVertex(matrix, 0, 0, 0).setColor(r, g, b, a);
            event.getGuiGraphics().flush();

            stack.popPose();
        }
    }

    @SubscribeEvent
    public void onRenderHealthBarPost(RenderGuiLayerEvent.@NotNull Post event) {
        if (event.getName() != VanillaGuiLayers.PLAYER_HEALTH) {
            return;
        }
        if (addTempPoison) {
            ((LivingEntityAccessor) mc.player).getActiveEffects().remove(MobEffects.POISON);
        }


    }

    @SubscribeEvent
    public void onRenderHealthBarPre(RenderGuiLayerEvent.@NotNull Pre event) {
        if (event.getName() != VanillaGuiLayers.PLAYER_HEALTH) {
            return;
        }
        addTempPoison = mc.player.hasEffect(ModEffects.POISON) && !((LivingEntityAccessor) mc.player).getActiveEffects().containsKey(MobEffects.POISON);

        if (addTempPoison) { //Add temporary dummy potion effect to trick renderer
            if (addedTempPoison == null) {
                addedTempPoison = new MobEffectInstance(MobEffects.POISON, 100);
            }
            ((LivingEntityAccessor) mc.player).getActiveEffects().put(MobEffects.POISON, addedTempPoison);
        }

    }

    private void renderBloodFangs(@NotNull GuiGraphics graphics, int width, int height, float perc, int color) {

        float r = ((color & 0xFF0000) >> 16) / 256f;
        float g = ((color & 0xFF00) >> 8) / 256f;
        float b = (color & 0xFF) / 256f;
        int left = width / 2 - 8;
        int top = height / 2 - 4;
        RenderSystem.enableBlend();
        graphics.setColor(1f, 1f, 1f, 0.7F);
        graphics.blitSprite(FANG_SPRITE, left, top, 16, 10);
        RenderSystem.setShaderColor(r, g, b, 0.8F);
        int percHeight = (int) (10 * perc);
        graphics.blitSprite(FANG_SPRITE, 16, 10, 0, 10 - percHeight, left, top + (10 - percHeight), 16, percHeight);
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        RenderSystem.disableBlend();

    }

    private void renderStakeInstantKill(@NotNull GuiGraphics graphics, int width, int height) {
        RenderSystem.enableBlend();
        if (this.mc.options.getCameraType().isFirstPerson() && this.mc.gameMode.getPlayerMode() != GameType.SPECTATOR) {
            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            graphics.setColor(158f / 256, 0, 0, 1);
            graphics.blitSprite(CROSSHAIR_SPRITE, (graphics.guiWidth() - 15) / 2, (graphics.guiHeight() - 15) / 2, 15, 15);

            float f = this.mc.player.getAttackStrengthScale(0.0F);
            boolean flag = false;
            if (this.mc.crosshairPickEntity != null && this.mc.crosshairPickEntity instanceof LivingEntity && f >= 1.0F) {
                flag = this.mc.player.getCurrentItemAttackStrengthDelay() > 5.0F;
                flag &= this.mc.crosshairPickEntity.isAlive();
            }

            int j = graphics.guiHeight() / 2 - 7 + 16;
            int k = graphics.guiWidth() / 2 - 8;
            if (flag) {
                graphics.blitSprite(CROSSHAIR_ATTACK_INDICATOR_FULL_SPRITE, k, j, 16, 16);
            } else if (f < 1.0F) {
                int l = (int) (f * 17.0F);
                graphics.blitSprite(CROSSHAIR_ATTACK_INDICATOR_BACKGROUND_SPRITE, k, j, 16, 4);
                graphics.blitSprite(CROSSHAIR_ATTACK_INDICATOR_PROGRESS_SPRITE, 16, 4, 0, 0, k, j, l, 4);
            }
            graphics.setColor(1, 1, 1, 1);
            RenderSystem.defaultBlendFunc();
        }
        RenderSystem.disableBlend();
    }
}
