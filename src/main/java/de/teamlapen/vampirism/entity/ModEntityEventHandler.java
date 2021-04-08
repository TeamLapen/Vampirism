package de.teamlapen.vampirism.entity;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.difficulty.Difficulty;
import de.teamlapen.vampirism.api.difficulty.IAdjustableLevel;
import de.teamlapen.vampirism.api.entity.IExtendedCreatureVampirism;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IFactionEntity;
import de.teamlapen.vampirism.api.items.IFactionSlayerItem;
import de.teamlapen.vampirism.api.items.IItemWithTier;
import de.teamlapen.vampirism.blocks.CastleBricksBlock;
import de.teamlapen.vampirism.blocks.CastleSlabBlock;
import de.teamlapen.vampirism.blocks.CastleStairsBlock;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.entity.goals.GolemTargetNonVillageFactionGoal;
import de.teamlapen.vampirism.entity.hunter.HunterBaseEntity;
import de.teamlapen.vampirism.entity.vampire.VampireBaseEntity;
import de.teamlapen.vampirism.items.HunterCoatItem;
import de.teamlapen.vampirism.items.VampirismVampireSword;
import de.teamlapen.vampirism.player.hunter.HunterPlayer;
import de.teamlapen.vampirism.player.hunter.HunterPlayerSpecialAttribute;
import de.teamlapen.vampirism.tileentity.TotemHelper;
import de.teamlapen.vampirism.tileentity.TotemTileEntity;
import de.teamlapen.vampirism.util.DifficultyCalculator;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.REFERENCE;
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
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.thread.EffectiveSide;
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
    private boolean skipAttackDamageOnceServer = false;
    private boolean skipAttackDamageOnceClient = false;

    private boolean warnAboutCreeper = true;
    private boolean warnAboutZombie = true;
    private boolean warnAboutGolem = true;
    private final Set<ResourceLocation> unknownZombies = new HashSet<>();

    @SubscribeEvent
    public void onAttachCapabilityEntity(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof CreatureEntity) {
            event.addCapability(REFERENCE.EXTENDED_CREATURE_KEY, ExtendedCreature.createNewCapability((CreatureEntity) event.getObject()));
        }
    }

    @SubscribeEvent
    public void onEntityAttacked(LivingAttackEvent event) {
        //Probably not a very "clean" solution, but the only one I found
        boolean client = EffectiveSide.get().isClient();
        if (!(client ? skipAttackDamageOnceClient : skipAttackDamageOnceServer) && "player".equals(event.getSource().getDamageType()) && event.getSource().getTrueSource() instanceof PlayerEntity) {
            ItemStack stack = ((PlayerEntity) event.getSource().getTrueSource()).getHeldItemMainhand();
            if (!stack.isEmpty() && stack.getItem() instanceof IFactionSlayerItem) {
                IFactionSlayerItem item = (IFactionSlayerItem) stack.getItem();
                IFaction faction = VampirismAPI.factionRegistry().getFaction(event.getEntity());

                if (faction != null && faction.equals(item.getSlayedFaction())) {
                    float amt = event.getAmount() * item.getDamageMultiplierForFaction(stack);
                    if (client) {
                        skipAttackDamageOnceClient = true;
                    } else {
                        skipAttackDamageOnceServer = true;
                    }
                    boolean result = event.getEntityLiving().attackEntityFrom(event.getSource(), amt);
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
        BlockPos pos = new BlockPos(event.getX() - 0.4F, event.getY(), event.getZ() - 0.4F).down();
        if (!event.getWorld().isBlockLoaded(pos)) return;
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

    private static final Predicate<LivingEntity> nonVampireCheck = entity -> !Helper.isVampire(entity);
    private static final Object2BooleanMap<String> entityAIReplacementWarnMap = new Object2BooleanArrayMap<>();

    public static <T extends MobEntity, S extends LivingEntity, Q extends NearestAttackableTargetGoal<S>> void makeVampireFriendly(String name, T e, Class<Q> targetClass, Class<S> targetEntityClass, int attackPriority, BiFunction<T, Predicate<LivingEntity>, Q> replacement, Predicate<EntityType<? extends T>> typeCheck) {
        Goal target = null;
        for (PrioritizedGoal t : e.targetSelector.goals) {
            Goal g = t.getGoal();
            if (targetClass.equals(g.getClass()) && t.getPriority() == attackPriority && targetEntityClass.equals(((NearestAttackableTargetGoal<?>) g).targetClass)) {
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
                LOGGER.warn("Could not replace {} attack target task for {}", name, e.getType().getName());
                entityAIReplacementWarnMap.put(name, false);
            }

        }
    }

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (!event.getWorld().isRemote()) {
            if (event.getEntity() instanceof IAdjustableLevel) {
                IAdjustableLevel entity = (IAdjustableLevel) event.getEntity();
                if (entity.getLevel() == -1) {
                    Difficulty d = DifficultyCalculator.findDifficultyForPos(event.getWorld(), event.getEntity().getPosition(), 30);
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
                    makeVampireFriendly("zombie", (ZombieEntity) event.getEntity(), NearestAttackableTargetGoal.class, PlayerEntity.class, 2, (entity, predicate) -> entity instanceof DrownedEntity ? new NearestAttackableTargetGoal<>(entity, PlayerEntity.class, 10, true, false, predicate.and(((DrownedEntity) entity)::shouldAttack)) : new NearestAttackableTargetGoal<>(entity, PlayerEntity.class, 10, true, false, predicate), type -> type == EntityType.ZOMBIE || type == EntityType.HUSK || type == EntityType.ZOMBIE_VILLAGER || type == EntityType.DROWNED);
                    //Also replace attack villager task for entities that have it
                    makeVampireFriendly("villager zombie", (ZombieEntity) event.getEntity(), NearestAttackableTargetGoal.class, AbstractVillagerEntity.class, 3, (entity, predicate) -> new NearestAttackableTargetGoal<>(entity, AbstractVillagerEntity.class, 10, true, false, predicate), type -> type == EntityType.ZOMBIE || type == EntityType.HUSK || type == EntityType.ZOMBIE_VILLAGER || type == EntityType.DROWNED);
                    return;
                }
            }

            if (VampirismConfig.BALANCE.skeletonIgnoreVampire.get()) {
                if (event.getEntity() instanceof SkeletonEntity) {
                    makeVampireFriendly("skeleton", (SkeletonEntity) event.getEntity(), NearestAttackableTargetGoal.class, PlayerEntity.class, 2, (entity, predicate) -> new NearestAttackableTargetGoal<PlayerEntity>(entity, PlayerEntity.class, 10, true, false, predicate), type -> type == EntityType.SKELETON);
                }
            }

            if (event.getEntity() instanceof IronGolemEntity) {
                ((IronGolemEntity) event.getEntity()).targetSelector.addGoal(4, new GolemTargetNonVillageFactionGoal((IronGolemEntity) event.getEntity()));

                Goal mobTarget = null;

                for (PrioritizedGoal t : ((IronGolemEntity) event.getEntity()).targetSelector.goals) {
                    if (t.getGoal() instanceof NearestAttackableTargetGoal && t.getPriority() == 3 && MobEntity.class.equals(((NearestAttackableTargetGoal<?>) t.getGoal()).targetClass)) {
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
                Optional<TotemTileEntity> tile = TotemHelper.getTotemNearPos(((ServerWorld) event.getWorld()), event.getEntity().getPosition(), true);
                if (tile.filter(t -> VReference.HUNTER_FACTION.equals(t.getControllingFaction())).isPresent()) {
                    ExtendedCreature.getSafe(event.getEntity()).ifPresent(e -> e.setPoisonousBlood(true));
                }
                return;
            }
        }
    }

    @SubscribeEvent
    public void onEyeHeightSet(EntityEvent.Size event) {
        if (event.getEntity() instanceof VampireBaseEntity || event.getEntity() instanceof HunterBaseEntity)
            event.setNewEyeHeight(event.getOldEyeHeight() * 0.875f);
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
            event.getEntity().getEntityWorld().getProfiler().startSection("vampirism_extended_creature");
            ExtendedCreature.getSafe(event.getEntity()).ifPresent(IExtendedCreatureVampirism::tick);
            event.getEntity().getEntityWorld().getProfiler().endSection();

        }
    }


    @SubscribeEvent
    public void onEntityVisibilityCheck(LivingEvent.LivingVisibilityEvent event){
        if (event.getEntity() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) event.getEntity();
            if (HunterPlayer.getOpt(player).map(HunterPlayer::getSpecialAttributes).map(HunterPlayerSpecialAttribute::isDisguised).orElse(false)) {
                event.modifyVisibility((HunterCoatItem.isFullyEquipped((PlayerEntity) event.getEntity())!=null?0.5:1)*VampirismConfig.BALANCE.haDisguiseVisibilityMod.get());
            }
        }
    }

    @SubscribeEvent
    public void onEntityLootingEvent(LootingLevelEvent event){
        if(event.getDamageSource().getTrueSource() instanceof PlayerEntity){
            @Nullable
            IItemWithTier.TIER hunterCoatTier = HunterCoatItem.isFullyEquipped((PlayerEntity) event.getDamageSource().getTrueSource());
            if(hunterCoatTier== IItemWithTier.TIER.ENHANCED || hunterCoatTier== IItemWithTier.TIER.ULTIMATE){
                event.setLootingLevel(Math.min(event.getLootingLevel()+1 , 3));
            }
        }
    }
}
