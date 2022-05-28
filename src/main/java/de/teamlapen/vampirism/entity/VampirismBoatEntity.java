package de.teamlapen.vampirism.entity;

import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.items.VampirismBoatItem;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nonnull;

public class VampirismBoatEntity extends Boat {

    private static final EntityDataAccessor<Integer> DATA_ID_TYPE = SynchedEntityData.defineId(VampirismBoatEntity.class, EntityDataSerializers.INT);


    public VampirismBoatEntity(EntityType<? extends VampirismBoatEntity> type, Level level) {
        super(type, level);
    }

    public VampirismBoatEntity(Level level, double x, double y, double z) {
        this(ModEntities.BOAT.get(), level);
        this.setPos(x,y,z);
        this.setDeltaMovement(Vec3.ZERO);
        this.xo = x;
        this.yo = y;
        this.zo = z;
    }

    /**
     * @deprecated  use {@link #setType(de.teamlapen.vampirism.items.VampirismBoatItem.BoatType)}
     */
    @Deprecated
    @Override
    public void setType(@Nonnull Type type) {
    }

    /**
     * @deprecated use {@link #getBType()}
     */
    @Nonnull
    @Deprecated
    @Override
    public Type getBoatType() {
        return Type.OAK;
    }

    @Nonnull
    public VampirismBoatItem.BoatType getBType() {
        return VampirismBoatItem.BoatType.byId(this.entityData.get(DATA_ID_TYPE));
    }

    public void setType(VampirismBoatItem.BoatType type) {
        this.entityData.set(DATA_ID_TYPE, type.ordinal());
    }

    @Nonnull
    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Nonnull
    @Override
    public Item getDropItem() {
        return switch (this.getBType()) {
            case DARK_SPRUCE -> ModItems.dark_spruce_boat.get();
            case CURSED_SPRUCE -> ModItems.cursed_spruce_boat.get();
        };
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_ID_TYPE, VampirismBoatItem.BoatType.DARK_SPRUCE.ordinal());
    }
}
