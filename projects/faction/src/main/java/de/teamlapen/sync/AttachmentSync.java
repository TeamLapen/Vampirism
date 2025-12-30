package de.teamlapen.sync;

import de.teamlapen.sync.api.IAttachmentSync;
import de.teamlapen.sync.properties.Property;

/**
 * Represents a synchronization mechanism for attachments {@link net.neoforged.neoforge.attachment.AttachmentType} where
 * data is saved on disk and synchronized to the client.
 * <p>
 * This abstract class extends {@code PropertySync}, enabling it to track and handle
 * the synchronization of properties. Additionally, it implements {@code IAttachment},
 * providing methods for dealing with attached data specific to an entity.
 */
public abstract class AttachmentSync extends PropertySync implements IAttachmentSync {

    public void sync() {
        if (this.properties.stream().anyMatch(Property::hasChanged)) {
            asEntity().syncData(getType());
        }
    }
}
