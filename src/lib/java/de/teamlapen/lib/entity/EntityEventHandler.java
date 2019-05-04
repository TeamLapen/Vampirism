package de.teamlapen.lib.entity;

import de.teamlapen.lib.HelperRegistry;
import de.teamlapen.lib.VampLib;
import de.teamlapen.lib.lib.entity.IPlayerEventListener;
import de.teamlapen.lib.lib.network.ISyncable;
import de.teamlapen.lib.network.RequestPlayerUpdatePacket;
import de.teamlapen.lib.network.UpdateEntityPacket;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.entity.ExtendedCreature;
import de.teamlapen.vampirism.world.villages.VampirismVillage;
import de.teamlapen.vampirism.world.villages.VampirismVillageHelper;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * Library's entity event handler to do stuff
 */
public class EntityEventHandler {

    private final Capability[] listeners;

    public EntityEventHandler(Capability[] listeners) {
        this.listeners = listeners;
    }

    @SubscribeEvent
    public void onChangedDimension(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent event) {
        for (Capability listener : listeners) {
            ((IPlayerEventListener) event.getPlayer().getCapability(listener, null)).onChangedDimension(event.getFrom(), event.getTo());
        }
    }

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (event.getEntity().getEntityWorld().isRemote) {
            if ((event.getEntity() instanceof EntityPlayerSP && HelperRegistry.getSyncablePlayerCaps().size() > 0)) {
                VampLib.dispatcher.sendToServer(new RequestPlayerUpdatePacket());
            }
        }

        if (event.getEntity() instanceof EntityPlayer) {
            for (Capability listener : listeners) {
                ((IPlayerEventListener) event.getEntity().getCapability(listener, null)).onJoinWorld();
            }
        }
        
        if (event.getEntity() instanceof EntityVillager && !event.getWorld().isRemote) {
            VampirismVillage village = VampirismVillageHelper.getNearestVillage(event.getWorld(), event.getEntity().getPosition(), 5);
            if (village != null && village.getControllingFaction() != null && village.getControllingFaction().equals(VReference.HUNTER_FACTION)) {
                ExtendedCreature.get((EntityCreature) event.getEntity()).setPoisonousBlood(true);
            }
        }
    }

    @SubscribeEvent
    public void onLivingAttack(LivingAttackEvent event) {
        if (event.getEntity() instanceof EntityPlayer) {
            for (Capability listener : listeners) {
                boolean cancel = ((IPlayerEventListener) event.getEntity().getCapability(listener, null)).onEntityAttacked(event.getSource(), event.getAmount());
                if (cancel) {
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof EntityPlayer) {
            for (Capability listener : listeners) {
                ((IPlayerEventListener) event.getEntity().getCapability(listener, null)).onDeath(event.getSource());
            }
        }
    }

    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntity() instanceof EntityPlayer) {
            for (Capability listener : listeners) {
                ((IPlayerEventListener) event.getEntity().getCapability(listener, null)).onUpdate();
            }
        }
    }

    @SubscribeEvent
    public void onPlayerClone(PlayerEvent.Clone event) {
        for (Capability listener : listeners) {
            ((IPlayerEventListener) event.getEntity().getCapability(listener, null)).onPlayerClone(event.getOriginal(), event.isWasDeath());
        }

    }

    @SubscribeEvent
    public void onPlayerLoggedIn(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent event) {
        for (Capability listener : listeners) {
            ((IPlayerEventListener) event.getPlayer().getCapability(listener, null)).onPlayerLoggedIn();
        }
    }

    @SubscribeEvent
    public void onPlayerLoggedOut(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent event) {
        for (Capability listener : listeners) {
            ((IPlayerEventListener) event.getPlayer().getCapability(listener, null)).onPlayerLoggedOut();
        }
    }

    @SubscribeEvent
    public void onPlayerUpdate(TickEvent.PlayerTickEvent event) {
        for (Capability listener : listeners) {
            ((IPlayerEventListener) event.player.getCapability(listener, null)).onUpdatePlayer(event.phase);
        }
    }

    @SubscribeEvent
    public void onStartTracking(PlayerEvent.StartTracking event) {
        if ((event.getTarget() instanceof EntityCreature && HelperRegistry.getSyncableEntityCaps().size() > 0) || event.getTarget() instanceof ISyncable || (event.getTarget() instanceof EntityPlayer && HelperRegistry.getSyncablePlayerCaps().size() > 0)) {
            UpdateEntityPacket packet = UpdateEntityPacket.createJoinWorldPacket(event.getTarget());
            VampLib.dispatcher.sendTo(packet, (EntityPlayerMP) event.getEntityPlayer());
        }
    }
}
