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
import org.jetbrains.annotations.Nullable;

import java.util.*;


public class CrucifixItem extends Item implements IItemWithTier {

    private final TIER tier;
    private static final ResourceLocation COOLDOWN_GROUP = VResourceLocation.mod("crucifix");

    public CrucifixItem(TIER tier, Properties properties) {
        super(FactionRestriction.builder(ModFactionTags.IS_HUNTER).skill(tier ==TIER.ULTIMATE ? HunterSkills.ULTIMATE_CRUCIFIX : HunterSkills.CRUCIFIX_WIELDER).apply(properties).stacksTo(1).component(DataComponents.USE_COOLDOWN, new UseCooldown( switch (tier) {
            case NORMAL -> 7;
            case ENHANCED -> 5;
            case ULTIMATE -> 3;
        }, Optional.of(COOLDOWN_GROUP))));
        this.tier = tier;
    }

    @Override
    public TIER getVampirismTier() {
        return tier;
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack p_77661_1_) {
        return ItemUseAnimation.NONE;
    }


    @Override
    public InteractionResult use(Level world, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        player.startUsingItem(hand);
        return InteractionResult.CONSUME;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        this.addTierInformation(tooltip);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level world, Entity entity, int slot, boolean held) {
        if (entity instanceof LivingEntity living && entity.tickCount % 16 == 8 && (living.getOffhandItem() == stack || living.getMainHandItem() == stack)) {
            if (Helper.isVampire(entity)) {
                ((LivingEntity) entity).addEffect(new MobEffectInstance(ModEffects.POISON, 20, 1));
                if (entity instanceof Player player) {
                    player.getInventory().removeItem(stack);
                    player.drop(stack, true);
                }
            }
        }
    }

    protected boolean affectsEntity(LivingEntity e, Level level) {
        return e.getType().is(EntityTypeTags.UNDEAD) || Helper.isVampire(e);
    }


    @Override
    public boolean releaseUsing(ItemStack stack, Level world, LivingEntity entity, int p_77615_4_) {
        return true;
    }

    @Override
    public int getUseDuration(ItemStack pStack, LivingEntity p_344979_) {
        return 72000;
    }

    protected static int determineEntityTier(LivingEntity e) {
        if (e instanceof Player) {
            int level = VampirismPlayerAttributes.get((Player) e).vampireLevel;
            int tier = 1;
            if (level == ModFactions.VAMPIRE.value().getHighestReachableLevel()) {
                tier = 3;
            } else if (level >= 8) {
                tier = 2;
            }
            if (VampirePlayer.get((Player) e).getRefinementHandler().isRefinementEquipped(ModRefinements.CRUCIFIX_RESISTANT)) {
                tier++;
            }
            return tier;
        } else if (e instanceof VampireBaronEntity) {
            return 3;
        } else if (e instanceof AdvancedVampireEntity) {
            return 2;
        }
        return 1;
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
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int count) {
        if (level instanceof ServerLevel serverLevel) {
            for (LivingEntity nearbyEntity : serverLevel.getNearbyEntities(LivingEntity.class, TargetingConditions.forCombat().selector(this::affectsEntity), entity, entity.getBoundingBox().inflate(getRange(stack)))) {
                Vec3 baseVector = entity.position().subtract(nearbyEntity.position()).multiply(1, 0, 1).normalize(); //Normalized horizontal (xz) vector giving the direction towards the holder of this crucifix
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

}
