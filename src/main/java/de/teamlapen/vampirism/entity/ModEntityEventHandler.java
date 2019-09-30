package de.teamlapen.vampirism.entity;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.difficulty.Difficulty;
import de.teamlapen.vampirism.api.difficulty.IAdjustableLevel;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.items.IFactionSlayerItem;
import de.teamlapen.vampirism.blocks.CastleBricksBlock;
import de.teamlapen.vampirism.blocks.CastleSlabBlock;
import de.teamlapen.vampirism.blocks.CastleStairsBlock;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.entity.goals.GolemTargetVampireGoal;
import de.teamlapen.vampirism.entity.hunter.HunterBaseEntity;
import de.teamlapen.vampirism.entity.vampire.VampireBaseEntity;
import de.teamlapen.vampirism.inventory.container.BloodPotionTableContainer;
import de.teamlapen.vampirism.items.VampirismVampireSword;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.tileentity.TotemTileEntity;
import de.teamlapen.vampirism.util.DifficultyCalculator;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.structure.Structures;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.thread.EffectiveSide;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Event handler for all entity related events
 */
public class ModEntityEventHandler {

    private final static Logger LOGGER = LogManager.getLogger(ModEntityEventHandler.class);
    private boolean skipAttackDamageOnceServer = false;
    private boolean skipAttackDamageOnceClient = false;

    private boolean warnAboutCreeper = true;
    private boolean warnAboutZombie = true;

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
        BlockState blockState = event.getWorld().getBlockState(new BlockPos(event.getX() - 0.4F, event.getY(), event.getZ() - 0.4F).down());
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
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (!event.getWorld().isRemote && (event.getEntity() instanceof IAdjustableLevel)) {
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

        //Creeper AI changes for AvoidedByCreepers Skill
        if (!event.getWorld().isRemote && !Balance.vps.DISABLE_AVOIDED_BY_CREEPERS) {
            if (event.getEntity() instanceof CreeperEntity) {
                ((CreeperEntity) event.getEntity()).goalSelector.addGoal(3, new AvoidEntityGoal<>((CreeperEntity) event.getEntity(), PlayerEntity.class, 20, 1.1, 1.3, input -> input != null && VampirePlayer.get((PlayerEntity) input).getSpecialAttributes().avoided_by_creepers));

                Goal target = null;
                for (PrioritizedGoal t : ((CreeperEntity) event.getEntity()).targetSelector.goals) {
                    if (t.getGoal() instanceof NearestAttackableTargetGoal && t.getPriority() == 1) {
                        target = t.getGoal();
                    }
                }
                if (target != null) {
                    ((CreeperEntity) event.getEntity()).targetSelector.removeGoal(target);
                    ((CreeperEntity) event.getEntity()).targetSelector.addGoal(1, new NearestAttackableTargetGoal<PlayerEntity>((CreeperEntity) event.getEntity(), PlayerEntity.class, 10, true, false, input -> input != null && !VampirePlayer.get((PlayerEntity) input).getSpecialAttributes().avoided_by_creepers));
                } else {
                    if (warnAboutCreeper) {
                        LOGGER.warn("Could not replace creeper target task");
                        warnAboutCreeper = false;
                    }
                }
            }
        }

        //Zombie AI changes
        if (!event.getWorld().isRemote && Balance.general.ZOMBIE_IGNORE_VAMPIRE) {
            if (event.getEntity() instanceof ZombieEntity) {
                Goal target = null;
                for (PrioritizedGoal t : ((ZombieEntity) event.getEntity()).targetSelector.goals) {
                    if (t.getGoal() instanceof NearestAttackableTargetGoal && t.getPriority() == 2) {
                        target = t.getGoal();
                        break;
                    }
                }
                if (target != null) {
                    ((ZombieEntity) event.getEntity()).targetSelector.removeGoal(target);
                    ((ZombieEntity) event.getEntity()).targetSelector.addGoal(2, new NearestAttackableTargetGoal<>((ZombieEntity) event.getEntity(), PlayerEntity.class, 10, true, false, entity -> !Helper.isVampire(entity)));
                } else {
                    if (warnAboutZombie) {
                        LOGGER.warn("Could not replace zombie target task");
                        warnAboutZombie = false;
                    }
                }
                Goal villagerTarget = null;

                for (PrioritizedGoal t : ((ZombieEntity) event.getEntity()).targetSelector.goals) {
                    if (t.getGoal() instanceof NearestAttackableTargetGoal && t.getPriority() == 3 && AbstractVillagerEntity.class.equals(((NearestAttackableTargetGoal) t.getGoal()).targetClass)) {
                        villagerTarget = t.getGoal();
                        break;
                    }
                }
                if (villagerTarget != null) {
                    ((ZombieEntity) event.getEntity()).targetSelector.removeGoal(villagerTarget);
                    ((ZombieEntity) event.getEntity()).targetSelector.addGoal(3, new NearestAttackableTargetGoal<>((ZombieEntity) event.getEntity(), AbstractVillagerEntity.class, 10, false, false, entity -> !Helper.isVampire(entity)));
                } else {
                    if (warnAboutZombie) {
                        LOGGER.warn("Could not replace villager zombie target task");
                        warnAboutZombie = false;
                    }
                }
            }
        }

        if (!event.getWorld().isRemote && Balance.general.GOLEM_ATTACK_VAMPIRE) {
            if (event.getEntity() instanceof IronGolemEntity) {
                ((IronGolemEntity) event.getEntity()).targetSelector.addGoal(4, new GolemTargetVampireGoal((IronGolemEntity) event.getEntity()));
            }
        }
        //------------------

        if (event.getEntity() instanceof VillagerEntity && !event.getWorld().isRemote) {
            if(Structures.VILLAGE.isPositionInStructure(event.getWorld(),event.getEntity().getPosition())){
                StructureStart structure = Structures.VILLAGE.getStart(event.getWorld(),event.getEntity().getPosition(),false);
                if(!(structure == StructureStart.DUMMY)) {
                    BlockPos pos = TotemTileEntity.getTotemPosition(structure);
                    if(pos != null) {
                        TileEntity tileEntity = event.getWorld().getTileEntity(pos);
                        if(tileEntity instanceof TotemTileEntity) {
                            if (VReference.HUNTER_FACTION.equals(((TotemTileEntity) tileEntity).getControllingFaction())) {
                                ExtendedCreature.get((CreatureEntity) event.getEntity()).setPoisonousBlood(true);
                            }
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onEyeHeightSet(EntityEvent.EyeHeight event) {
        if (event.getEntity() instanceof VampireBaseEntity || event.getEntity() instanceof HunterBaseEntity)
            event.setNewHeight(event.getOldHeight() * 0.875f);
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
            ExtendedCreature.get((CreatureEntity) event.getEntity()).tick();
            event.getEntity().getEntityWorld().getProfiler().endSection();

        } else if (!event.getEntity().getEntityWorld().isRemote && event.getEntity() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) event.getEntity();
            if (player.openContainer instanceof BloodPotionTableContainer) {
                ((BloodPotionTableContainer) player.openContainer).tick();
            }
        }
    }
}
