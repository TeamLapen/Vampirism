package de.teamlapen.sync.api;

import de.teamlapen.faction.api.world.entities.extensions.IEntity;
import net.minecraft.core.component.DataComponentGetter;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.MutableDataComponentHolder;
import net.neoforged.neoforge.common.extensions.IDataComponentHolderExtension;
import net.neoforged.neoforge.common.util.ValueIOSerializable;

/**
 * Base interface for load/saving and synchronization of attachments.
 */
public interface IAttachmentSync extends ValueIOSerializable, ISyncable, IEntity, MutableDataComponentHolder {

    /**
     * The {@link AttachmentType} of this attachment.
     */
    AttachmentType<?> getType();
}
