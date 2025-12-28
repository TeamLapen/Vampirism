package de.teamlapen.factions.common.world.entities;

import de.teamlapen.factions.common.event.PlayerEventHandlerEvent;
import de.teamlapen.sync.AttachmentSync;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.player.Player;
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

import java.util.Set;
import java.util.function.Supplier;

@ApiStatus.Internal
public class PlayerListenerEventHandler {

    private Set<Supplier<AttachmentType<IPlayerEventListener>>> playerEventListenerCaps = Set.of();

    public void collect(FMLLoadCompleteEvent event) {
        this.playerEventListenerCaps = ModLoader.postEventWithReturn(new PlayerEventHandlerEvent()).getAttachments();
    }

    @SubscribeEvent
    public void onChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        for (var listener : this.playerEventListenerCaps) {
            event.getEntity().getData(listener).onChangedDimension(event.getFrom(), event.getTo());
        }
    }

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof Player) {
            for (var listener : this.playerEventListenerCaps) {
                event.getEntity().getData(listener).onJoinWorld();
            }
        }

    }

    @SubscribeEvent
    public void onLivingAttack(LivingIncomingDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            for (var listener : this.playerEventListenerCaps) {
                if (event.getEntity().getData(listener).onEntityAttacked(event.getSource(), event.getAmount())) {
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
        for (var listener : this.playerEventListenerCaps) {
            event.getEntity().getData(listener).onRespawn();
        }
    }

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof Player) {
            for (var listener : this.playerEventListenerCaps) {
                event.getEntity().getData(listener).onDeath(event.getSource());
            }
        }
        if (event.getSource().getEntity() instanceof Player) {
            for (var listener : this.playerEventListenerCaps) {
                event.getSource().getEntity().getData(listener).onEntityKilled(event.getEntity(), event.getSource());
            }
        }
    }

    @SubscribeEvent
    public void onLivingUpdate(EntityTickEvent.Post event) {
        if (event.getEntity() instanceof Player) {
            for (var type : this.playerEventListenerCaps) {
                IPlayerEventListener listener = event.getEntity().getData(type);
                listener.onUpdate();
                if (listener  instanceof AttachmentSync syncable && !event.getEntity().level().isClientSide()) {
                    syncable.sync();
                }
            }
        }
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        for (var listener : this.playerEventListenerCaps) {
            event.getEntity().getData(listener).onPlayerLoggedIn();
        }
    }

    @SubscribeEvent
    public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        for (var listener : this.playerEventListenerCaps) {
            event.getEntity().getData(listener).onPlayerLoggedOut();
        }
    }

    @SubscribeEvent
    public void onPlayerUpdate(PlayerTickEvent.Post event) {
        for (var listener : this.playerEventListenerCaps) {
            event.getEntity().getData(listener).onUpdatePlayer(event);
        }
    }
}
