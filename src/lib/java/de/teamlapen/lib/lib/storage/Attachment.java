package de.teamlapen.lib.lib.storage;

import de.teamlapen.lib.HelperLib;
import net.minecraft.nbt.*;
import org.jetbrains.annotations.NotNull;

public abstract class Attachment implements IAttachment {

    public void sync(String key, Tag value, boolean sendToAll) {
        if (asEntity().level().isClientSide()) return;
        CompoundTag tag = new CompoundTag();
        tag.put(key, value);
        HelperLib.sync(this, tag, asEntity(), sendToAll);
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
        sync(UpdateParams.defaults());
    }

    public final void sync(UpdateParams param) {
        if (asEntity().level().isClientSide()) return;
        CompoundTag tag = serializeUpdateNBT(this.registryAccess(), param);
        if (!tag.isEmpty()) {
            sync(tag, param.isForAllPlayer());
        }
    }

    /**
     * Sync the capability using the given data
     *
     * @param allToAll Whether all tracking players should receive this packet or only the representing player
     */
    public void sync(@NotNull CompoundTag data, boolean allToAll) {
        HelperLib.sync(this, data, asEntity(), allToAll);
    }

}
