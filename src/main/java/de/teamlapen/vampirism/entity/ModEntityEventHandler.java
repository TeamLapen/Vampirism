package de.teamlapen.vampirism.entity;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.difficulty.Difficulty;
import de.teamlapen.vampirism.api.difficulty.IAdjustableLevel;
import de.teamlapen.vampirism.api.entity.IExtendedCreatureVampirism;
import de.teamlapen.vampirism.api.items.IItemWithTier;
import de.teamlapen.vampirism.api.items.oil.IWeaponOil;
import de.teamlapen.vampirism.blockentity.TotemBlockEntity;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.core.ModTags;
import de.teamlapen.vampirism.entity.ai.goals.GolemTargetNonVillageFactionGoal;
import de.teamlapen.vampirism.entity.ai.goals.NearestTargetGoalModifier;
import de.teamlapen.vampirism.entity.hunter.HunterBaseEntity;
import de.teamlapen.vampirism.entity.minion.MinionEntity;
import de.teamlapen.vampirism.entity.player.VampirismPlayerAttributes;
import de.teamlapen.vampirism.entity.player.hunter.HunterPlayer;
import de.teamlapen.vampirism.entity.player.hunter.skills.HunterSkills;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.entity.vampire.VampireBaseEntity;
import de.teamlapen.vampirism.items.HunterAxeItem;
import de.teamlapen.vampirism.items.VampirismVampireSwordItem;
import de.teamlapen.vampirism.items.crossbow.VampirismCrossbowItem;
import de.teamlapen.vampirism.items.oil.EvasionOil;
import de.teamlapen.vampirism.mixin.accessor.GoalSelectorAccessor;
import de.teamlapen.vampirism.mixin.accessor.NearestAttackableTargetGoalAccessor;
import de.teamlapen.vampirism.util.DifficultyCalculator;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.OilUtils;
import de.teamlapen.vampirism.util.TotemHelper;
import it.unimi.dsi.fastutil.objects.Object2BooleanArrayMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.*;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Event handler for all entity related events
 */
public class ModEntityEventHandler {

    private final static Logger LOGGER = LogManager.getLogger(ModEntityEventHandler.class);
    private static final Object2BooleanMap<String> entityAIReplacementWarnMap = new Object2BooleanArrayMap<>();

    public static <T extends Mob, S extends LivingEntity, Q extends NearestAttackableTargetGoal<S>> void makeVampireFriendly(String name, @NotNull T e, @NotNull Class<Q> targetClass, @NotNull Class<S> targetEntityClass, int attackPriority, @NotNull Predicate<EntityType<? extends T>> typeCheck) {
        Goal target = null;
        for (WrappedGoal t : ((GoalSelectorAccessor) e.targetSelector).getAvailableGoals()) {
            Goal g = t.getGoal();
            if (targetClass.equals(g.getClass()) && t.getPriority() == attackPriority && targetEntityClass.equals(((NearestAttackableTargetGoalAccessor<?>) g).getTargetType())) {
                target = g;
                break;
            }
        }
        if (target != null) {
            @SuppressWarnings("unchecked")
            EntityType<? extends T> type = (EntityType<? extends T>) e.getType();
            if (typeCheck.test(type)) {
                ((NearestTargetGoalModifier) target).ignoreVampires(e);
            }
        } else {
            if (entityAIReplacementWarnMap.getOrDefault(name, true)) {
                LOGGER.warn("Could not modify {} attack target task for {}", name, e.getType().getDescription());
                entityAIReplacementWarnMap.put(name, false);
            }

        }
    }

    private final Set<ResourceLocation> unknownZombies = new HashSet<>();

    private boolean warnAboutGolem = true;

    @SubscribeEvent
    public void onFinalizeSpawn(@NotNull MobSpawnEvent.FinalizeSpawn event) {
        BlockPos pos = new BlockPos((int) (event.getX() - 0.6f), (int) event.getY(), (int) (event.getZ() - 0.6f)).below();
        if (!event.getLevel().hasChunkAt(pos)) return;
        BlockState blockState = event.getLevel().getBlockState(pos);

        if (blockState.is(ModTags.Blocks.NO_SPAWN) || (blockState.is(ModTags.Blocks.VAMPIRE_SPAWN) && event.getEntity().getClassification(false) != VReference.VAMPIRE_CREATURE_TYPE)) {
            event.setSpawnCancelled(true);
        }
    }

    @SubscribeEvent
    public void onEntityEquipmentChange(@NotNull LivingEquipmentChangeEvent event) {
        if (event.getSlot().getType() == EquipmentSlot.Type.ARMOR && event.getEntity() instanceof Player player) {
            VampirePlayer.get(player).requestNaturalArmorUpdate();
        }
    }

    @SubscribeEvent
    public void onEntityJoinWorld(@NotNull EntityJoinLevelEvent event) {
        if (!event.getLevel().isClientSide()) {
            if (event.getEntity() instanceof IAdjustableLevel entity) {
                if (entity.getEntityLevel() == -1) {
                    Difficulty d = DifficultyCalculator.findDifficultyForPos(event.getLevel(), event.getEntity().blockPosition(), 30);
                    int l = entity.suggestEntityLevel(d);
                    if (l > entity.getMaxEntityLevel()) {
                        l = entity.getMaxEntityLevel();
                    } else if (l < 0) {
                        event.setCanceled(true);
                    }
                    entity.setEntityLevel(l);
                    if (entity instanceof PathfinderMob) {
                        ((PathfinderMob) entity).setHealth(((PathfinderMob) entity).getMaxHealth());
                    }
                }
            }

            //------------------------------------------
            //  Individual entity class changes below. Will return once processed

            //Creeper AI changes for AvoidedByCreepers Skill
            if (VampirismConfig.BALANCE.creeperIgnoreVampire.get()) {
                if (event.getEntity() instanceof Creeper) {
                    ((Creeper) event.getEntity()).goalSelector.addGoal(3, new AvoidEntityGoal<>((Creeper) event.getEntity(), Player.class, 20, 1.1, 1.3, (target)-> Helper.appearsAsVampire(target, event.getEntity())));
                    //noinspection unchecked
                    makeVampireFriendly("creeper", (Creeper) event.getEntity(), NearestAttackableTargetGoal.class, Player.class, 1, type -> type == EntityType.CREEPER);

                    return;
                }
            }

            //Zombie AI changes
            if (VampirismConfig.BALANCE.zombieIgnoreVampire.get()) {
                if (event.getEntity() instanceof Zombie) {
                    //noinspection unchecked
                    makeVampireFriendly("zombie", (Zombie) event.getEntity(), NearestAttackableTargetGoal.class, Player.class, 2, type -> type == EntityType.ZOMBIE || type == EntityType.HUSK || type == EntityType.ZOMBIE_VILLAGER || type == EntityType.DROWNED);
                    //Also replace attack villager task for entities that have it
                    //noinspection unchecked
                    makeVampireFriendly("villager zombie", (Zombie) event.getEntity(), NearestAttackableTargetGoal.class, AbstractVillager.class, 3, type -> type == EntityType.ZOMBIE || type == EntityType.HUSK || type == EntityType.ZOMBIE_VILLAGER || type == EntityType.DROWNED);
                    return;
                }
            }

            if (VampirismConfig.BALANCE.skeletonIgnoreVampire.get()) {
                if (event.getEntity() instanceof Skeleton || event.getEntity() instanceof Stray) {
                    //noinspection unchecked
                    makeVampireFriendly("skeleton", (AbstractSkeleton) event.getEntity(), NearestAttackableTargetGoal.class, Player.class, 2, type -> type == EntityType.SKELETON);
                }
            }

            if (event.getEntity() instanceof IronGolem) {
                ((IronGolem) event.getEntity()).targetSelector.addGoal(4, new GolemTargetNonVillageFactionGoal((IronGolem) event.getEntity()));

                Goal mobTarget = null;

                for (WrappedGoal t : ((GoalSelectorAccessor) ((IronGolem) event.getEntity()).targetSelector).getAvailableGoals()) {
                    if (t.getGoal() instanceof NearestAttackableTargetGoal && t.getPriority() == 3 && Mob.class.equals(((NearestAttackableTargetGoalAccessor<?>) t.getGoal()).getTargetType())) {
                        mobTarget = t.getGoal();
                        break;
                    }
                }
                if (mobTarget != null) {
                    ((NearestTargetGoalModifier) mobTarget).ignoreFactionEntities();
                } else {
                    if (warnAboutGolem) {
                        LOGGER.warn("Could not replace villager iron golem target task");
                        warnAboutGolem = false;
                    }
                }
                return;
            }

            if (event.getEntity() instanceof Villager) {
                Optional<TotemBlockEntity> tile = TotemHelper.getTotemNearPos(((ServerLevel) event.getLevel()), event.getEntity().blockPosition(), true);
                if (tile.filter(t -> VReference.HUNTER_FACTION.equals(t.getControllingFaction())).isPresent()) {
                    ExtendedCreature.getSafe(event.getEntity()).ifPresent(e -> e.setPoisonousBlood(true));
                }
                //noinspection UnnecessaryReturnStatement
                return;
            }
        }
    }

    @SubscribeEvent
    public void onEntityLootingEvent(@NotNull LootingLevelEvent event) {
        if (event.getDamageSource() != null && event.getDamageSource().getEntity() instanceof Player) {
            @Nullable
            IItemWithTier.TIER hunterCoatTier = VampirismPlayerAttributes.get((Player) event.getDamageSource().getEntity()).getHuntSpecial().fullHunterCoat;
            if (hunterCoatTier == IItemWithTier.TIER.ENHANCED || hunterCoatTier == IItemWithTier.TIER.ULTIMATE) {
                event.setLootingLevel(Math.min(event.getLootingLevel() + 1, 3));
            }
        }
    }

    @SubscribeEvent
    public void onEyeHeightSet(EntityEvent.@NotNull Size event) {
        if (event.getEntity() instanceof VampireBaseEntity || event.getEntity() instanceof HunterBaseEntity) {
            event.setNewEyeHeight(event.getOldEyeHeight() * 0.875f);
        }
        if (event.getEntity() instanceof LivingEntity) {
            //(CoffinBlock.setSleepSize(event, ((LivingEntity) event.getEntity()));
        }
    }

    @SubscribeEvent
    public void onItemUseFinish(LivingEntityUseItemEvent.@NotNull Finish event) {
        if (event.getEntity() instanceof MinionEntity) {
            if (event.getItem().getItem() instanceof PotionItem) {
                ItemStack stack = event.getResultStack();
                stack.shrink(1);
                if (stack.isEmpty()) {
                    event.setResultStack(new ItemStack(Items.GLASS_BOTTLE));
                    return;
                }
                ((MinionEntity<?>) event.getEntity()).getInventory().ifPresent(inv -> inv.addItemStack(new ItemStack(Items.GLASS_BOTTLE)));
            }
        }
    }

    @SubscribeEvent
    public void onItemStartUsing(LivingEntityUseItemEvent.Start event) {
        if (event.getItem().getItem() instanceof VampirismCrossbowItem crossbowItem) {
            event.setDuration(crossbowItem.getCombinedUseDuration(event.getItem(), event.getEntity(), event.getEntity().getUsedItemHand()));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onLivingEquipmentChange(@NotNull LivingEquipmentChangeEvent event) {
        if (event.getTo().getItem() instanceof VampirismVampireSwordItem) {
            ((VampirismVampireSwordItem) event.getTo().getItem()).updateTrainedCached(event.getTo(), event.getEntity());
        }
    }

    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.@NotNull LivingTickEvent event) {
        if (event.getEntity() instanceof PathfinderMob) {
            event.getEntity().getCommandSenderWorld().getProfiler().push("vampirism_extended_creature");
            ExtendedCreature.getSafe(event.getEntity()).ifPresent(IExtendedCreatureVampirism::tick);
            event.getEntity().getCommandSenderWorld().getProfiler().pop();

        }
    }

    @SubscribeEvent
    public void onStartAttackHit(AttackEntityEvent event) {
        if (!Helper.isHunter(event.getEntity()) && OilUtils.getAppliedOil(event.getEntity().getMainHandItem()).isPresent()) {
            event.setCanceled(true);
            event.getEntity().displayClientMessage(Component.translatable("text.vampirism.oils.cannot_use"),true);
        }
    }

    @SubscribeEvent
    public void onActuallyHurt(@NotNull LivingHurtEvent event) {
        if (event.getSource().is(DamageTypes.PLAYER_ATTACK) && event.getSource().getEntity() instanceof Player player) {
            ItemStack stack = player.getMainHandItem();
            OilUtils.getAppliedOil(stack).ifPresent(oil -> {
                if (oil instanceof IWeaponOil) {
                    event.setAmount(event.getAmount() + ((IWeaponOil) oil).onHit(stack, event.getAmount(), ((IWeaponOil) oil), event.getEntity(), player));
                    oil.reduceDuration(stack, oil, oil.getDurationReduction());
                }
            });
        }
    }

    @SubscribeEvent
    public void onLivingDamage(@NotNull LivingDamageEvent event) {
        if (event.getSource().is(DamageTypes.PLAYER_ATTACK) && event.getSource().getEntity() instanceof Player player) {
            ItemStack stack = player.getMainHandItem();
            OilUtils.getAppliedOil(stack).ifPresent(oil -> {
                if (oil instanceof IWeaponOil) {
                    event.setAmount(event.getAmount() + ((IWeaponOil) oil).onDamage(stack, event.getAmount(), ((IWeaponOil) oil), event.getEntity(), player));
                }
            });
            if (Helper.isHunter(player) && Helper.isVampire(event.getEntity())) {
                Item item = stack.getItem();
                if (item instanceof HunterAxeItem && HunterPlayer.get(player).getSkillHandler().isSkillEnabled(HunterSkills.AXE2)) {
                    event.getEntity().addEffect(new MobEffectInstance(ModEffects.BLEEDING.get(), 20, 0, false, false, true));
                } else if (item == ModItems.STAKE.get() && HunterPlayer.get(player).getSkillHandler().isSkillEnabled(HunterSkills.STAKE2)) {
                    event.getEntity().addEffect(new MobEffectInstance(ModEffects.BLEEDING.get(), 40, 0, false, false, true));
                }
            }
        }
        if (event.getSource().is(ModTags.DamageTypes.ENTITY_PHYSICAL) && !event.getSource().is(DamageTypeTags.BYPASSES_ARMOR)) {
            for (ItemStack armorStack : event.getEntity().getArmorSlots()) {
                if (OilUtils.getAppliedOil(armorStack).map(oil -> {
                    if (oil instanceof EvasionOil evasionOil && evasionOil.evasionChance() > Optional.ofNullable(event.getSource().getEntity()).map(entity -> entity.level().random.nextFloat()).orElse(1f)) {
                        event.setAmount(0);
                        oil.reduceDuration(armorStack, oil, oil.getDurationReduction());
                        return true;
                    }
                    return false;
                }).orElse(false)) {
                    break;
                }
            }
        }
    }
}
