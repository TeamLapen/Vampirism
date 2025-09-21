package de.teamlapen.sync.common.storage;

import de.teamlapen.vampirism.api.extensions.IEntity;
import net.minecraft.resources.ResourceLocation;

/**
 * Interface for {@link net.neoforged.neoforge.attachment.AttachmentType} implementations, which should be syncable
 */
public interface IAttachedSyncable extends ISyncable, IEntity {

    /**
     * @return unique id of this attachment.
     * @implSpec The method returns the registry id of the {@link net.neoforged.neoforge.attachment.AttachmentType} of this implementation from {@link net.neoforged.neoforge.registries.NeoForgeRegistries#ATTACHMENT_TYPES}
     */
    ResourceLocation getAttachedKey();

    @Override
    default String nbtKey() {
        return getAttachedKey().toString();
    }

}
