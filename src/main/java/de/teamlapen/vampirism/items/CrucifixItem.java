package de.teamlapen.vampirism.items;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.IRefinementHandler;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.items.IFactionExclusiveItem;
import de.teamlapen.vampirism.api.items.IFactionLevelItem;
import de.teamlapen.vampirism.api.items.IItemWithTier;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.core.ModFactions;
import de.teamlapen.vampirism.core.ModRefinements;
import de.teamlapen.vampirism.core.tags.ModFactionTags;
import de.teamlapen.vampirism.entity.player.VampirismPlayerAttributes;
import de.teamlapen.vampirism.entity.player.hunter.skills.HunterSkills;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.entity.vampire.AdvancedVampireEntity;
import de.teamlapen.vampirism.entity.vampire.VampireBaronEntity;
import de.teamlapen.vampirism.mixin.accessor.EntityAccessor;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class CrucifixItem extends Item implements IItemWithTier, IFactionExclusiveItem, IFactionLevelItem<IHunterPlayer> {

    private final static String baseRegName = "crucifix";
    private final TIER tier;
    /**
     * All crucifix items are added to this set. This is used to add cooldown for all existing crucifix items at once.
     * Synchronized set, so no issues during Mod creation. Later on no synchronicity needed.
     */
    private static final Set<CrucifixItem> all_crucifix = Collections.synchronizedSet(new HashSet<>());

    public CrucifixItem(IItemWithTier.TIER tier) {
        super(new Properties().stacksTo(1));
        this.tier = tier;
        all_crucifix.add(this);
    }

    @Override
    public int getMinLevel(@NotNull ItemStack stack) {
        return 1;
    }

    @Nullable
    @Override
    public Holder<ISkill<?>> requiredSkill(@NotNull ItemStack stack) {
        if (tier == TIER.ULTIMATE) return HunterSkills.ULTIMATE_CRUCIFIX;
        return HunterSkills.CRUCIFIX_WIELDER;
    }

    @Override
    public @NotNull TagKey<IFaction<?>> getExclusiveFaction(@NotNull ItemStack stack) {
        return ModFactionTags.IS_HUNTER;
    }

    @Override
    public TIER getVampirismTier() {
        return tier;
    }

    @Override
    public @NotNull UseAnim getUseAnimation(ItemStack p_77661_1_) {
        return UseAnim.NONE;
    }


    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level world, @NotNull Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(itemstack);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        this.addTierInformation(tooltip);
        this.addFactionToolTips(stack, context, tooltip, flagIn, VampirismMod.proxy.getClientPlayer());
    }

    @Override
    public void inventoryTick(ItemStack stack, Level world, Entity entity, int slot, boolean held) {
        if (entity instanceof LivingEntity living && entity.tickCount % 16 == 8) {
            if (Helper.isVampire(living) && (living.getOffhandItem() == stack || living.getMainHandItem() == stack)) {
                living.addEffect(new MobEffectInstance(ModEffects.POISON, 20, 1));
                if (entity instanceof Player player) {
                    player.getInventory().removeItem(stack);
                    player.drop(stack, true);
                }
            }
            if (Helper.isHunter(living) && held && living instanceof Player player && all_crucifix.stream().noneMatch(s -> player.getCooldowns().isOnCooldown(s))) {
                var nearbyVampires = living.level().getEntities(entity, new AABB(living.blockPosition()).inflate(6), Helper::isVampire);
                var viewVector = living.getViewVector(1.0F).normalize();
                for (Entity e : nearbyVampires) {
                    if (e instanceof Player other && player.hasLineOfSight(other)) {
                        var targetVector = other.position().subtract(living.position());
                        TIER tier = getVampirismTier();
                        if (IRefinementHandler.get(other).filter(s -> s.isRefinementEquipped(ModRefinements.CRUCIFIX_RESISTANT)).isPresent()) {
                            int i = UtilLib.indexOf(TIER.values(), tier);
                            if (i > 0) {
                                tier = TIER.values()[i - 1];
                            } else if(i == 0) {
                                continue;
                            }
                        }

                        double distance = targetVector.lengthSqr();
                        double degrees = Math.toDegrees(Math.cos(viewVector.dot(targetVector.normalize())));
                        boolean effect = switch (tier) {
                            case ULTIMATE -> (distance < 100 && degrees < 20) || (distance < 56 && degrees < 55) || (distance < 25 && degrees < 70);
                            case ENHANCED -> (distance < 56 && degrees < 45) || (distance < 25 && degrees < 55);
                            case NORMAL -> (distance < 25 && degrees < 45);
                        };
                        if (effect) {
                            VampirePlayer vampirePlayer = VampirePlayer.get(other);
                            vampirePlayer.effectCrucifixSuppression();
                        }
                    }
                }
            }
        }
    }

    protected boolean affectsEntity(@NotNull LivingEntity e) {
        return e.getType().is(EntityTypeTags.UNDEAD) || Helper.isVampire(e);
    }


    @Override
    public void releaseUsing(ItemStack stack, Level world, LivingEntity entity, int p_77615_4_) {
        if (entity instanceof Player) {
            all_crucifix.forEach(item -> ((Player) entity).getCooldowns().addCooldown(item, getCooldown(stack)));
        }
    }

    protected int getCooldown(ItemStack stack) {
        return switch (tier) {
            case ENHANCED -> 100;
            case ULTIMATE -> 60;
            default -> 140;
        };
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
    public void onUseTick(@NotNull Level level, @NotNull LivingEntity entity, @NotNull ItemStack stack, int count) {
        for (LivingEntity nearbyEntity : entity.level().getNearbyEntities(LivingEntity.class, TargetingConditions.forCombat().selector(this::affectsEntity), entity, entity.getBoundingBox().inflate(getRange(stack)))) {
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
