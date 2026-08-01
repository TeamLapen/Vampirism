package de.teamlapen.faction.common.event;

import com.google.common.collect.ImmutableSet;
import de.teamlapen.faction.api.util.SafeCast;
import de.teamlapen.faction.common.world.entities.IPlayerEventListener;
import de.teamlapen.faction.common.world.entities.PlayerListenerEventHandler;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;
import net.neoforged.neoforge.attachment.AttachmentType;
import org.apache.commons.lang3.tuple.Pair;
import org.jspecify.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class PlayerEventHandlerEvent extends Event implements IModBusEvent {

    private final Set<PlayerListenerEventHandler.Entry> attachments = new HashSet<>();

    public void addAttachmentListener(Supplier<? extends AttachmentType<? extends IPlayerEventListener>> listener) {
        this.addAttachmentListener(null, listener);
    }

    public void addServerAttachmentListener(Supplier<? extends AttachmentType<? extends IPlayerEventListener>> listener) {
        this.addAttachmentListener(Dist.DEDICATED_SERVER, listener);
    }

    public void addClientAttachmentListener(Supplier<? extends AttachmentType<? extends IPlayerEventListener>> listener) {
        this.addAttachmentListener(Dist.CLIENT, listener);
    }

    @SuppressWarnings("unchecked")
    private void addAttachmentListener(@Nullable Dist dist, Supplier<? extends AttachmentType<? extends IPlayerEventListener>> listener) {
        attachments.add(new PlayerListenerEventHandler.Entry(dist, (Supplier<AttachmentType<IPlayerEventListener>>) listener));
    }

    public Set<PlayerListenerEventHandler.Entry> getAttachments() {
        return SafeCast.cast(ImmutableSet.copyOf(this.attachments));
    }
}
