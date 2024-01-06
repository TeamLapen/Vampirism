package de.teamlapen.lib.entity;

import de.teamlapen.lib.HelperRegistry;
import de.teamlapen.lib.lib.entity.IPlayerEventListener;
import de.teamlapen.lib.lib.network.ISyncable;
import de.teamlapen.lib.network.ClientboundUpdateEntityPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.event.TickEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingAttackEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Library's entity event handler to do stuff
 */
public class EntityEventHandler {

    private final AttachmentType<IPlayerEventListener>[] listeners;

    public EntityEventHandler(AttachmentType<IPlayerEventListener>[] listeners) {
        this.listeners = listeners;
    }

    @SubscribeEvent
    public void onChangedDimension(PlayerEvent.@NotNull PlayerChangedDimensionEvent event) {
        for (AttachmentType<IPlayerEventListener> listener : listeners) {
            Optional.ofNullable(event.getEntity().getData(listener)).ifPresent(cap -> cap.onChangedDimension(event.getFrom(), event.getTo()));
        }
    }

    @SubscribeEvent
    public void onEntityJoinWorld(@NotNull EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof Player) {
            for (AttachmentType<IPlayerEventListener> listener : listeners) {
                Optional.ofNullable(event.getEntity().getData(listener)).ifPresent(IPlayerEventListener::onJoinWorld);
            }
        }

    }

    @SubscribeEvent
    public void onLivingAttack(@NotNull LivingAttackEvent event) {
        if (event.getEntity() instanceof Player) {
            for (AttachmentType<IPlayerEventListener> listener : listeners) {
                boolean cancel = Optional.ofNullable(event.getEntity().getData(listener)).map(cap -> cap.onEntityAttacked(event.getSource(), event.getAmount())).orElse(false);
                if (cancel) {
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public void onLivingDeath(@NotNull LivingDeathEvent event) {
        if (event.getEntity() instanceof Player) {
            for (AttachmentType<IPlayerEventListener> listener : listeners) {
                Optional.ofNullable(event.getEntity().getData(listener)).ifPresent(cap -> cap.onDeath(event.getSource()));
            }
        }
        if (event.getSource().getEntity() instanceof Player) {
            for (AttachmentType<IPlayerEventListener> listener : listeners) {
                Optional.ofNullable(event.getSource().getEntity().getData(listener)).ifPresent(cap -> cap.onEntityKilled(event.getEntity(), event.getSource()));
            }
        }
    }

    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.@NotNull LivingTickEvent event) {
        if (event.getEntity() instanceof Player) {
            for (AttachmentType<IPlayerEventListener> listener : listeners) {
                Optional.ofNullable(event.getEntity().getData(listener)).ifPresent(IPlayerEventListener::onUpdate);
            }
        }
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.@NotNull PlayerLoggedInEvent event) {
        for (AttachmentType<IPlayerEventListener> listener : listeners) {
            Optional.ofNullable(event.getEntity().getData(listener)).ifPresent(IPlayerEventListener::onPlayerLoggedIn);
        }
    }

    @SubscribeEvent
    public void onPlayerLoggedOut(PlayerEvent.@NotNull PlayerLoggedOutEvent event) {
        for (AttachmentType<IPlayerEventListener> listener : listeners) {
            Optional.ofNullable(event.getEntity().getData(listener)).ifPresent(IPlayerEventListener::onPlayerLoggedOut);
        }
    }

    @SubscribeEvent
    public void onPlayerUpdate(TickEvent.@NotNull PlayerTickEvent event) {
        for (AttachmentType<IPlayerEventListener> listener : listeners) {
            Optional.ofNullable(event.player.getData(listener)).ifPresent(cap -> cap.onUpdatePlayer(event.phase));
        }
    }

    @SubscribeEvent
    public void onStartTracking(PlayerEvent.@NotNull StartTracking event) {
        if ((event.getTarget() instanceof PathfinderMob && !HelperRegistry.getSyncableEntityCaps().isEmpty()) || event.getTarget() instanceof ISyncable || (event.getTarget() instanceof Player && !HelperRegistry.getSyncablePlayerCaps().isEmpty())) {
            ClientboundUpdateEntityPacket packet = ClientboundUpdateEntityPacket.createJoinWorldPacket(event.getTarget());
            if (packet != null && event.getEntity() instanceof ServerPlayer player) {
                player.connection.send(packet);
            }
        }
    }
}
