package de.teamlapen.factions.common.event;

import com.google.common.collect.ImmutableSet;
import de.teamlapen.factions.api.util.SafeCast;
import de.teamlapen.factions.common.world.entities.IPlayerEventListener;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;
import net.neoforged.neoforge.attachment.AttachmentType;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class PlayerEventHandlerEvent extends Event implements IModBusEvent {

    private final Set<Supplier<? extends AttachmentType<? extends IPlayerEventListener>>> attachments = new HashSet<>();

    public void addAttachmentListener(Supplier<? extends AttachmentType<? extends IPlayerEventListener>> listener) {
        attachments.add(listener);
    }

    public Set<Supplier<AttachmentType<IPlayerEventListener>>> getAttachments() {
        return SafeCast.cast(ImmutableSet.copyOf(this.attachments));
    }
}
