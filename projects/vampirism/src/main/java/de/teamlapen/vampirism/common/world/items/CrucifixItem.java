package de.teamlapen.vampirism.common.world.items;

import de.teamlapen.faction.api.factions.refinements.IRefinementHandler;
import de.teamlapen.faction.api.factions.skills.ISkillHandler;
import de.teamlapen.faction.common.components.FactionRestriction;
import de.teamlapen.vampirism.api.VampirismTags;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.api.world.items.IItemWithTier;
import de.teamlapen.vampirism.common.core.ModEffects;
import de.teamlapen.vampirism.common.core.ModFactions;
import de.teamlapen.vampirism.common.core.ModRefinements;
import de.teamlapen.vampirism.common.util.Helper;
import de.teamlapen.vampirism.common.util.UtilLib;
import de.teamlapen.vampirism.common.world.entity.player.hunter.skills.HunterSkills;
import de.teamlapen.vampirism.common.world.entity.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.common.world.entity.vampire.AdvancedVampireEntity;
import de.teamlapen.vampirism.common.world.entity.vampire.VampireBaronEntity;
import de.teamlapen.vampirism.misc.mixin.accessor.EntityAccessor;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.component.UseCooldown;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Consumer;

public class CrucifixItem extends Item implements IItemWithTier {

    private final Tier tier;
    private static final Identifier COOLDOWN_GROUP = VIdentifier.mod("crucifix");

    public CrucifixItem(Tier tier, Properties properties) {
        super(FactionRestriction.builder(VampirismTags.Factions.IS_HUNTER).skill(tier == Tier.ULTIMATE ? HunterSkills.ULTIMATE_CRUCIFIX : HunterSkills.CRUCIFIX_WIELDER).message(HolyWaterBottleItem.MASSAGE_RESTRICTION_HOLY).apply(properties).stacksTo(1).component(DataComponents.USE_COOLDOWN, new UseCooldown( switch (tier) {
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
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (Helper.isHunter(player)) {
            if (ISkillHandler.isSkillEnabled(player, HunterSkills.CRUCIFIX_REPEL)) {
                ItemStack itemStack = player.getItemInHand(hand);
                if (level instanceof ServerLevel serverLevel) {
                    activePush(serverLevel, player, itemStack);
                }
                applyCooldown(player, itemStack);
            } else {
                player.startUsingItem(hand);
            }
            return InteractionResult.CONSUME;
        }
        return InteractionResult.FAIL;
    }

    protected void applyCooldown(Player player, ItemStack itemStack) {
        UseCooldown useCooldown = itemStack.get(DataComponents.USE_COOLDOWN);
        if (useCooldown == null) {
            throw new IllegalStateException("Crucifix itemstack does not have a UseCooldown component");
        }
        player.getCooldowns().addCooldown(itemStack, useCooldown.ticks() * 2);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        this.addTierInformation(tooltipComponents);
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, @Nullable EquipmentSlot slot) {
        if (entity instanceof LivingEntity livingEntity && slot != null && slot.getType() == EquipmentSlot.Type.HAND) {
            if (entity.tickCount % 16 == 8 && Helper.isVampire(entity)) {
                livingEntity.addEffect(new MobEffectInstance(ModEffects.POISON, 20, 1));
                if (entity instanceof Player player) {
                    player.getInventory().removeItem(stack);
                    player.drop(stack, true);
                }
            }

            if (entity instanceof Player player && player.getCooldowns().isOnCooldown(stack) && ISkillHandler.isSkillEnabled(player, HunterSkills.CRUCIFIX_REPEL)) {
                passivePush(level, player, stack, true);
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
            VampirePlayer vampire = VampirePlayer.get(player);
            int level = vampire.getLevel();
            int tier = (level >= ModFactions.VAMPIRE.value().getHighestReachableLevel()) ? 3 : (level >= 8) ? 2 : 1;

            if (vampire.getRefinementHandler().isRefinementEquipped(ModRefinements.CRUCIFIX_RESISTANT)) {
                tier++;
            }
            return tier;
        }

        return (entity instanceof VampireBaronEntity) ? 3 : (entity instanceof AdvancedVampireEntity) ? 2 : 1;
    }

    protected double determineSlowdown(int entityTier, boolean reducedStrength) {
        var slowdown = switch (tier) {
            case NORMAL -> entityTier > 1 ? 0.1 : 0.5;
            case ENHANCED -> entityTier > 2 ? 0.1 : 0.5;
            case ULTIMATE -> entityTier > 3 ? 0.3 : 0.5;
        };
        return reducedStrength ? slowdown * 0.25 : slowdown;
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
        if (level instanceof ServerLevel serverLevel) {
            passivePush(serverLevel, livingEntity, stack, false);
        }
    }

    protected void passivePush(ServerLevel level, LivingEntity entity, ItemStack stack, boolean reducedStrength) {
        for (LivingEntity nearbyEntity : level.getNearbyEntities(LivingEntity.class, TargetingConditions.forCombat().selector(this::affectsEntity), entity, entity.getBoundingBox().inflate(getRange(stack)))) {
            Vec3 baseVector = entity.position().subtract(nearbyEntity.position()).multiply(1, 0, 1).normalize(); //Normalized horizontal (xz) vector giving the direction towards the holder of this crucifix
            Vec3 oldDelta = nearbyEntity.getDeltaMovement();
            Vec3 horizontalDelta = oldDelta.multiply(1, 0, 1);
            double parallelScale = baseVector.dot(horizontalDelta);
            if (parallelScale > 0) {
                Vec3 parallelPart = baseVector.scale(parallelScale); //Part of delta that is parallel to baseVector
                double scale = determineSlowdown(determineEntityTier(nearbyEntity), reducedStrength);
                Vec3 newDelta = oldDelta.subtract(parallelPart.scale(scale)); //Substract parallel part from old Delta (scaled to still allow some movement)
                if (newDelta.lengthSqr() > oldDelta.lengthSqr()) { //Just to make sure we do not speed up the movement even though this should not be possible
                    newDelta = Vec3.ZERO;
                }
                //Unfortunately, Vanilla converts y-collision with ground into forward movement later on (in #move)
                //Therefore, we check for collision here and remove any y component if entity would collide with ground
                Vec3 collisionDelta = ((EntityAccessor) nearbyEntity).invokeCollide(newDelta);
                if (collisionDelta.y != newDelta.y && newDelta.y < 0) {
                    newDelta = newDelta.multiply(1, 0, 1);
                }

                nearbyEntity.setDeltaMovement(newDelta);
            }
            applyEffect(entity, level, nearbyEntity, stack);
        }
    }

    public void activePush(ServerLevel level, LivingEntity entity, ItemStack stack) {
        for (LivingEntity nearbyEntity : level.getNearbyEntities(LivingEntity.class, TargetingConditions.forCombat().selector(this::affectsEntity), entity, entity.getBoundingBox().inflate(getRange(stack)))) {
            Vec3 baseVector = entity.position().subtract(nearbyEntity.position()).multiply(1, 0, 1).normalize(); //Normalized horizontal (xz) vector giving the direction towards the holder of this crucifix
            Vec3 oldDelta = nearbyEntity.getDeltaMovement();
            double scale = determineSlowdown(determineEntityTier(nearbyEntity), false);
            Vec3 newDelta = oldDelta.subtract(baseVector.scale(scale * 3));
            Vec3 collisionDelta = ((EntityAccessor) nearbyEntity).invokeCollide(newDelta);
            if (collisionDelta.y != newDelta.y && newDelta.y < 0) {
                newDelta = newDelta.multiply(1, 0, 1);
            }
            nearbyEntity.setDeltaMovement(newDelta);
        }
    }

    protected void applyEffect(LivingEntity owner, ServerLevel level, LivingEntity entity, ItemStack stack) {
        if (entity instanceof Player player && Helper.isVampire(player)) {
            Tier tier = getVampirismTier();
            if (IRefinementHandler.get(player).filter(s -> s.isRefinementEquipped(ModRefinements.CRUCIFIX_RESISTANT)).isPresent()) {
                int i = UtilLib.indexOf(Tier.values(), tier);
                if (i > 0) {
                    tier = Tier.values()[i - 1];
                } else if(i == 0) {
                    return;
                }
            }
            var viewVector = owner.getViewVector(1.0F).normalize();
            var targetVector = owner.position().subtract(entity.position());
            double distance = targetVector.lengthSqr();
            double degrees = Math.toDegrees(Math.cos(viewVector.dot(targetVector.normalize())));
            boolean effect = switch (tier) {
                case ULTIMATE -> (distance < 100 && degrees < 20) || (distance < 56 && degrees < 55) || (distance < 25 && degrees < 70);
                case ENHANCED -> (distance < 56 && degrees < 45) || (distance < 25 && degrees < 55);
                case NORMAL -> (distance < 25 && degrees < 45);
            };
            if (effect) {
                VampirePlayer.get(player).effectCrucifixSuppression();
            }
        }
    }
}
