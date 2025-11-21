package de.teamlapen.sync;

import com.google.common.collect.ImmutableList;
import de.teamlapen.lib.util.ThreadSafeLibAPI;
import de.teamlapen.sync.common.entities.IPlayerEventListener;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.InterModProcessEvent;
import net.neoforged.neoforge.attachment.AttachmentType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@EventBusSubscriber
public class SyncRegistry {

    private static Set<AttachmentType<? extends IPlayerEventListener>> playerEventListenerCaps = ConcurrentHashMap.newKeySet();


    private static ImmutableList<AttachmentType<IPlayerEventListener>> playerEventListenerCapsFinal;

    @Unmodifiable
    public static @NotNull List<AttachmentType<IPlayerEventListener>> getEventListenerCaps() {
        if (playerEventListenerCapsFinal == null) {
            throw new IllegalStateException("Cannot get PlayerEventReceiver before the InterModProcessEvent");
        }
        return playerEventListenerCapsFinal;
    }

    @ThreadSafeLibAPI
    public static void registerPlayerEventHandler(AttachmentType<? extends IPlayerEventListener> capability) {
        if (playerEventListenerCaps == null) {
            throw new IllegalStateException("Cannot register PlayerEventReceiver (" + capability + ") after the InterModEnqueueEvent");
        } else {
            playerEventListenerCaps.add(capability);
        }
    }

    @SubscribeEvent
    public static void onRegistered(InterModProcessEvent event) {
        finish();
    }

    @SuppressWarnings("unchecked")
    static void finish() {
        playerEventListenerCapsFinal = ImmutableList.copyOf((Set<AttachmentType<IPlayerEventListener>>) (Object) playerEventListenerCaps);
        playerEventListenerCaps = null;
    }
}
