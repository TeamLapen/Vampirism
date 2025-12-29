package de.teamlapen.sync.api;

import de.teamlapen.factions.api.world.entities.extensions.IEntity;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.util.ValueIOSerializable;

/**
 * Base interface for load/saving and synchronization of attachments.
 */
public interface IAttachmentSync extends ValueIOSerializable, ISyncable, IEntity {

    /**
     * The {@link AttachmentType} of this attachment.
     */
    AttachmentType<?> getType();
}
