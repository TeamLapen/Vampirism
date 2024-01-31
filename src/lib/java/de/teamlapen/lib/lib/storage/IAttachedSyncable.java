package de.teamlapen.lib.lib.storage;

import de.teamlapen.lib.lib.entity.IEntity;
import net.minecraft.resources.ResourceLocation;

/**
 * Interface for {@link net.neoforged.neoforge.attachment.AttachmentType} implementations, which should be syncable
 */
public interface IAttachedSyncable extends ISyncable, IEntity {

    /**
     *
     * @implSpec The method returns the registry id of the {@link net.neoforged.neoforge.attachment.AttachmentType} of this implementation from {@link net.neoforged.neoforge.registries.NeoForgeRegistries#ATTACHMENT_TYPES}
     * @return unique id of this attachment.
     */
    ResourceLocation getAttachedKey();

}
