package de.teamlapen.vampirism.entity;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.difficulty.Difficulty;
import de.teamlapen.vampirism.api.difficulty.IAdjustableLevel;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.minions.IMinionLordWithSaveable;
import de.teamlapen.vampirism.api.items.IFactionSlayerItem;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.entity.hunter.HunterBaseEntity;
import de.teamlapen.vampirism.entity.vampire.VampireBaseEntity;
import de.teamlapen.vampirism.inventory.container.BloodPotionTableContainer;
import de.teamlapen.vampirism.items.VampirismVampireSword;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.util.DifficultyCalculator;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.block.BlockState;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Event handler for all entity related events
 */
public class ModEntityEventHandler {

    private final static Logger LOGGER = LogManager.getLogger(ModEntityEventHandler.class);
    private boolean skipAttackDamageOnce = false;
    private boolean warnAboutCreeper = true;

    @SubscribeEvent
    public void baseTick(LivingEvent.LivingUpdateEvent event) {
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

    @SubscribeEvent
    public void onAttachCapabilityEntity(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof CreatureEntity) {
            event.addCapability(REFERENCE.EXTENDED_CREATURE_KEY, ExtendedCreature.createNewCapability((CreatureEntity) event.getObject()));
        }
    }

    @SubscribeEvent
    public void onEntityAttacked(LivingAttackEvent event) {
        //Probably not a very "clean" solution, but the only one I found
        if (!skipAttackDamageOnce && "player".equals(event.getSource().getDamageType()) && event.getSource().getTrueSource() instanceof PlayerEntity) {
            ItemStack stack = ((PlayerEntity) event.getSource().getTrueSource()).getHeldItemMainhand();
            if (!stack.isEmpty() && stack.getItem() instanceof IFactionSlayerItem) {
                IFactionSlayerItem item = (IFactionSlayerItem) stack.getItem();
                IFaction faction = VampirismAPI.factionRegistry().getFaction(event.getEntity());

                if (faction != null && faction.equals(item.getSlayedFaction())) {
                    float amt = event.getAmount() * item.getDamageMultiplierForFaction(stack);
                    skipAttackDamageOnce = true;
                    boolean result = net.minecraftforge.common.ForgeHooks.onLivingAttack(event.getEntityLiving(), event.getSource(), amt);
                    skipAttackDamageOnce = false;
                    event.setCanceled(!result);
                }
            }
        }
    }

    @SubscribeEvent
    public void onEntityCheckSpawn(LivingSpawnEvent.CheckSpawn event) {
        BlockState blockState = event.getWorld().getBlockState(new BlockPos(event.getX() - 0.4F, event.getY(), event.getZ() - 0.4F).down());
        if (blockState.getBlock().equals(ModBlocks.castle_block_dark_stone) || !(event.getEntity().getClassification(false) == VReference.VAMPIRE_CREATURE_TYPE)) {
            event.setResult(Event.Result.DENY);
        } else if (blockState.getBlock().equals(ModBlocks.castle_stairs_dark_stone) || !(event.getEntity().getClassification(false) == VReference.VAMPIRE_CREATURE_TYPE)) {
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
                for (PrioritizedGoal t : ((CreeperEntity) event.getEntity()).targetSelector.goals) {//TODO private
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
        //------------------

        if (event.getEntity() instanceof IMinionLordWithSaveable) {
            ((IMinionLordWithSaveable) event.getEntity()).getSaveableMinionHandler().addLoadedMinions();
        }


        if (event.getEntity() instanceof VillagerEntity && !event.getWorld().isRemote) {//TODO 1.14 village
//            VampirismVillage village = VampirismVillageHelper.getNearestVillage(event.getWorld(), event.getEntity().getPosition(), 5);
//            if (village != null && village.getControllingFaction() != null && village.getControllingFaction().equals(VReference.HUNTER_FACTION)) {
//                ExtendedCreature.get((CreatureEntity) event.getEntity()).setPoisonousBlood(true);
//            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onLivingEquipmentChange(LivingEquipmentChangeEvent event) {
        if (event.getTo().getItem() instanceof VampirismVampireSword) {
            ((VampirismVampireSword) event.getTo().getItem()).updateTrainedCached(event.getTo(), event.getEntityLiving());
        }
    }

    @SubscribeEvent
    public void onEyeHeightSet(EntityEvent.EyeHeight event) {
        if (event.getEntity() instanceof VampireBaseEntity || event.getEntity() instanceof HunterBaseEntity)
            event.setNewHeight(event.getOldHeight() * 0.875f);
    }
}
