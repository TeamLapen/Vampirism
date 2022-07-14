package de.teamlapen.lib.entity;

import de.teamlapen.lib.HelperRegistry;
import de.teamlapen.lib.VampLib;
import de.teamlapen.lib.lib.entity.IPlayerEventListener;
import de.teamlapen.lib.lib.network.ISyncable;
import de.teamlapen.lib.network.UpdateEntityPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Library's entity event handler to do stuff
 */
public class EntityEventHandler {

    private final Capability<IPlayerEventListener>[] listeners;

    public EntityEventHandler(Capability<IPlayerEventListener>[] listeners) {
        this.listeners = listeners;
    }

    @SubscribeEvent
    public void onChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        for (Capability<IPlayerEventListener> listener : listeners) {
            event.getEntity().getCapability(listener, null).ifPresent(cap -> cap.onChangedDimension(event.getFrom(), event.getTo()));
        }
    }

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof Player) {
            for (Capability<IPlayerEventListener> listener : listeners) {
                (event.getEntity().getCapability(listener, null)).ifPresent(IPlayerEventListener::onJoinWorld);
            }
        }

    }

    @SubscribeEvent
    public void onLivingAttack(LivingAttackEvent event) {
        if (event.getEntity() instanceof Player) {
            for (Capability<IPlayerEventListener> listener : listeners) {
                boolean cancel = event.getEntity().getCapability(listener, null).map(cap -> cap.onEntityAttacked(event.getSource(), event.getAmount())).orElse(false);
                if (cancel) {
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof Player) {
            for (Capability<IPlayerEventListener> listener : listeners) {
                (event.getEntity().getCapability(listener, null)).ifPresent(cap -> cap.onDeath(event.getSource()));
            }
        }
        if (event.getSource().getEntity() instanceof Player) {
            for (Capability<IPlayerEventListener> listener : listeners) {
                (event.getSource().getEntity().getCapability(listener, null)).ifPresent(cap -> cap.onEntityKilled(event.getEntity(), event.getSource()));
            }
        }
    }

    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingTickEvent event) {
        if (event.getEntity() instanceof Player) {
            for (Capability<IPlayerEventListener> listener : listeners) {
                event.getEntity().getCapability(listener, null).ifPresent(IPlayerEventListener::onUpdate);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onPlayerClone(PlayerEvent.Clone event) {
        for (Capability<IPlayerEventListener> listener : listeners) {
            (event.getEntity().getCapability(listener, null)).ifPresent(cap -> cap.onPlayerClone(event.getOriginal(), event.isWasDeath()));
        }

    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        for (Capability<IPlayerEventListener> listener : listeners) {
            (event.getEntity().getCapability(listener, null)).ifPresent(IPlayerEventListener::onPlayerLoggedIn);
        }
    }

    @SubscribeEvent
    public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        for (Capability<IPlayerEventListener> listener : listeners) {
            (event.getEntity().getCapability(listener, null)).ifPresent(IPlayerEventListener::onPlayerLoggedOut);
        }
    }

    @SubscribeEvent
    public void onPlayerUpdate(TickEvent.PlayerTickEvent event) {
        for (Capability<IPlayerEventListener> listener : listeners) {
            (event.player.getCapability(listener, null)).ifPresent(cap -> cap.onUpdatePlayer(event.phase));
        }
    }

    @SubscribeEvent
    public void onStartTracking(PlayerEvent.StartTracking event) {
        if ((event.getTarget() instanceof PathfinderMob && HelperRegistry.getSyncableEntityCaps().size() > 0) || event.getTarget() instanceof ISyncable || (event.getTarget() instanceof Player && HelperRegistry.getSyncablePlayerCaps().size() > 0)) {
            UpdateEntityPacket packet = UpdateEntityPacket.createJoinWorldPacket(event.getTarget());
            if (packet != null) {
                VampLib.dispatcher.sendTo(packet, (ServerPlayer) event.getEntity());
            }
        }
    }
}
