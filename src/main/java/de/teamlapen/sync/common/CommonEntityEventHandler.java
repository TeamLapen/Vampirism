package de.teamlapen.sync.common;

import de.teamlapen.sync.common.entities.IPlayerEventListener;
import de.teamlapen.sync.common.storage.Attachment;
import de.teamlapen.sync.common.storage.ISyncable;
import de.teamlapen.sync.SyncRegistry;
import de.teamlapen.sync.common.packages.ClientboundUpdateEntityPacket;
import de.teamlapen.vampirism.REFERENCE;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

@EventBusSubscriber(modid = REFERENCE.MODID)
public class CommonEntityEventHandler {

    private static Collection<AttachmentType<IPlayerEventListener>> listeners() {
        return SyncRegistry.getEventListenerCaps();
    }

    @SubscribeEvent
    public static void onChangedDimension(PlayerEvent.@NotNull PlayerChangedDimensionEvent event) {
        for (AttachmentType<IPlayerEventListener> listener : listeners()) {
            event.getEntity().getData(listener).onChangedDimension(event.getFrom(), event.getTo());
        }
    }

    @SubscribeEvent
    public static void onEntityJoinWorld(@NotNull EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof Player) {
            for (AttachmentType<IPlayerEventListener> listener : listeners()) {
                event.getEntity().getData(listener).onJoinWorld();
            }
        }

    }

    @SubscribeEvent
    public static void onLivingAttack(@NotNull LivingIncomingDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            for (AttachmentType<IPlayerEventListener> listener : listeners()) {
                if (event.getEntity().getData(listener).onEntityAttacked(event.getSource(), event.getAmount())) {
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(@NotNull LivingDeathEvent event) {
        if (event.getEntity() instanceof Player) {
            for (AttachmentType<IPlayerEventListener> listener : listeners()) {
                event.getEntity().getData(listener).onDeath(event.getSource());
            }
        }
        if (event.getSource().getEntity() instanceof Player) {
            for (AttachmentType<IPlayerEventListener> listener : listeners()) {
                event.getSource().getEntity().getData(listener).onEntityKilled(event.getEntity(), event.getSource());
            }
        }
    }

    @SubscribeEvent
    public static void onLivingUpdate(EntityTickEvent.Post event) {
        if (event.getEntity() instanceof Player) {
            for (AttachmentType<IPlayerEventListener> type : listeners()) {
                IPlayerEventListener listener = event.getEntity().getData(type);
                listener.onUpdate();
                if (listener  instanceof Attachment syncable && !event.getEntity().level().isClientSide()) {
                    syncable.sync();
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.@NotNull PlayerLoggedInEvent event) {
        for (AttachmentType<IPlayerEventListener> listener : listeners()) {
            event.getEntity().getData(listener).onPlayerLoggedIn();
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.@NotNull PlayerLoggedOutEvent event) {
        for (AttachmentType<IPlayerEventListener> listener : listeners()) {
            event.getEntity().getData(listener).onPlayerLoggedOut();
        }
    }

    @SubscribeEvent
    public static void onPlayerUpdate(PlayerTickEvent.Post event) {
        for (AttachmentType<IPlayerEventListener> listener : listeners()) {
            event.getEntity().getData(listener).onUpdatePlayer(event);
        }
    }

    @SubscribeEvent
    public static void onStartTracking(PlayerEvent.@NotNull StartTracking event) {
        if ((event.getTarget() instanceof PathfinderMob && !SyncRegistry.getSyncableEntityCaps().isEmpty()) || event.getTarget() instanceof ISyncable || (event.getTarget() instanceof Player && !SyncRegistry.getSyncablePlayerCaps().isEmpty())) {
            ClientboundUpdateEntityPacket packet = ClientboundUpdateEntityPacket.createJoinWorldPacket(event.getTarget());
            if (packet != null && event.getEntity() instanceof ServerPlayer player) {
                player.connection.send(packet);
            }
        }
    }
}
