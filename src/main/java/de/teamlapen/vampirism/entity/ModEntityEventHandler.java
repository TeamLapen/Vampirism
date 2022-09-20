package de.teamlapen.vampirism.entity;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.difficulty.Difficulty;
import de.teamlapen.vampirism.api.difficulty.IAdjustableLevel;
import de.teamlapen.vampirism.api.entity.IExtendedCreatureVampirism;
import de.teamlapen.vampirism.api.entity.factions.IFactionEntity;
import de.teamlapen.vampirism.api.items.IItemWithTier;
import de.teamlapen.vampirism.api.items.oil.IWeaponOil;
import de.teamlapen.vampirism.blocks.CastleBricksBlock;
import de.teamlapen.vampirism.blocks.CastleSlabBlock;
import de.teamlapen.vampirism.blocks.CastleStairsBlock;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModOils;
import de.teamlapen.vampirism.entity.goals.GolemTargetNonVillageFactionGoal;
import de.teamlapen.vampirism.entity.hunter.HunterBaseEntity;
import de.teamlapen.vampirism.entity.minion.MinionEntity;
import de.teamlapen.vampirism.entity.vampire.VampireBaseEntity;
import de.teamlapen.vampirism.items.VampirismVampireSword;
import de.teamlapen.vampirism.player.VampirismPlayerAttributes;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.tileentity.TotemHelper;
import de.teamlapen.vampirism.tileentity.TotemTileEntity;
import de.teamlapen.vampirism.util.DifficultyCalculator;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.OilUtils;
import it.unimi.dsi.fastutil.objects.Object2BooleanArrayMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PotionItem;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Predicate;

/**
 * Event handler for all entity related events
 */
public class ModEntityEventHandler {

    private final static Logger LOGGER = LogManager.getLogger(ModEntityEventHandler.class);
    private static final Predicate<LivingEntity> nonVampireCheck = entity -> !Helper.isVampire(entity);
    private static final Object2BooleanMap<String> entityAIReplacementWarnMap = new Object2BooleanArrayMap<>();

    public static <T extends MobEntity, S extends LivingEntity, Q extends NearestAttackableTargetGoal<S>> void makeVampireFriendly(String name, T e, Class<Q> targetClass, Class<S> targetEntityClass, int attackPriority, BiFunction<T, Predicate<LivingEntity>, Q> replacement, Predicate<EntityType<? extends T>> typeCheck) {
        Goal target = null;
        for (PrioritizedGoal t : e.targetSelector.availableGoals) {
            Goal g = t.getGoal();
            if (targetClass.equals(g.getClass()) && t.getPriority() == attackPriority && targetEntityClass.equals(((NearestAttackableTargetGoal<?>) g).targetType)) {
                target = g;
                break;
            }
        }
        if (target != null) {
            e.targetSelector.removeGoal(target);
            EntityType<? extends T> type = (EntityType<? extends T>) e.getType();
            if (typeCheck.test(type)) {
                e.targetSelector.addGoal(attackPriority, replacement.apply(e, nonVampireCheck));
            }
        } else {
            if (entityAIReplacementWarnMap.getOrDefault(name, true)) {
                LOGGER.warn("Could not replace {} attack target task for {}", name, e.getType().getDescription());
                entityAIReplacementWarnMap.put(name, false);
            }

        }
    }
    private final Set<ResourceLocation> unknownZombies = new HashSet<>();

    private boolean warnAboutGolem = true;

    @SubscribeEvent
    public void onAttachCapabilityEntity(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof CreatureEntity) {
            event.addCapability(REFERENCE.EXTENDED_CREATURE_KEY, ExtendedCreature.createNewCapability((CreatureEntity) event.getObject()));
        }
    }

    @SubscribeEvent
    public void onEntityCheckSpawn(LivingSpawnEvent.CheckSpawn event) {
        BlockPos pos = new BlockPos(event.getX() - 0.4F, event.getY(), event.getZ() - 0.4F).below();
        if (!event.getWorld().hasChunkAt(pos)) return;
        BlockState blockState = event.getWorld().getBlockState(pos);
        Block b = blockState.getBlock();
        boolean deny = false;
        CastleBricksBlock.EnumVariant v = null;

        if (b instanceof CastleBricksBlock) {
            deny = true;
            v = ((CastleBricksBlock) b).getVariant();
        } else if (b instanceof CastleSlabBlock) {
            deny = true;
            v = ((CastleSlabBlock) b).getVariant();
        } else if (b instanceof CastleStairsBlock) {
            deny = true;
            v = ((CastleStairsBlock) b).getVariant();
        }
        if (deny && (v == CastleBricksBlock.EnumVariant.DARK_STONE || !(event.getEntity().getClassification(false) == VReference.VAMPIRE_CREATURE_TYPE))) {
            event.setResult(Event.Result.DENY);
        }
    }

    @SubscribeEvent
    public void onEntityEquipmentChange(LivingEquipmentChangeEvent event) {
        if (event.getSlot().getType() == EquipmentSlotType.Group.ARMOR && event.getEntity() instanceof PlayerEntity) {
            VampirePlayer.getOpt((PlayerEntity) event.getEntity()).ifPresent(VampirePlayer::requestNaturalArmorUpdate);
        }
    }

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (!event.getWorld().isClientSide()) {
            if (event.getEntity() instanceof IAdjustableLevel) {
                IAdjustableLevel entity = (IAdjustableLevel) event.getEntity();
                if (entity.getLevel() == -1) {
                    Difficulty d = DifficultyCalculator.findDifficultyForPos(event.getWorld(), event.getEntity().blockPosition(), 30);
                    int l = entity.suggestLevel(d);
                    if (l > entity.getMaxLevel()) {
                        l = entity.getMaxLevel();
                    } else if (l < 0) {
                        event.setCanceled(true);
                    }
                    entity.setLevel(l);
                    if (entity instanceof CreatureEntity) {
                        ((CreatureEntity) entity).setHealth(((CreatureEntity) entity).getMaxHealth());
                    }
                }
            }

            //------------------------------------------
            //  Individual entity class changes below. Will return once processed

            //Creeper AI changes for AvoidedByCreepers Skill
            if (VampirismConfig.BALANCE.creeperIgnoreVampire.get()) {
                if (event.getEntity() instanceof CreeperEntity) {
                    ((CreeperEntity) event.getEntity()).goalSelector.addGoal(3, new AvoidEntityGoal<>((CreeperEntity) event.getEntity(), PlayerEntity.class, 20, 1.1, 1.3, Helper::isVampire));
                    makeVampireFriendly("creeper", (CreeperEntity) event.getEntity(), NearestAttackableTargetGoal.class, PlayerEntity.class, 1, (entity, predicate) -> new NearestAttackableTargetGoal<>(entity, PlayerEntity.class, 10, true, false, predicate), type -> type == EntityType.CREEPER);

                    return;
                }
            }

            //Zombie AI changes
            if (VampirismConfig.BALANCE.zombieIgnoreVampire.get()) {
                if (event.getEntity() instanceof ZombieEntity) {
                    makeVampireFriendly("zombie", (ZombieEntity) event.getEntity(), NearestAttackableTargetGoal.class, PlayerEntity.class, 2, (entity, predicate) -> entity instanceof DrownedEntity ? new NearestAttackableTargetGoal<>(entity, PlayerEntity.class, 10, true, false, predicate.and(((DrownedEntity) entity)::okTarget)) : new NearestAttackableTargetGoal<>(entity, PlayerEntity.class, 10, true, false, predicate), type -> type == EntityType.ZOMBIE || type == EntityType.HUSK || type == EntityType.ZOMBIE_VILLAGER || type == EntityType.DROWNED);
                    //Also replace attack villager task for entities that have it
                    makeVampireFriendly("villager zombie", (ZombieEntity) event.getEntity(), NearestAttackableTargetGoal.class, AbstractVillagerEntity.class, 3, (entity, predicate) -> new NearestAttackableTargetGoal<>(entity, AbstractVillagerEntity.class, 10, true, false, predicate), type -> type == EntityType.ZOMBIE || type == EntityType.HUSK || type == EntityType.ZOMBIE_VILLAGER || type == EntityType.DROWNED);
                    return;
                }
            }

            if (VampirismConfig.BALANCE.skeletonIgnoreVampire.get()) {
                if (event.getEntity() instanceof SkeletonEntity || event.getEntity() instanceof StrayEntity) {
                    makeVampireFriendly("skeleton", (AbstractSkeletonEntity) event.getEntity(), NearestAttackableTargetGoal.class, PlayerEntity.class, 2, (entity, predicate) -> new NearestAttackableTargetGoal<>(entity, PlayerEntity.class, 10, true, false, predicate), type -> type == EntityType.SKELETON || type == EntityType.STRAY);
                }
            }

            if (event.getEntity() instanceof IronGolemEntity) {
                ((IronGolemEntity) event.getEntity()).targetSelector.addGoal(4, new GolemTargetNonVillageFactionGoal((IronGolemEntity) event.getEntity()));

                Goal mobTarget = null;

                for (PrioritizedGoal t : ((IronGolemEntity) event.getEntity()).targetSelector.availableGoals) {
                    if (t.getGoal() instanceof NearestAttackableTargetGoal && t.getPriority() == 3 && MobEntity.class.equals(((NearestAttackableTargetGoal<?>) t.getGoal()).targetType)) {
                        mobTarget = t.getGoal();
                        break;
                    }
                }
                if (mobTarget != null) {
                    ((IronGolemEntity) event.getEntity()).targetSelector.removeGoal(mobTarget);
                    ((IronGolemEntity) event.getEntity()).targetSelector.addGoal(3, new NearestAttackableTargetGoal<>((IronGolemEntity) event.getEntity(), MobEntity.class, 5, false, false, entity -> entity instanceof IMob && !(entity instanceof IFactionEntity) && !(entity instanceof CreeperEntity)));
                } else {
                    if (warnAboutGolem) {
                        LOGGER.warn("Could not replace villager iron golem target task");
                        warnAboutGolem = false;
                    }
                }
                return;
            }

            if (event.getEntity() instanceof VillagerEntity) {
                Optional<TotemTileEntity> tile = TotemHelper.getTotemNearPos(((ServerWorld) event.getWorld()), event.getEntity().blockPosition(), true);
                if (tile.filter(t -> VReference.HUNTER_FACTION.equals(t.getControllingFaction())).isPresent()) {
                    ExtendedCreature.getSafe(event.getEntity()).ifPresent(e -> e.setPoisonousBlood(true));
                }
                return;
            }
        }
    }

    @SubscribeEvent
    public void onEntityLootingEvent(LootingLevelEvent event) {
        if (event.getDamageSource() != null && event.getDamageSource().getEntity() instanceof PlayerEntity) {
            @Nullable
            IItemWithTier.TIER hunterCoatTier = VampirismPlayerAttributes.get((PlayerEntity) event.getDamageSource().getEntity()).getHuntSpecial().fullHunterCoat;
            if (hunterCoatTier == IItemWithTier.TIER.ENHANCED || hunterCoatTier == IItemWithTier.TIER.ULTIMATE) {
                event.setLootingLevel(Math.min(event.getLootingLevel() + 1, 3));
            }
        }
    }

    @SubscribeEvent
    public void onEntityVisibilityCheck(LivingEvent.LivingVisibilityEvent event) {
        if (event.getEntity() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) event.getEntity();
            if (VampirismPlayerAttributes.get(player).getHuntSpecial().isDisguised()) {
                event.modifyVisibility((VampirismPlayerAttributes.get((PlayerEntity) event.getEntity()).getHuntSpecial().fullHunterCoat != null ? 0.5 : 1) * VampirismConfig.BALANCE.haDisguiseVisibilityMod.get());
            }
        }
    }

    @SubscribeEvent
    public void onEyeHeightSet(EntityEvent.Size event) {
        if (event.getEntity() instanceof VampireBaseEntity || event.getEntity() instanceof HunterBaseEntity) {
            event.setNewEyeHeight(event.getOldEyeHeight() * 0.875f);
        }
        if (event.getEntity() instanceof LivingEntity) {
            //(CoffinBlock.setSleepSize(event, ((LivingEntity) event.getEntity()));
        }
    }

    @SubscribeEvent
    public void onItemUseFinish(LivingEntityUseItemEvent.Finish event) {
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

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onLivingEquipmentChange(LivingEquipmentChangeEvent event) {
        if (event.getTo().getItem() instanceof VampirismVampireSword) {
            ((VampirismVampireSword) event.getTo().getItem()).updateTrainedCached(event.getTo(), event.getEntityLiving());
        }
    }

    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntity() instanceof CreatureEntity) {
            event.getEntity().getCommandSenderWorld().getProfiler().push("vampirism_extended_creature");
            ExtendedCreature.getSafe(event.getEntity()).ifPresent(IExtendedCreatureVampirism::tick);
            event.getEntity().getCommandSenderWorld().getProfiler().pop();

        }
    }

    @SubscribeEvent
    public void onStartAttackHit(AttackEntityEvent event) {
        if (!Helper.isHunter(event.getPlayer()) && OilUtils.getAppliedOil(event.getPlayer().getMainHandItem()).isPresent()) {
            event.setCanceled(true);
            event.getPlayer().displayClientMessage(new TranslationTextComponent("text.vampirism.oils.cannot_use"),true);
        }
    }

    @SubscribeEvent
    public void onActuallyHurt(LivingHurtEvent event) {
        if (event.getSource() instanceof EntityDamageSource && event.getSource().msgId.equals("player") && event.getSource().getEntity() instanceof PlayerEntity) {
            PlayerEntity player = ((PlayerEntity) event.getSource().getEntity());
            ItemStack stack = player.getMainHandItem();
            OilUtils.getAppliedOil(stack).ifPresent(oil -> {
                if (oil instanceof IWeaponOil) {
                    event.setAmount(event.getAmount() + ((IWeaponOil) oil).onHit(stack, event.getAmount(), ((IWeaponOil) oil), event.getEntityLiving(), player));
                    oil.reduceDuration(stack, oil, oil.getDurationReduction());
                }
            });
        }
    }

    @SubscribeEvent
    public void onLivingDamage(LivingDamageEvent event) {
        if (event.getSource() instanceof EntityDamageSource && event.getSource().msgId.equals("player") && event.getSource().getEntity() instanceof PlayerEntity) {
            PlayerEntity player = ((PlayerEntity) event.getSource().getEntity());
            ItemStack stack = player.getMainHandItem();
            OilUtils.getAppliedOil(stack).ifPresent(oil -> {
                if (oil instanceof IWeaponOil) {
                    event.setAmount(event.getAmount() + ((IWeaponOil) oil).onDamage(stack, event.getAmount(), ((IWeaponOil) oil), event.getEntityLiving(), player));
                }
            });
        }
        if (event.getSource() instanceof EntityDamageSource && !event.getSource().isBypassArmor()) {
            for (ItemStack armorStack : event.getEntityLiving().getArmorSlots()) {
                if(OilUtils.getAppliedOil(armorStack).map(oil -> {
                    if (oil == ModOils.EVASION.get()) {
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
