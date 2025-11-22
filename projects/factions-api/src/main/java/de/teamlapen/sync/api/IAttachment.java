package de.teamlapen.sync.api;

import de.teamlapen.factions.api.extensions.IEntity;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.util.ValueIOSerializable;

public interface IAttachment extends ValueIOSerializable, ISyncable, IEntity {

    AttachmentType<?> getType();

    void sync();
}
