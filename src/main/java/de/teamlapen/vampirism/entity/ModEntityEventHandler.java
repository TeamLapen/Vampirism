package de.teamlapen.vampirism.entity;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.difficulty.Difficulty;
import de.teamlapen.vampirism.api.difficulty.IAdjustableLevel;
import de.teamlapen.vampirism.api.entity.IExtendedCreatureVampirism;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IFactionEntity;
import de.teamlapen.vampirism.api.items.IFactionSlayerItem;
import de.teamlapen.vampirism.api.items.IItemWithTier;
import de.teamlapen.vampirism.blockentity.TotemBlockEntity;
import de.teamlapen.vampirism.blockentity.TotemHelper;
import de.teamlapen.vampirism.blocks.CastleBricksBlock;
import de.teamlapen.vampirism.blocks.CastleSlabBlock;
import de.teamlapen.vampirism.blocks.CastleStairsBlock;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.entity.goals.GolemTargetNonVillageFactionGoal;
import de.teamlapen.vampirism.entity.hunter.HunterBaseEntity;
import de.teamlapen.vampirism.entity.minion.MinionEntity;
import de.teamlapen.vampirism.entity.vampire.VampireBaseEntity;
import de.teamlapen.vampirism.items.VampirismVampireSword;
import de.teamlapen.vampirism.player.VampirismPlayerAttributes;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.util.DifficultyCalculator;
import de.teamlapen.vampirism.util.Helper;
import it.unimi.dsi.fastutil.objects.Object2BooleanArrayMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.util.thread.EffectiveSide;
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

    public static <T extends Mob, S extends LivingEntity, Q extends NearestAttackableTargetGoal<S>> void makeVampireFriendly(String name, T e, Class<Q> targetClass, Class<S> targetEntityClass, int attackPriority, BiFunction<T, Predicate<LivingEntity>, Q> replacement, Predicate<EntityType<? extends T>> typeCheck) {
        Goal target = null;
        for (WrappedGoal t : e.targetSelector.availableGoals) {
            Goal g = t.getGoal();
            if (targetClass.equals(g.getClass()) && t.getPriority() == attackPriority && targetEntityClass.equals(((NearestAttackableTargetGoal<?>) g).targetType)) {
                target = g;
                break;
            }
        }
        if (target != null) {
            e.targetSelector.removeGoal(target);
            @SuppressWarnings("unchecked")
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
    private boolean skipAttackDamageOnceServer = false;
    private boolean skipAttackDamageOnceClient = false;
    private boolean warnAboutGolem = true;

    @SubscribeEvent
    public void onAttachCapabilityEntity(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof PathfinderMob) {
            event.addCapability(REFERENCE.EXTENDED_CREATURE_KEY, ExtendedCreature.createNewCapability((PathfinderMob) event.getObject()));
        }
    }

    @SubscribeEvent
    public void onEntityAttacked(LivingAttackEvent event) {
        //Probably not a very "clean" solution, but the only one I found
        boolean client = EffectiveSide.get().isClient();
        if (!(client ? skipAttackDamageOnceClient : skipAttackDamageOnceServer) && "player".equals(event.getSource().getMsgId()) && event.getSource().getEntity() instanceof Player) {
            ItemStack stack = ((Player) event.getSource().getEntity()).getMainHandItem();
            if (!stack.isEmpty() && stack.getItem() instanceof IFactionSlayerItem item) {
                IFaction<?> faction = VampirismAPI.factionRegistry().getFaction(event.getEntity());

                if (faction != null && faction.equals(item.getSlayedFaction())) {
                    float amt = event.getAmount() * item.getDamageMultiplierForFaction(stack);
                    if (client) {
                        skipAttackDamageOnceClient = true;
                    } else {
                        skipAttackDamageOnceServer = true;
                    }
                    boolean result = event.getEntity().hurt(event.getSource(), amt);
                    if (client) {
                        skipAttackDamageOnceClient = false;
                    } else {
                        skipAttackDamageOnceServer = false;
                    }
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public void onEntityCheckSpawn(LivingSpawnEvent.CheckSpawn event) {
        BlockPos pos = new BlockPos(event.getX() - 0.4F, event.getY(), event.getZ() - 0.4F).below();
        if (!event.getLevel().hasChunkAt(pos)) return;
        BlockState blockState = event.getLevel().getBlockState(pos);
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
        if (event.getSlot().getType() == EquipmentSlot.Type.ARMOR && event.getEntity() instanceof Player) {
            VampirePlayer.getOpt((Player) event.getEntity()).ifPresent(VampirePlayer::requestNaturalArmorUpdate);
        }
    }

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinLevelEvent event) {
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
                    ((Creeper) event.getEntity()).goalSelector.addGoal(3, new AvoidEntityGoal<>((Creeper) event.getEntity(), Player.class, 20, 1.1, 1.3, Helper::isVampire));
                    //noinspection unchecked
                    makeVampireFriendly("creeper", (Creeper) event.getEntity(), NearestAttackableTargetGoal.class, Player.class, 1, (entity, predicate) -> new NearestAttackableTargetGoal<>(entity, Player.class, 10, true, false, predicate), type -> type == EntityType.CREEPER);

                    return;
                }
            }

            //Zombie AI changes
            if (VampirismConfig.BALANCE.zombieIgnoreVampire.get()) {
                if (event.getEntity() instanceof Zombie) {
                    //noinspection unchecked
                    makeVampireFriendly("zombie", (Zombie) event.getEntity(), NearestAttackableTargetGoal.class, Player.class, 2, (entity, predicate) -> entity instanceof Drowned ? new NearestAttackableTargetGoal<>(entity, Player.class, 10, true, false, predicate.and(((Drowned) entity)::okTarget)) : new NearestAttackableTargetGoal<>(entity, Player.class, 10, true, false, predicate), type -> type == EntityType.ZOMBIE || type == EntityType.HUSK || type == EntityType.ZOMBIE_VILLAGER || type == EntityType.DROWNED);
                    //Also replace attack villager task for entities that have it
                    //noinspection unchecked
                    makeVampireFriendly("villager zombie", (Zombie) event.getEntity(), NearestAttackableTargetGoal.class, AbstractVillager.class, 3, (entity, predicate) -> new NearestAttackableTargetGoal<>(entity, AbstractVillager.class, 10, true, false, predicate), type -> type == EntityType.ZOMBIE || type == EntityType.HUSK || type == EntityType.ZOMBIE_VILLAGER || type == EntityType.DROWNED);
                    return;
                }
            }

            if (VampirismConfig.BALANCE.skeletonIgnoreVampire.get()) {
                if (event.getEntity() instanceof Skeleton || event.getEntity() instanceof Stray) {
                    //noinspection unchecked
                    makeVampireFriendly("skeleton", (AbstractSkeleton) event.getEntity(), NearestAttackableTargetGoal.class, Player.class, 2, (entity, predicate) -> new NearestAttackableTargetGoal<>(entity, Player.class, 10, true, false, predicate), type -> type == EntityType.SKELETON);
                }
            }

            if (event.getEntity() instanceof IronGolem) {
                ((IronGolem) event.getEntity()).targetSelector.addGoal(4, new GolemTargetNonVillageFactionGoal((IronGolem) event.getEntity()));

                Goal mobTarget = null;

                for (WrappedGoal t : ((IronGolem) event.getEntity()).targetSelector.availableGoals) {
                    if (t.getGoal() instanceof NearestAttackableTargetGoal && t.getPriority() == 3 && Mob.class.equals(((NearestAttackableTargetGoal<?>) t.getGoal()).targetType)) {
                        mobTarget = t.getGoal();
                        break;
                    }
                }
                if (mobTarget != null) {
                    ((IronGolem) event.getEntity()).targetSelector.removeGoal(mobTarget);
                    ((IronGolem) event.getEntity()).targetSelector.addGoal(3, new NearestAttackableTargetGoal<>((IronGolem) event.getEntity(), Mob.class, 5, false, false, entity -> entity instanceof Enemy && !(entity instanceof IFactionEntity) && !(entity instanceof Creeper)));
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
    public void onEntityLootingEvent(LootingLevelEvent event) {
        if (event.getDamageSource() != null && event.getDamageSource().getEntity() instanceof Player) {
            @Nullable
            IItemWithTier.TIER hunterCoatTier = VampirismPlayerAttributes.get((Player) event.getDamageSource().getEntity()).getHuntSpecial().fullHunterCoat;
            if (hunterCoatTier == IItemWithTier.TIER.ENHANCED || hunterCoatTier == IItemWithTier.TIER.ULTIMATE) {
                event.setLootingLevel(Math.min(event.getLootingLevel() + 1, 3));
            }
        }
    }

    @SubscribeEvent
    public void onEntityVisibilityCheck(LivingEvent.LivingVisibilityEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (VampirismPlayerAttributes.get(player).getHuntSpecial().isDisguised()) {
                event.modifyVisibility((VampirismPlayerAttributes.get((Player) event.getEntity()).getHuntSpecial().fullHunterCoat != null ? 0.5 : 1) * VampirismConfig.BALANCE.haDisguiseVisibilityMod.get());
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
            ((VampirismVampireSword) event.getTo().getItem()).updateTrainedCached(event.getTo(), event.getEntity());
        }
    }

    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingTickEvent event) {
        if (event.getEntity() instanceof PathfinderMob) {
            event.getEntity().getCommandSenderWorld().getProfiler().push("vampirism_extended_creature");
            ExtendedCreature.getSafe(event.getEntity()).ifPresent(IExtendedCreatureVampirism::tick);
            event.getEntity().getCommandSenderWorld().getProfiler().pop();

        }
    }
}
