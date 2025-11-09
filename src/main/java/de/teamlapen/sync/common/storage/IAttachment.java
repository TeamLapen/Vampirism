package de.teamlapen.sync.common.storage;

import net.neoforged.neoforge.attachment.AttachmentType;

public interface IAttachment extends IAttachedSyncable, ISyncableSaveData {

    AttachmentType<?> attachmentType();
}
