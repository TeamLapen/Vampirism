package de.teamlapen.sync.common.storage;

import de.teamlapen.sync.SyncHelper;
import net.minecraft.nbt.*;

public abstract class Attachment implements IAttachment {

    public void sync(String key, Tag value, boolean sendToAll) {
        if (asEntity().level().isClientSide()) return;
        CompoundTag tag = new CompoundTag();
        tag.put(key, value);
        SyncHelper.sync(this, tag, asEntity(), sendToAll);
    }

    public void sync(String key, Tag value) {
        sync(key, value, false);
    }

    public void sync(String key, int value) {
        sync(key, IntTag.valueOf(value));
    }

    public void sync(String key, String value) {
        sync(key, StringTag.valueOf(value));
    }

    public void sync(String key, boolean value) {
        sync(key, ByteTag.valueOf(value));
    }

    public void sync(String key, int value, boolean sendToAll) {
        sync(key, IntTag.valueOf(value), sendToAll);
    }

    public void sync(String key, String value, boolean sendToAll) {
        sync(key, StringTag.valueOf(value), sendToAll);
    }

    public void sync(String key, boolean value, boolean sendToAll) {
        sync(key, ByteTag.valueOf(value), sendToAll);
    }

    public void sync() {
//        sync(UpdateParams.defaults());
    }

//    @SuppressWarnings("deprecation")
//    public final void sync(UpdateParams param) {
//        if (asEntity().level().isClientSide()) return;
//            var output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, asEntity().registryAccess());
//            serializeUpdate(output, param);
//            CompoundTag compoundTag = output.buildResult();
//            if (!compoundTag.isEmpty()) {
//                sync(compoundTag, output, param.isForAllPlayer());
//            }
//    }

    /**
     * Sync the capability using the given data
     *
     * @param allToAll Whether all tracking players should receive this packet or only the representing player
     */
    public void sync(CompoundTag data, boolean allToAll) {
        SyncHelper.sync(this, data, asEntity(), allToAll);
    }

}
