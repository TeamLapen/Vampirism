package de.teamlapen.lib.entity;

import de.teamlapen.lib.lib.entity.IPlayerEventListener;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
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
    public void onPlayerClone(PlayerEvent.Clone event) {
        for (int i = 0; i < listeners.length; i++) {
            ((IPlayerEventListener) event.entityPlayer.getExtendedProperties(listeners[i])).onPlayerClone(event.original);
        }
    }

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (event.entity instanceof EntityPlayer) {
            if (event.entity.worldObj.isRemote || event.entity instanceof EntityPlayerSP) {
                //TODO request update
            } else {
                for (int i = 0; i < listeners.length; i++) {
                    ((IPlayerEventListener) event.entity.getExtendedProperties(listeners[i])).onJoinWorld();
                }
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
    public void onChangedDimension(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent event) {
        for (int i = 0; i < listeners.length; i++) {
            ((IPlayerEventListener) event.player.getExtendedProperties(listeners[i])).onChangedDimension(event.fromDim, event.toDim);
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
}
