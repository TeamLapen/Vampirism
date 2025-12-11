package de.teamlapen.factions.common.util;

import com.mojang.logging.LogUtils;
import de.teamlapen.factions.api.extensions.IEntity;
import de.teamlapen.sync.api.IAttachment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.attachment.AttachmentSyncHandler;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.function.Function;

public abstract class AttachmentSynchronization<T extends IAttachment & IEntity, Z extends IAttachmentHolder> implements IAttachmentSerializer<T>, AttachmentSyncHandler<T>, Function<IAttachmentHolder, T> {

    private static final Logger LOGGER = LogUtils.getLogger();

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

    @Override
    public void write(RegistryFriendlyByteBuf buf, T attachment, boolean initialSync) {
        try (var problemReported = new ProblemReporter.ScopedCollector(attachment.asEntity().problemPath(), LOGGER)) {
            TagValueOutput output = TagValueOutput.createWithContext(problemReported, attachment.asEntity().registryAccess());
            if (initialSync) {
                attachment.serializeFullUpdate(output);
            } else {
                attachment.serializeUpdate(output);
            }
            buf.writeNbt(output.buildResult());
        }
    }

    @Override
    public @Nullable T read(IAttachmentHolder holder, RegistryFriendlyByteBuf buf, @Nullable T previousValue) {
        if (previousValue == null) {
            previousValue = apply(holder);
        }
        try (var problemReported = new ProblemReporter.ScopedCollector(previousValue.asEntity().problemPath(), LOGGER)) {
            CompoundTag compoundTag = buf.readNbt();
            if (compoundTag == null) return null;
            var input = TagValueInput.create(problemReported, previousValue.asEntity().registryAccess(), compoundTag);
            previousValue.deserializeUpdate(input);
            return previousValue;
        }
    }

    public static abstract class PlayerOptions<T extends IAttachment & IEntity> extends AttachmentSynchronization<T, Player> {
        @Override
        public T apply(IAttachmentHolder holder) {
            if (holder instanceof net.minecraft.world.entity.player.Player player) {
                return create(player);
            }
            throw new IllegalArgumentException("Cannot create attachment for holder " + holder.getClass() + ". Expected Player");
        }
    }
}