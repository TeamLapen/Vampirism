package de.teamlapen.factions.common.util;

import de.teamlapen.factions.api.world.entities.extensions.IEntity;
import de.teamlapen.sync.BaseAttachmentSyncHandler;
import de.teamlapen.sync.api.IAttachmentSync;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import org.jetbrains.annotations.NotNull;

public abstract class AttachmentSynchronization<T extends IAttachmentSync & IEntity, Z extends IAttachmentHolder> extends BaseAttachmentSyncHandler<@NotNull T> implements IAttachmentSerializer<T> {

    @Override
    public T read(IAttachmentHolder holder, ValueInput input) {
        T attachment = apply(holder);
        attachment.deserialize(input);
        return attachment;
    }

    protected abstract T create(Z player);

    @Override
    public boolean write(T attachment, ValueOutput output) {
        attachment.serialize(output);
        return true;
    }

    public static abstract class PlayerOptions<T extends IAttachmentSync & IEntity> extends AttachmentSynchronization<T, Player> {
        @Override
        public T apply(IAttachmentHolder holder) {
            if (holder instanceof net.minecraft.world.entity.player.Player player) {
                return create(player);
            }
            throw new IllegalArgumentException("Cannot create attachment for holder " + holder.getClass() + ". Expected Player");
        }
    }
}