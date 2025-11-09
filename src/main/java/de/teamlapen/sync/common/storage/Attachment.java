package de.teamlapen.sync.common.storage;

public abstract class Attachment implements IAttachment {

    public void sync() {
        asEntity().syncData(attachmentType());
    }
}
