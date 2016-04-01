package de.teamlapen.lib.entity;

import de.teamlapen.lib.HelperRegistry;
import de.teamlapen.lib.VampLib;
import de.teamlapen.lib.lib.entity.IPlayerEventListener;
import de.teamlapen.lib.lib.network.ISyncable;
import de.teamlapen.lib.network.RequestPlayerUpdatePacket;
import de.teamlapen.lib.network.UpdateEntityPacket;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
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

    private final String[] listeners;

    public EntityEventHandler(String[] listeners) {
        this.listeners = listeners;
    }

    @SubscribeEvent
    public void onChangedDimension(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent event) {
        for (int i = 0; i < listeners.length; i++) {
            ((IPlayerEventListener) event.player.getExtendedProperties(listeners[i])).onChangedDimension(event.fromDim, event.toDim);
        }
    }

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (event.entity.worldObj.isRemote) {
            if ((event.entity instanceof EntityPlayerSP && HelperRegistry.getSyncablePlayerProperties().length > 0)) {
                VampLib.dispatcher.sendToServer(new RequestPlayerUpdatePacket());
            }
        }

        if (event.entity instanceof EntityPlayer) {
            for (int i = 0; i < listeners.length; i++) {
                ((IPlayerEventListener) event.entity.getExtendedProperties(listeners[i])).onJoinWorld();
            }
        }
    }

    @SubscribeEvent
    public void onLivingAttack(LivingAttackEvent event) {
        if (event.entity instanceof EntityPlayer) {
            for (int i = 0; i < listeners.length; i++) {
                ((IPlayerEventListener) event.entity.getExtendedProperties(listeners[i])).onEntityAttacked(event.source, event.ammount);
            }
        }
    }

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        if (event.entity instanceof EntityPlayer) {
            for (int i = 0; i < listeners.length; i++) {
                ((IPlayerEventListener) event.entity.getExtendedProperties(listeners[i])).onDeath(event.source);
            }
        }
    }

    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        if (event.entity instanceof EntityPlayer) {
            for (int i = 0; i < listeners.length; i++) {
                ((IPlayerEventListener) event.entity.getExtendedProperties(listeners[i])).onUpdate();
            }
        }
    }

    @SubscribeEvent
    public void onPlayerClone(PlayerEvent.Clone event) {
        if (event.wasDeath) {
            for (int i = 0; i < listeners.length; i++) {
                ((IPlayerEventListener) event.entityPlayer.getExtendedProperties(listeners[i])).onPlayerClone(event.original);
            }
        }

    }

    @SubscribeEvent
    public void onPlayerLoggedIn(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent event) {
        for (int i = 0; i < listeners.length; i++) {
            ((IPlayerEventListener) event.player.getExtendedProperties(listeners[i])).onPlayerLoggedIn();
        }
    }

    @SubscribeEvent
    public void onPlayerLoggedOut(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent event) {
        for (int i = 0; i < listeners.length; i++) {
            ((IPlayerEventListener) event.player.getExtendedProperties(listeners[i])).onPlayerLoggedOut();
        }
    }

    @SubscribeEvent
    public void onStartTracking(PlayerEvent.StartTracking event) {
        if ((event.target instanceof EntityLiving && HelperRegistry.getSyncableEntityProperties().length > 0) || event.target instanceof ISyncable || (event.target instanceof EntityPlayer && HelperRegistry.getSyncablePlayerProperties().length > 0)) {
            UpdateEntityPacket packet = UpdateEntityPacket.createJoinWorldPacket(event.target);
            VampLib.dispatcher.sendTo(packet, (EntityPlayerMP) event.entityPlayer);
        }
    }
}
