package de.teamlapen.factions.common.world.entities;

import de.teamlapen.factions.api.util.FResourceLocation;
import de.teamlapen.factions.api.util.SafeCast;
import de.teamlapen.factions.api.world.entities.extensions.IEntity;
import de.teamlapen.sync.AttachmentSync;
import de.teamlapen.sync.BaseAttachmentSyncHandler;
import de.teamlapen.sync.PropertySync;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class EntitySyncHolder<TEntity extends EntitySyncHolder.ISyncHolder<TData>, TData extends PropertySync> extends AttachmentSync {

    private final TEntity entity;

    public EntitySyncHolder(TEntity entity) {
        this.entity = entity;
    }

    @Override
    protected void registerProperties() {
        //noinspection Convert2MethodRef
        registerProperty(FResourceLocation.mod("sub"))
                .subProperty(() -> entity.getSyncData())
                .disableServerLoad()
                .register();
    }

    @Override
    public AttachmentType<?> getType() {
        return this.entity.getDataAttachmentType();
    }

    @Override
    public @NotNull Entity asEntity() {
        return this.entity.asEntity();
    }

    public interface ISyncHolder<TData extends PropertySync> extends IEntity {

        Optional<TData> getData();

        @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
        void setData(Optional<TData> data);

        TData createData();

        PropertySync getSyncData();

        AttachmentType<?> getDataAttachmentType();

        default void sync() {
            Entity entity = asEntity();

            // init attachment
            entity.getData(getDataAttachmentType());

            // sync
            entity.syncData(getDataAttachmentType());
        }
    }

    public static class Factory<TEntity extends EntitySyncHolder.ISyncHolder<TData>,TData extends PropertySync> extends BaseAttachmentSyncHandler<EntitySyncHolder<TEntity,TData>> {

        private final Class<TEntity> clazz;

        public Factory(Class<TEntity> clazz) {
            this.clazz = clazz;
        }

        @Override
        public EntitySyncHolder<TEntity,TData> apply(IAttachmentHolder holder) {
            if (holder instanceof ISyncHolder<?> entity && this.clazz.isInstance(holder)) {
                return new EntitySyncHolder<>(SafeCast.cast(entity));
            }
            throw new IllegalArgumentException("Cannot create data holder. It must be assigned");
        }
    }
}
