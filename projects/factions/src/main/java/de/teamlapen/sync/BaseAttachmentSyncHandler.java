package de.teamlapen.sync;

import com.mojang.logging.LogUtils;
import de.teamlapen.factions.api.world.entities.extensions.IEntity;
import de.teamlapen.sync.api.IAttachmentSync;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.neoforged.neoforge.attachment.AttachmentSyncHandler;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.function.Function;

public abstract class BaseAttachmentSyncHandler<T extends IAttachmentSync & IEntity> implements AttachmentSyncHandler<T>, Function<IAttachmentHolder, T> {
    private static final Logger LOGGER = LogUtils.getLogger();

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
}
