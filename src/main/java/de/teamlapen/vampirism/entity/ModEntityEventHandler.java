package de.teamlapen.vampirism.entity;

import com.google.common.base.Predicate;
import de.teamlapen.vampirism.api.difficulty.Difficulty;
import de.teamlapen.vampirism.api.difficulty.IAdjustableLevel;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IFactionEntity;
import de.teamlapen.vampirism.api.entity.minions.IMinionLordWithSaveable;
import de.teamlapen.vampirism.api.items.IFactionSlayerItem;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.inventory.BloodPotionTableContainer;
import de.teamlapen.vampirism.util.DifficultyCalculator;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nullable;

/**
 * Event handler for all entity related events
 */
public class ModEntityEventHandler {

    private boolean skipAttackDamageOnce = false;

    @SubscribeEvent
    public void onAttachCapability(AttachCapabilitiesEvent.Entity event) {
        if (event.getEntity() instanceof EntityCreature) {
            event.addCapability(REFERENCE.EXTENDED_CREATURE_KEY, ExtendedCreature.createNewCapability((EntityCreature) event.getEntity()));
        }
    }

    @SubscribeEvent
    public void onEntityAttacked(LivingAttackEvent event) {
        //Probably not a very "clean" solution, but the only one I found
        if (!skipAttackDamageOnce && "player".equals(event.getSource().getDamageType()) && event.getSource().getEntity() instanceof EntityPlayer) {
            ItemStack stack = ((EntityPlayer) event.getSource().getEntity()).getHeldItemMainhand();
            if (stack != null && stack.getItem() instanceof IFactionSlayerItem) {
                IFactionSlayerItem item = (IFactionSlayerItem) stack.getItem();
                IFaction faction = null;
                if (event.getEntity() instanceof IFactionEntity) {
                    faction = ((IFactionEntity) event.getEntity()).getFaction();
                } else if (event.getEntity() instanceof EntityPlayer) {
                    faction = FactionPlayerHandler.get((EntityPlayer) event.getEntity()).getCurrentFaction();
                }
                if (faction != null && faction.equals(item.getSlayedFaction())) {
                    float amt = event.getAmount() * item.getDamageMultiplier(stack);
                    skipAttackDamageOnce = true;
                    boolean result = net.minecraftforge.common.ForgeHooks.onLivingAttack(event.getEntityLiving(), event.getSource(), amt);
                    skipAttackDamageOnce = false;
                    event.setCanceled(!result);
                }
            }
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
            }
        }
        if (event.getEntity() instanceof EntityCreeper) {
            ((EntityCreeper) event.getEntity()).tasks.addTask(3, new EntityAIAvoidEntity<>((EntityCreeper) event.getEntity(), EntityPlayer.class, new Predicate<EntityPlayer>() {
                @Override
                public boolean apply(@Nullable EntityPlayer input) {
                    return VampirePlayer.get(input).getSpecialAttributes().avoided_by_creepers;
                }
            }, 6, 1, 1.2));
        }
        if (event.getEntity() instanceof IMinionLordWithSaveable) {
            ((IMinionLordWithSaveable) event.getEntity()).getSaveableMinionHandler().addLoadedMinions();
        }
    }

    @SubscribeEvent
    public void onEntityUpdate(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntity() instanceof EntityCreature) {
            ExtendedCreature.get((EntityCreature) event.getEntity()).onUpdate();
        }
        if (!event.getEntity().worldObj.isRemote && event.getEntity() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntity();
            if (player.openContainer instanceof BloodPotionTableContainer) {
                ((BloodPotionTableContainer) player.openContainer).tick();
            }
        }
    }
}
