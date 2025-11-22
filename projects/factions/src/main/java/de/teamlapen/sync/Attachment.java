package de.teamlapen.sync;

import de.teamlapen.sync.api.IAttachment;

public abstract class Attachment extends PropertySync implements IAttachment {

    public void sync() {
        if (this.properties.stream().anyMatch(Property::hasChanged)) {
            asEntity().syncData(getType());
        }
    }
}
