package de.teamlapen.vampirism.client.gui.overlay;

import com.mojang.blaze3d.platform.Window;
import de.teamlapen.faction.client.IMinecraftAccessor;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.api.world.entity.vampire.IVampireMob;
import de.teamlapen.vampirism.common.core.ModEffects;
import de.teamlapen.vampirism.common.core.ModFluids;
import de.teamlapen.vampirism.common.core.ModItems;
import de.teamlapen.vampirism.common.util.Helper;
import de.teamlapen.vampirism.common.world.entity.player.hunter.HunterPlayer;
import de.teamlapen.vampirism.common.world.entity.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.common.world.items.StakeItem;
import de.teamlapen.vampirism.common.world.items.SyringeItem;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
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

import java.util.stream.Stream;

public class VampirismHUDOverlay implements IMinecraftAccessor {

    protected static final Identifier CROSSHAIR_SPRITE = VIdentifier.mc("hud/crosshair");
    protected static final Identifier CROSSHAIR_ATTACK_INDICATOR_FULL_SPRITE = VIdentifier.mc("hud/crosshair_attack_indicator_full");
    protected static final Identifier CROSSHAIR_ATTACK_INDICATOR_BACKGROUND_SPRITE = VIdentifier.mc("hud/crosshair_attack_indicator_background");
    protected static final Identifier CROSSHAIR_ATTACK_INDICATOR_PROGRESS_SPRITE = VIdentifier.mc("hud/crosshair_attack_indicator_progress");

    public static final Identifier FANG_EMPTY_SPRITE = VIdentifier.mod("fang/fang_empty");
    public static final Identifier FANG_FULL_SPRITE = VIdentifier.mod("fang/fang_full");
    public static final Identifier FANG_POISON_SPRITE = VIdentifier.mod("fang/fang_poison");

    public static final Identifier BLOOD_DROP_EMPTY_SPRITE = VIdentifier.mod("blood_drop/blood_drop_empty");
    public static final Identifier BLOOD_DROP_FULL_SPRITE = VIdentifier.mod("blood_drop/blood_drop_full");
    public static final Identifier BLOOD_DROP_POISON_SPRITE = VIdentifier.mod("blood_drop/blood_drop_poison");

    public static final Identifier PROGRESS_BACKGROUND_SPRITE = VIdentifier.mod("fang/progress_background");
    public static final Identifier PROGRESS_FOREGROUND_SPRITE = VIdentifier.mod("fang/progress_foreground");

    private boolean addTempPoison;

    @SubscribeEvent
    public void onRenderCrosshair(RenderGuiLayerEvent.Pre event) {
        if (event.getName() != VanillaGuiLayers.CROSSHAIR) return;

        LocalPlayer player = mc().player;
        HitResult hit = mc().hitResult;
        if (player == null || !player.isAlive() || hit == null) return;

        Window window = mc().getWindow();
        int screenWidth  = window.getGuiScaledWidth();
        int screenHeight = window.getGuiScaledHeight();
        GuiGraphicsExtractor graphics = event.getGuiGraphics();

        if (hit instanceof EntityHitResult entityHit) {
            Entity target = entityHit.getEntity();
            if (!target.isInvisibleTo(player) && tryRenderEntityCrosshair(graphics, player, target, screenWidth, screenHeight)) {
                event.setCanceled(true);
            }
        } else if (hit instanceof BlockHitResult blockHit) {
            if (tryRenderBlockBloodCrosshair(graphics, player, blockHit, screenWidth, screenHeight)) {
                event.setCanceled(true);
            }
        }

        renderProgressBar(graphics, player, window, FANG_EMPTY_SPRITE, VampirePlayer.get(player).getFeedProgress());
        renderProgressBar(graphics, player, window, BLOOD_DROP_EMPTY_SPRITE, player.getUseItem().getItem() instanceof SyringeItem ? (float) player.getTicksUsingItem() / player.getUseItem().getUseDuration(player) : -1f);
    }

    @SubscribeEvent
    public void onRenderFoodBar(RenderGuiLayerEvent.Pre event) {
        LocalPlayer player = mc().player;
        if (player == null || !player.isAlive() || !Helper.isVampire(player)) return;

        if (event.getName() == VanillaGuiLayers.FOOD_LEVEL && !VampirismMod.services().imc().isRequestedToDisableBloodbar() && mc().gameMode.hasExperience()) {
            event.setCanceled(true);
        } else if (event.getName().equals(VanillaGuiLayers.AIR_LEVEL)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onRenderHealthBarPre(RenderGuiLayerEvent.Pre event) {
        if (event.getName() != VanillaGuiLayers.PLAYER_HEALTH) return;

        LocalPlayer player = mc().player;
        if (player == null) return;

        addTempPoison = player.hasEffect(ModEffects.TOXICANT) && !player().vampirism$activeEffects().containsKey(MobEffects.POISON);

        if (addTempPoison) {
            player().vampirism$activeEffects().put(MobEffects.POISON, new MobEffectInstance(MobEffects.POISON, 100));
        }
    }

    @SubscribeEvent
    public void onRenderHealthBarPost(RenderGuiLayerEvent.Post event) {
        if (event.getName() != VanillaGuiLayers.PLAYER_HEALTH) return;

        if (addTempPoison) {
            player().vampirism$activeEffects().remove(MobEffects.POISON);
        }
    }

    private boolean tryRenderEntityCrosshair(GuiGraphicsExtractor graphics, LocalPlayer player, Entity target, int width, int height) {
        return tryRenderSyringeCrosshair(graphics, player, target, width, height) || tryRenderVampireCrosshair(graphics, player, target, width, height) || tryRenderStakeCrosshair(graphics, player, target, width, height);
    }

    private boolean tryRenderVampireCrosshair(GuiGraphicsExtractor graphics, LocalPlayer player, Entity target, int width, int height) {
        VampirePlayer vampire = VampirePlayer.get(player);
        if (vampire.getLevel() == 0 || player.isSpectator() || vampire.getSkillProperties().bat) return false;

        return SyringeItem.getBiteable(target)
                .filter(b -> b.canBeBitten(vampire))
                .map(biteable -> {
                    renderBloodCrosshair(graphics, width, height, biteable.getBloodLevelRelative(), SyringeItem.isPoisonous(target), FANG_EMPTY_SPRITE, FANG_FULL_SPRITE, FANG_POISON_SPRITE);
                    return true;
                }).orElse(false);
    }

    private boolean tryRenderSyringeCrosshair(GuiGraphicsExtractor graphics, LocalPlayer player, Entity target, int width, int height) {
        if (player.isSpectator()) return false;
        if (Stream.of(player.getMainHandItem(), player.getOffhandItem()).noneMatch(stack -> stack.getItem() instanceof SyringeItem)) return false;
        if (!SyringeItem.isPlayerFacingEntity(player, target)) return false;

        return SyringeItem.getBiteable(target)
                .filter(b -> b.canBeBitten(null))
                .map(biteable -> {
                    renderBloodCrosshair(graphics, width, height, biteable.getBloodLevelRelative(), SyringeItem.isPoisonous(target), BLOOD_DROP_EMPTY_SPRITE, BLOOD_DROP_FULL_SPRITE, BLOOD_DROP_POISON_SPRITE);
                    return true;
                }).orElse(false);
    }

    private boolean tryRenderStakeCrosshair(GuiGraphicsExtractor graphics, LocalPlayer player, Entity target, int width, int height) {
        HunterPlayer hunter = HunterPlayer.get(player);
        if (hunter.getLevel() == 0 || player.isSpectator()) return false;
        if (player.getMainHandItem().getItem() != ModItems.STAKE.get()) return false;
        if (!(target instanceof LivingEntity living) || !(target instanceof IVampireMob)) return false;
        if (!StakeItem.canKillInstantly(living, player) || living.getHealth() <= 0) return false;

        renderStakeInstantKill(graphics, width, height);
        return true;
    }

    private boolean tryRenderBlockBloodCrosshair(GuiGraphicsExtractor graphics, LocalPlayer player, BlockHitResult blockHit, int width, int height) {
        ClientLevel level = mc().level;
        if (level == null) return false;

        BlockPos pos = blockHit.getBlockPos();
        VampirePlayer vampire = VampirePlayer.get(player);
        if (!VampirePlayer.isBlockBiteable(level, pos, blockHit.getDirection()) || !vampire.wantsBlood()) return false;

        BlockState state = level.getBlockState(pos);
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity == null) return false;

        var handler = level.getCapability(Capabilities.Fluid.BLOCK, pos, state, blockEntity, null);
        if (handler == null) return false;

        try (Transaction transaction = Transaction.openRoot()) {
            if (ResourceHandlerUtil.move(handler, vampire.getBloodStats(), x -> x.is(ModFluids.BLOOD.get()), 1000, transaction) > 0) {
                renderBloodCrosshair(graphics, width, height, 1f, false, FANG_EMPTY_SPRITE, FANG_FULL_SPRITE, FANG_POISON_SPRITE);
                return true;
            }
        }

        return false;
    }

    private void renderBloodCrosshair(GuiGraphicsExtractor graphics, int width, int height, float percent, boolean poisonous, Identifier emptySprite, Identifier fullSprite, Identifier poisonSprite) {
        TextureAtlasSprite sprite = graphics.guiSprites.getSprite(emptySprite);
        int textureWidth = sprite.contents().width();
        int textureHeight = sprite.contents().height();
        int x = (width - textureWidth) / 2;
        int y = (height - textureHeight) / 2;

        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, emptySprite, textureWidth, textureHeight, 0, 0, x, y, textureWidth, textureHeight);

        int fillHeight = (int) (textureHeight * percent);
        if (fillHeight != textureHeight) fillHeight--;
        int vOffset = textureHeight - fillHeight;

        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, poisonous ? poisonSprite : fullSprite, textureWidth, textureHeight, 0, vOffset, x, y + vOffset, textureWidth, fillHeight);
    }

    private void renderProgressBar(GuiGraphicsExtractor graphics, LocalPlayer player, Window window, Identifier iconSprite, float progress) {
        if (!mc().options.getCameraType().isFirstPerson()) return;
        if (mc().gameMode == null || mc().gameMode.getPlayerMode() == GameType.SPECTATOR) return;
        if (progress <= 0 || progress > 1f) return;

        TextureAtlasSprite bgSprite = graphics.guiSprites.getSprite(PROGRESS_BACKGROUND_SPRITE);
        TextureAtlasSprite iconSpr = graphics.guiSprites.getSprite(iconSprite);
        int textureWidth = bgSprite.contents().width();
        int textureHeight = bgSprite.contents().height();

        int x = (window.getGuiScaledWidth()  - textureWidth) / 2;
        int y = (window.getGuiScaledHeight() - textureHeight) / 2 + iconSpr.contents().height() / 2 + textureHeight * 2;

        int barWidth = (int) (progress * textureWidth);

        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, PROGRESS_BACKGROUND_SPRITE, x, y, textureWidth, textureHeight);
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, PROGRESS_FOREGROUND_SPRITE, textureWidth, textureHeight, 0, 0, x, y, barWidth, textureHeight);
    }

    private void renderStakeInstantKill(GuiGraphicsExtractor graphics, int width, int height) {
        if (!mc().options.getCameraType().isFirstPerson()) return;
        if (mc().gameMode.getPlayerMode() == GameType.SPECTATOR) return;

        int color = ARGB.colorFromFloat(1f, 158 / 255f, 0f, 0f);
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, CROSSHAIR_SPRITE, (width - 15) / 2, (height - 15) / 2, 15, 15, color);

        LocalPlayer player = mc().player;
        float attackStrength = player.getAttackStrengthScale(0f);
        int centerX = width  / 2 - 8;
        int centerY = height / 2 - 7 + 16;

        boolean fullCharge = mc().crosshairPickEntity instanceof LivingEntity living && living.isAlive() && attackStrength >= 1f && player.getCurrentItemAttackStrengthDelay() > 5f;

        if (fullCharge) {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, CROSSHAIR_ATTACK_INDICATOR_FULL_SPRITE, centerX, centerY, 16, 16, color);
        } else if (attackStrength < 1f) {
            int barWidth = (int) (attackStrength * 17f);
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, CROSSHAIR_ATTACK_INDICATOR_BACKGROUND_SPRITE, centerX, centerY, 16, 4, color);
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, CROSSHAIR_ATTACK_INDICATOR_PROGRESS_SPRITE, 16, 4, 0, 0, centerX, centerY, barWidth, 4, color);
        }
    }
}
