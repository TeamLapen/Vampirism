package de.teamlapen.vampirism.common.world.items;

import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.api.world.entity.IBiteableEntity;
import de.teamlapen.vampirism.common.core.ModItems;
import de.teamlapen.vampirism.common.core.ModSounds;
import de.teamlapen.vampirism.common.world.entity.ExtendedCreature;
import de.teamlapen.vampirism.common.world.entity.player.vampire.VampirePlayer;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;
import java.util.WeakHashMap;

public class SyringeItem extends Item {

    public static final int USE_DURATION = 40;
    private static final double MAX_FACING_ANGLE = 30.0;
    private static final double TARGET_SLOWDOWN = -0.75;
    private static final Identifier SLOWDOWN_ID = VIdentifier.mod("syringe_drain_slowdown");

    private static final WeakHashMap<Player, LivingEntity> TARGET_MAP = new WeakHashMap<>();

    public SyringeItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity interactionTarget, InteractionHand usedHand) {
        if (!isPlayerFacingEntity(player, interactionTarget) || player.level().isClientSide()) {
            return InteractionResult.PASS;
        }

        Optional<? extends IBiteableEntity> biteableOpt = getBiteable(interactionTarget);
        if (biteableOpt.isEmpty() || !biteableOpt.get().canBeBitten(null) || !biteableOpt.get().canDrain(BloodSyringeFluidHandler.LEVELS_PER_FILL)) {
            return InteractionResult.PASS;
        }

        TARGET_MAP.put(player, interactionTarget);
        applySlowdown(interactionTarget);
        player.startUsingItem(usedHand);

        return InteractionResult.CONSUME;
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        if (!(livingEntity instanceof Player player) || level.isClientSide()) return;

        LivingEntity target = TARGET_MAP.get(player);
        if (target == null || !target.isAlive() || !isPlayerFacingEntity(player, target)) {
            player.stopUsingItem();
        }
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        if (!(livingEntity instanceof Player player) || level.isClientSide()) return stack;

        LivingEntity target = TARGET_MAP.remove(player);
        if (target == null) return stack;

        removeSlowdown(target);

        if (!target.isAlive()) return stack;

        Optional<? extends IBiteableEntity> biteableOpt = getBiteable(target);
        biteableOpt.filter(b -> b.canBeBitten(null)).ifPresent(biteable -> {
            int drained = biteable.onSyringeUse(BloodSyringeFluidHandler.LEVELS_PER_FILL);
            if (drained <= 0) return;

            ItemStack filledStack = new ItemStack(ModItems.SYRINGE_BLOOD.get());

            if (!player.isCreative()) {
                if (stack.getCount() == 1) {
                    player.setItemInHand(player.getUsedItemHand(), filledStack);
                } else {
                    stack.shrink(1);
                    player.addItem(filledStack);
                }
            } else {
                player.addItem(filledStack);
            }

            level.playSound(null, player.blockPosition(), ModSounds.VAMPIRE_BITE.get(), SoundSource.PLAYERS, 1.0f, 1.0f);
        });

        return stack;
    }

    @Override
    public boolean releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (entity instanceof Player player) {
            LivingEntity target = TARGET_MAP.remove(player);
            if (target != null) {
                removeSlowdown(target);
            }
        }

        return false;
    }

    private static boolean isPlayerFacingEntity(Player player, LivingEntity target) {
        Vec3 lookVec = player.getLookAngle();
        Vec3 toTarget = target.position().add(0, target.getBbHeight() / 2.0, 0).subtract(player.getEyePosition()).normalize();
        double dot = lookVec.dot(toTarget);
        double angleRadians = Math.acos(Math.clamp(dot, -1.0, 1.0));

        return Math.toDegrees(angleRadians) <= MAX_FACING_ANGLE;
    }

    private static void applySlowdown(LivingEntity target) {
        var attribute = target.getAttribute(Attributes.MOVEMENT_SPEED);
        if (attribute == null) return;
        if (attribute.getModifier(SLOWDOWN_ID) != null) return;
        attribute.addTransientModifier(new AttributeModifier(SLOWDOWN_ID, TARGET_SLOWDOWN, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    }

    private static void removeSlowdown(LivingEntity target) {
        var attribute = target.getAttribute(Attributes.MOVEMENT_SPEED);
        if (attribute == null) return;
        attribute.removeModifier(SLOWDOWN_ID);
    }

    private static Optional<? extends IBiteableEntity> getBiteable(Entity target) {
        return switch (target) {
            case PathfinderMob mob when mob.isAlive() -> ExtendedCreature.getSafe(mob);
            case Player targetPlayer -> Optional.of(VampirePlayer.get(targetPlayer));
            case IBiteableEntity biteableEntity -> Optional.of(biteableEntity);
            default -> Optional.empty();
        };
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return USE_DURATION;
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return ItemUseAnimation.NONE;
    }
}
