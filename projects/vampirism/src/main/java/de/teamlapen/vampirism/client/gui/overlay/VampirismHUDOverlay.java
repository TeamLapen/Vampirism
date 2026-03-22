package de.teamlapen.vampirism.client.gui.overlay;

import com.mojang.blaze3d.platform.Window;
import de.teamlapen.faction.client.IMinecraftAccessor;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.api.world.entity.IBiteableEntity;
import de.teamlapen.vampirism.api.world.entity.IExtendedCreatureVampirism;
import de.teamlapen.vampirism.api.world.entity.hunter.IHunterMob;
import de.teamlapen.vampirism.api.world.entity.vampire.IVampireMob;
import de.teamlapen.vampirism.common.core.ModEffects;
import de.teamlapen.vampirism.common.core.ModFluids;
import de.teamlapen.vampirism.common.core.ModItems;
import de.teamlapen.vampirism.common.util.Helper;
import de.teamlapen.vampirism.common.world.entity.ExtendedCreature;
import de.teamlapen.vampirism.common.world.entity.player.hunter.HunterPlayer;
import de.teamlapen.vampirism.common.world.entity.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.common.world.items.StakeItem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
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
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import java.util.Optional;

public class VampirismHUDOverlay implements IMinecraftAccessor {

    protected static final Identifier CROSSHAIR_SPRITE = VIdentifier.mc("hud/crosshair");
    protected static final Identifier CROSSHAIR_ATTACK_INDICATOR_FULL_SPRITE = VIdentifier.mc("hud/crosshair_attack_indicator_full");
    protected static final Identifier CROSSHAIR_ATTACK_INDICATOR_BACKGROUND_SPRITE = VIdentifier.mc("hud/crosshair_attack_indicator_background");
    protected static final Identifier CROSSHAIR_ATTACK_INDICATOR_PROGRESS_SPRITE = VIdentifier.mc("hud/crosshair_attack_indicator_progress");
    public static final Identifier FANG_SPRITE = VIdentifier.mod("fang/fang");
    public static final Identifier PROGRESS_BACKGROUND_SPRITE = VIdentifier.mod("fang/progress_background");
    public static final Identifier PROGRESS_FOREGROUND_SPRITE = VIdentifier.mod("fang/progress_foreground");

    private boolean addTempPoison;
    private MobEffectInstance addedTempPoison;


    @SubscribeEvent
    public void onRenderCrosshair(RenderGuiLayerEvent.Pre event) {
        if (event.getName() != VanillaGuiLayers.CROSSHAIR) return;

        LocalPlayer player = mc().player;
        HitResult hit = mc().hitResult;
        if (player == null || !player.isAlive() || hit == null) return;

        Window window = mc().getWindow();

        if (hit instanceof EntityHitResult entityHit) {
            Entity targetEntity = entityHit.getEntity();
            if (!targetEntity.isInvisibleTo(player)) {
                VampirePlayer vampire = VampirePlayer.get(player);

                if (vampire.getLevel() > 0 && !player.isSpectator() && !vampire.getSkillProperties().bat) {
                    Optional<? extends IBiteableEntity> biteableOpt = switch (targetEntity) {
                        case IBiteableEntity biteableEntity -> Optional.of(biteableEntity);
                        case PathfinderMob mob when mob.isAlive() -> ExtendedCreature.getSafe(mob);
                        case Player targetPlayer -> Optional.of(VampirePlayer.get(targetPlayer));
                        default -> Optional.empty();
                    };
                    biteableOpt.filter(biteable -> biteable.canBeBitten(vampire)).ifPresent(biteable -> {
                        int color = (targetEntity instanceof IHunterMob || ExtendedCreature.getSafe(targetEntity).map(IExtendedCreatureVampirism::hasPoisonousBlood).orElse(false))
                                ? ARGB.color(9, 144, 34)
                                : ARGB.color(255, 0, 0);
                        renderBloodFangs(event.getGuiGraphics(), window.getGuiScaledWidth(), window.getGuiScaledHeight(), Mth.clamp(biteable.getBloodLevelRelative(), 0.2F, 1F), color);
                        event.setCanceled(true);
                    });
                }

                HunterPlayer hunter = HunterPlayer.get(player);

                if (hunter.getLevel() > 0 && !player.isSpectator() && player.getMainHandItem().getItem() == ModItems.STAKE.get()) {
                    if (targetEntity instanceof LivingEntity livingTargetEntity && targetEntity instanceof IVampireMob && StakeItem.canKillInstantly(livingTargetEntity, player)) {
                        if (StakeItem.canKillInstantly(livingTargetEntity, player) && livingTargetEntity.getHealth() > 0) {
                            this.renderStakeInstantKill(event.getGuiGraphics(), window.getGuiScaledWidth(), window.getGuiScaledHeight());
                            event.setCanceled(true);
                        }
                    }
                }
            }
        } else if (hit instanceof BlockHitResult blockHit) {
            ClientLevel level = mc().level;
            if (level == null) return;

            BlockPos pos = blockHit.getBlockPos();
            BlockState state = level.getBlockState(pos);

            VampirePlayer vampire = VampirePlayer.get(player);
            if (VampirePlayer.isBlockBiteable(level, pos, blockHit.getDirection()) && vampire.wantsBlood()) {
                BlockEntity blockEntity = level.getBlockEntity(pos);
                if (blockEntity != null) {
                    Optional.ofNullable(level.getCapability(Capabilities.Fluid.BLOCK, pos, state, blockEntity, null)).ifPresent(handler -> {
                        try(Transaction transaction = Transaction.openRoot()) {
                            if (ResourceHandlerUtil.move(handler, vampire.getBloodStats(),x -> x.is(ModFluids.BLOOD.get()) ,1000, transaction) > 0) {
                                renderBloodFangs(event.getGuiGraphics(), window.getGuiScaledWidth(), window.getGuiScaledHeight(), 1, ARGB.color(255, 0, 0));
                                event.setCanceled(true);
                            }
                        }
                    });
                }
            }
        }

        // Blood feed progress
        if (mc().options.getCameraType().isFirstPerson() && mc().gameMode != null && mc().gameMode.getPlayerMode() != GameType.SPECTATOR) {
            float progress = VampirePlayer.get(player).getFeedProgress();
            if (progress > 0) {
//                RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                if (progress <= 1.0F) {
                    int x = window.getGuiScaledWidth() / 2 - 8;
                    int y = window.getGuiScaledHeight() / 2 + 9;
                    int l = (int) (progress * 14.0F) + 2;

                    event.getGuiGraphics().blitSprite(RenderPipelines.GUI_TEXTURED, PROGRESS_BACKGROUND_SPRITE, x, y, 16, 2);
                    event.getGuiGraphics().blitSprite(RenderPipelines.GUI_TEXTURED, PROGRESS_FOREGROUND_SPRITE, 16, 2, 0, 0, x, y, l, 2);
                }
            }
        }
    }

    @SubscribeEvent
    public void onRenderFoodBar(RenderGuiLayerEvent.Pre event) {
        if (mc().player == null || !mc().player.isAlive() || !Helper.isVampire(mc().player)) return;
        //disable foodbar if bloodbar is rendered
        if (event.getName() == VanillaGuiLayers.FOOD_LEVEL && !VampirismMod.services().imc().isRequestedToDisableBloodbar() && mc().gameMode.hasExperience()) {
            event.setCanceled(true);
        }
        if (event.getName().equals(VanillaGuiLayers.AIR_LEVEL)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onRenderHealthBarPost(RenderGuiLayerEvent.Post event) {
        if (event.getName() != VanillaGuiLayers.PLAYER_HEALTH) {
            return;
        }
        if (addTempPoison) {
            player().vampirism$activeEffects().remove(MobEffects.POISON);
        }


    }

    @SubscribeEvent
    public void onRenderHealthBarPre(RenderGuiLayerEvent.Pre event) {
        if (event.getName() != VanillaGuiLayers.PLAYER_HEALTH) {
            return;
        }
        addTempPoison = mc().player.hasEffect(ModEffects.TOXICANT) && !player().vampirism$activeEffects().containsKey(MobEffects.POISON);

        if (addTempPoison) { //Add temporary dummy potion effect to trick renderer
            if (addedTempPoison == null) {
                addedTempPoison = new MobEffectInstance(MobEffects.POISON, 100);
            }
            player().vampirism$activeEffects().put(MobEffects.POISON, addedTempPoison);
        }

    }

    private void renderBloodFangs(GuiGraphics graphics, int width, int height, float perc, int color) {
        int left = width / 2 - 8;
        int top = height / 2 - 4;
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, FANG_SPRITE, left, top, 16, 8);
        int percHeight = (int) (10f * (1f-perc));
        graphics.vampirism$blitSpriteTiledOffset(FANG_SPRITE, left, top, 16, 8, 0, percHeight, color);
    }

    private void renderStakeInstantKill(GuiGraphics graphics, int width, int height) {
        if (this.mc().options.getCameraType().isFirstPerson() && this.mc().gameMode.getPlayerMode() != GameType.SPECTATOR) {
//            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            int color = ARGB.colorFromFloat(1f, 158 / 256f, 0, 0);
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, CROSSHAIR_SPRITE, (graphics.guiWidth() - 15) / 2, (graphics.guiHeight() - 15) / 2, 15, 15, color);

            float f = this.mc().player.getAttackStrengthScale(0.0F);
            boolean flag = false;
            if (this.mc().crosshairPickEntity != null && this.mc().crosshairPickEntity instanceof LivingEntity && f >= 1.0F) {
                flag = this.mc().player.getCurrentItemAttackStrengthDelay() > 5.0F;
                flag &= this.mc().crosshairPickEntity.isAlive();
            }

            int j = graphics.guiHeight() / 2 - 7 + 16;
            int k = graphics.guiWidth() / 2 - 8;
            if (flag) {
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, CROSSHAIR_ATTACK_INDICATOR_FULL_SPRITE, k, j, 16, 16, color);
            } else if (f < 1.0F) {
                int l = (int) (f * 17.0F);
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, CROSSHAIR_ATTACK_INDICATOR_BACKGROUND_SPRITE, k, j, 16, 4, color);
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, CROSSHAIR_ATTACK_INDICATOR_PROGRESS_SPRITE, 16, 4, 0, 0, k, j, l, 4, color);
            }
        }
    }
}
