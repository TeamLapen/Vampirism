package de.teamlapen.lib.entity;

import de.teamlapen.lib.HelperRegistry;
import de.teamlapen.lib.VampLib;
import de.teamlapen.lib.lib.entity.IPlayerEventListener;
import de.teamlapen.lib.lib.network.ISyncable;
import de.teamlapen.lib.network.RequestPlayerUpdatePacket;
import de.teamlapen.lib.network.UpdateEntityPacket;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

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
        for (int i = 0; i < listeners.length; i++) {
            ((IPlayerEventListener) event.player.getCapability(listeners[i], null)).onChangedDimension(event.fromDim, event.toDim);
        }
    }

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (event.entity.worldObj.isRemote) {
            if ((event.entity instanceof EntityPlayerSP && HelperRegistry.getSyncablePlayerCaps().size() > 0)) {
                VampLib.dispatcher.sendToServer(new RequestPlayerUpdatePacket());
            }
        }

        if (event.entity instanceof EntityPlayer) {
            for (int i = 0; i < listeners.length; i++) {
                ((IPlayerEventListener) event.entity.getCapability(listeners[i], null)).onJoinWorld();
            }
        }
    }

    @SubscribeEvent
    public void onLivingAttack(LivingAttackEvent event) {
        if (event.entity instanceof EntityPlayer) {
            for (int i = 0; i < listeners.length; i++) {
                ((IPlayerEventListener) event.entity.getCapability(listeners[i], null)).onEntityAttacked(event.source, event.ammount);
            }
        }
    }

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        if (event.entity instanceof EntityPlayer) {
            for (int i = 0; i < listeners.length; i++) {
                ((IPlayerEventListener) event.entity.getCapability(listeners[i], null)).onDeath(event.source);
            }
        }
    }

    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        if (event.entity instanceof EntityPlayer) {
            for (int i = 0; i < listeners.length; i++) {
                ((IPlayerEventListener) event.entity.getCapability(listeners[i], null)).onUpdate();
            }
        }
    }

    @SubscribeEvent
    public void onPlayerClone(PlayerEvent.Clone event) {
        if (event.wasDeath) {
            for (int i = 0; i < listeners.length; i++) {
                ((IPlayerEventListener) event.entity.getCapability(listeners[i], null)).onPlayerClone(event.original);
            }
        }

    }

    @SubscribeEvent
    public void onPlayerLoggedIn(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent event) {
        for (int i = 0; i < listeners.length; i++) {
            ((IPlayerEventListener) event.player.getCapability(listeners[i], null)).onPlayerLoggedIn();
        }
    }

    @SubscribeEvent
    public void onPlayerLoggedOut(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent event) {
        for (int i = 0; i < listeners.length; i++) {
            ((IPlayerEventListener) event.player.getCapability(listeners[i], null)).onPlayerLoggedOut();
        }
    }

    @SubscribeEvent
    public void onStartTracking(PlayerEvent.StartTracking event) {
        if ((event.target instanceof EntityCreature && HelperRegistry.getSyncableEntityCaps().size() > 0) || event.target instanceof ISyncable || (event.target instanceof EntityPlayer && HelperRegistry.getSyncablePlayerCaps().size() > 0)) {
            UpdateEntityPacket packet = UpdateEntityPacket.createJoinWorldPacket(event.target);
            VampLib.dispatcher.sendTo(packet, (EntityPlayerMP) event.entityPlayer);
        }
    }
}
