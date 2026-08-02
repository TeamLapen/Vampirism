package de.teamlapen.sync;

import de.teamlapen.faction.api.util.FIdentifier;
import de.teamlapen.sync.api.IAttachmentSync;
import de.teamlapen.sync.properties.Property;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jspecify.annotations.Nullable;

/**
 * Represents a synchronization mechanism for attachments {@link net.neoforged.neoforge.attachment.AttachmentType} where
 * data is saved on disk and synchronized to the client.
 * <p>
 * This abstract class extends {@code PropertySync}, enabling it to track and handle
 * the synchronization of properties. Additionally, it implements {@code IAttachment},
 * providing methods for dealing with attached data specific to an entity.
 */
public abstract class AttachmentSync extends PropertySync implements IAttachmentSync {

    private final SimpleMutableDataComponentMap dataComponents = new SimpleMutableDataComponentMap(this);

    public void sync() {
        if (this.properties.stream().anyMatch(Property::hasChanged)) {
            asEntity().syncData(getType());
        }
    }

    @Override
    @MustBeInvokedByOverriders
    protected void registerProperties() {
        this.registerProperty(FIdentifier.mod("components")).subProperty(() -> this.dataComponents).register();
    }

    @Override
    public @Nullable <T> T set(DataComponentType<T> componentType, @Nullable T value) {
        return dataComponents.set(componentType, value);
    }

    @Override
    public @Nullable <T> T remove(DataComponentType<? extends T> componentType) {
        return dataComponents.remove(componentType);
    }

    @Override
    public void applyComponents(DataComponentPatch patch) {
        dataComponents.applyPatch(patch);
    }

    @Override
    public void applyComponents(DataComponentMap components) {
        dataComponents.setAll(components);
    }

    @Override
    public DataComponentMap getComponents() {
        return this.dataComponents;
    }
}
