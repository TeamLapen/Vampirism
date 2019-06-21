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
import de.teamlapen.vampirism.inventory.BloodPotionTableContainer;
import de.teamlapen.vampirism.items.VampirismVampireSword;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.util.DifficultyCalculator;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.AttachCapabilitiesEvent;
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
    public void onAttachCapabilityEntity(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof EntityCreature) {
            event.addCapability(REFERENCE.EXTENDED_CREATURE_KEY, ExtendedCreature.createNewCapability((EntityCreature) event.getObject()));
        }
    }

    @SubscribeEvent
    public void onEntityAttacked(LivingAttackEvent event) {
        //Probably not a very "clean" solution, but the only one I found
        if (!skipAttackDamageOnce && "player".equals(event.getSource().getDamageType()) && event.getSource().getTrueSource() instanceof EntityPlayer) {
            ItemStack stack = ((EntityPlayer) event.getSource().getTrueSource()).getHeldItemMainhand();
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
        IBlockState blockState = event.getWorld().getBlockState(new BlockPos(event.getX() - 0.4F, event.getY(), event.getZ() - 0.4F).down());
        if (blockState.getBlock().equals(ModBlocks.castle_block_dark_stone) || !event.getEntity().isCreatureType(VReference.VAMPIRE_CREATURE_TYPE, false)) {
            event.setResult(Event.Result.DENY);
        } else if (blockState.getBlock().equals(ModBlocks.castle_stairs_dark_stone) || !event.getEntity().isCreatureType(VReference.VAMPIRE_CREATURE_TYPE, false)) {
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
                if (entity instanceof EntityCreature) {
                    ((EntityCreature) entity).setHealth(((EntityCreature) entity).getMaxHealth());
                }
            }
        }

        //Creeper AI changes for AvoidedByCreepers Skill
        if (!event.getWorld().isRemote && !Balance.vps.DISABLE_AVOIDED_BY_CREEPERS) {
            if (event.getEntity() instanceof EntityCreeper) {
                ((EntityCreeper) event.getEntity()).tasks.addTask(3, new EntityAIAvoidEntity<>((EntityCreeper) event.getEntity(), EntityPlayer.class, 20, 1.1, 1.3, input -> input != null && VampirePlayer.get((EntityPlayer) input).getSpecialAttributes().avoided_by_creepers));

                EntityAIBase target = null;
                for (EntityAITasks.EntityAITaskEntry t : ((EntityCreeper) event.getEntity()).targetTasks.taskEntries) {
                    if (t.action instanceof EntityAINearestAttackableTarget && t.priority == 1) {
                        target = t.action;
                    }
                }
                if (target != null) {
                    ((EntityCreeper) event.getEntity()).targetTasks.removeTask(target);
                    ((EntityCreeper) event.getEntity()).targetTasks.addTask(1, new EntityAINearestAttackableTarget<>((EntityCreeper) event.getEntity(), EntityPlayer.class, 10, true, false, input -> input != null && !VampirePlayer.get(input).getSpecialAttributes().avoided_by_creepers));
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
    }

    @SubscribeEvent
    public void baseTick(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntity() instanceof EntityCreature) {
            event.getEntity().getEntityWorld().profiler.startSection("vampirism_extended_creature");
            ExtendedCreature.get((EntityCreature) event.getEntity()).tick();
            event.getEntity().getEntityWorld().profiler.endSection();

        } else if (!event.getEntity().getEntityWorld().isRemote && event.getEntity() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntity();
            if (player.openContainer instanceof BloodPotionTableContainer) {
                ((BloodPotionTableContainer) player.openContainer).tick();
            }
        }

    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onLivingEquipmentChange(LivingEquipmentChangeEvent event) {
        if (event.getTo().getItem() instanceof VampirismVampireSword) {
            ((VampirismVampireSword) event.getTo().getItem()).updateTrainedCached(event.getTo(), event.getEntityLiving());
        }
    }
}
