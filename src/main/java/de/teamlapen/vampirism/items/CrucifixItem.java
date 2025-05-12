package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.api.items.IItemWithTier;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.core.ModFactions;
import de.teamlapen.vampirism.core.ModRefinements;
import de.teamlapen.vampirism.core.tags.ModFactionTags;
import de.teamlapen.vampirism.entity.player.VampirismPlayerAttributes;
import de.teamlapen.vampirism.entity.player.hunter.skills.HunterSkills;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.entity.vampire.AdvancedVampireEntity;
import de.teamlapen.vampirism.entity.vampire.VampireBaronEntity;
import de.teamlapen.vampirism.items.component.FactionRestriction;
import de.teamlapen.vampirism.mixin.accessor.EntityAccessor;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.UseCooldown;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.*;

public class CrucifixItem extends Item implements IItemWithTier {

    private final Tier tier;
    private static final ResourceLocation COOLDOWN_GROUP = VResourceLocation.mod("crucifix");

    public CrucifixItem(Tier tier, Properties properties) {
        super(FactionRestriction.builder(ModFactionTags.IS_HUNTER).skill(tier == Tier.ULTIMATE ? HunterSkills.ULTIMATE_CRUCIFIX : HunterSkills.CRUCIFIX_WIELDER).apply(properties).stacksTo(1).component(DataComponents.USE_COOLDOWN, new UseCooldown( switch (tier) {
            case NORMAL -> 7;
            case ENHANCED -> 5;
            case ULTIMATE -> 3;
        }, Optional.of(COOLDOWN_GROUP))));
        this.tier = tier;
    }

    @Override
    public Tier getVampirismTier() {
        return tier;
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return ItemUseAnimation.NONE;
    }

    @Override
    public InteractionResult use(Level world, Player player, InteractionHand hand) {
        player.startUsingItem(hand);
        return InteractionResult.CONSUME;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        this.addTierInformation(tooltipComponents);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (entity instanceof LivingEntity livingEntity && entity.tickCount % 16 == 8 && (livingEntity.getOffhandItem() == stack || livingEntity.getMainHandItem() == stack)) {
            if (Helper.isVampire(entity)) {
                livingEntity.addEffect(new MobEffectInstance(ModEffects.POISON, 20, 1));
                if (entity instanceof Player player) {
                    player.getInventory().removeItem(stack);
                    player.drop(stack, true);
                }
            }
        }
    }

    protected boolean affectsEntity(LivingEntity entity, Level level) {
        return entity.getType().is(EntityTypeTags.UNDEAD) || Helper.isVampire(entity);
    }

    @Override
    public boolean releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        return true;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    protected static int determineEntityTier(LivingEntity entity) {
        if (entity instanceof Player player) {
            int level = VampirismPlayerAttributes.get(player).vampireLevel;
            int tier = (level >= ModFactions.VAMPIRE.value().getHighestReachableLevel()) ? 3 : (level >= 8) ? 2 : 1;

            if (VampirePlayer.get(player).getRefinementHandler().isRefinementEquipped(ModRefinements.CRUCIFIX_RESISTANT)) {
                tier++;
            }
            return tier;
        }

        return (entity instanceof VampireBaronEntity) ? 3 : (entity instanceof AdvancedVampireEntity) ? 2 : 1;
    }

    protected double determineSlowdown(int entityTier) {
        return switch (tier) {
            case NORMAL -> entityTier > 1 ? 0.1 : 0.5;
            case ENHANCED -> entityTier > 2 ? 0.1 : 0.5;
            case ULTIMATE -> entityTier > 3 ? 0.3 : 0.5;
        };
    }

    protected int getRange(ItemStack stack) {
        return switch (tier) {
            case ENHANCED -> 8;
            case ULTIMATE -> 10;
            default -> 4;
        };
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        if (!(level instanceof ServerLevel serverLevel)) return;

        for (LivingEntity nearbyEntity : serverLevel.getNearbyEntities(LivingEntity.class, TargetingConditions.forCombat().selector(this::affectsEntity), livingEntity, livingEntity.getBoundingBox().inflate(getRange(stack)))) {
            Vec3 baseVector = livingEntity.position().subtract(nearbyEntity.position()).multiply(1, 0, 1).normalize(); //Normalized horizontal (xz) vector giving the direction towards the holder of this crucifix
            Vec3 oldDelta = nearbyEntity.getDeltaMovement();
            Vec3 horizontalDelta = oldDelta.multiply(1, 0, 1);
            double parallelScale = baseVector.dot(horizontalDelta);
            if (parallelScale > 0) {
                Vec3 parallelPart = baseVector.scale(parallelScale); //Part of delta that is parallel to baseVector
                double scale = determineSlowdown(determineEntityTier(nearbyEntity));
                Vec3 newDelta = oldDelta.subtract(parallelPart.scale(scale)); //Substract parallel part from old Delta (scaled to still allow some movement)
                if (newDelta.lengthSqr() > oldDelta.lengthSqr()) { //Just to make sure we do not speed up the movement even though this should not be possible
                    newDelta = Vec3.ZERO;
                }
                //Unfortunately, Vanilla converts y-collision with ground into forward movement later on (in #move)
                //Therefore, we check for collision here and remove any y component if entity would collide with ground
                Vec3 collisionDelta = ((EntityAccessor) nearbyEntity).invoke_collide(newDelta);
                if (collisionDelta.y != newDelta.y && newDelta.y < 0) {
                    newDelta = newDelta.multiply(1, 0, 1);
                }

                nearbyEntity.setDeltaMovement(newDelta);
            }
        }
    }
}
