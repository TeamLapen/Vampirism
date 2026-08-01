package de.teamlapen.faction.common.world.entities;

import de.teamlapen.faction.common.event.PlayerEventHandlerEvent;
import de.teamlapen.sync.AttachmentSync;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoader;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

@ApiStatus.Internal
public class PlayerListenerEventHandler {

    private Set<Entry> playerEventListenerCaps = Set.of();

    public void collect(FMLLoadCompleteEvent event) {
        this.playerEventListenerCaps = ModLoader.postEventWithReturn(new PlayerEventHandlerEvent()).getAttachments();
    }

    public record Entry(@Nullable Dist dist, Supplier<AttachmentType<IPlayerEventListener>> attachment) {

        public void run(Player player, Consumer<IPlayerEventListener> consumer) {
            if (dist == Dist.DEDICATED_SERVER && !(player instanceof ServerPlayer)) {
                return;
            }
            if (dist == Dist.CLIENT && !(player instanceof AbstractClientPlayer)) {
                return;
            }

            consumer.accept(player.getData(attachment.get()));
        }
    }

    @SubscribeEvent
    public void onChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        for (var listener : this.playerEventListenerCaps) {
            listener.run(event.getEntity(), x -> x.onChangedDimension(event.getFrom(), event.getTo()));
        }
    }

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof Player player) {
            for (var listener : this.playerEventListenerCaps) {
                listener.run(player, IPlayerEventListener::onJoinWorld);
            }
        }

    }

    @SubscribeEvent
    public void onLivingAttack(LivingIncomingDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            for (var listener : this.playerEventListenerCaps) {
                listener.run(player, x -> {
                    if (x.onEntityAttacked(event.getSource(), event.getAmount())) {
                        event.setCanceled(true);
                    }
                });
            }
        }
    }

    @SubscribeEvent
    public void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
        for (var listener : this.playerEventListenerCaps) {
            listener.run(event.getEntity(), IPlayerEventListener::onRespawn);
        }
    }

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof Player player) {
            for (var listener : this.playerEventListenerCaps) {
                listener.run(player, x -> x.onDeath(event.getSource()));
            }
        }
        if (event.getSource().getEntity() instanceof Player attacker) {
            for (var listener : this.playerEventListenerCaps) {
                listener.run(attacker, x -> x.onEntityKilled(event.getEntity(), event.getSource()));
            }
        }
    }

    @SubscribeEvent
    public void onLivingUpdate(EntityTickEvent.Post event) {
        if (event.getEntity() instanceof Player player) {
            for (var type : this.playerEventListenerCaps) {
                type.run(player, listener -> {
                    listener.onUpdate();
                    if (listener instanceof AttachmentSync syncable && !player.level().isClientSide()) {
                        syncable.sync();
                    }
                });
            }
        }
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        for (var listener : this.playerEventListenerCaps) {
            listener.run(event.getEntity(), IPlayerEventListener::onPlayerLoggedIn);
        }
    }

    @SubscribeEvent
    public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        for (var listener : this.playerEventListenerCaps) {
            listener.run(event.getEntity(), IPlayerEventListener::onPlayerLoggedOut);
        }
    }

    @SubscribeEvent
    public void onPlayerUpdate(PlayerTickEvent.Post event) {
        for (var listener : this.playerEventListenerCaps) {
            listener.run(event.getEntity(), x -> x.onUpdatePlayer(event));
        }
    }
}
