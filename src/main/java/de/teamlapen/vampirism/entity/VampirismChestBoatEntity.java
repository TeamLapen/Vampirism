package de.teamlapen.vampirism.entity;

import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.core.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.ChestBoat;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

public class VampirismChestBoatEntity extends ChestBoat implements IVampirismBoat {

    private static final EntityDataAccessor<Integer> DATA_ID_TYPE = SynchedEntityData.defineId(VampirismChestBoatEntity.class, EntityDataSerializers.INT);

    public VampirismChestBoatEntity(@NotNull EntityType<? extends Boat> type, @NotNull Level level) {
        super(type, level);
    }

    public VampirismChestBoatEntity(@NotNull Level level, double x, double y, double z) {
        super(ModEntities.CHEST_BOAT.get(), level);
        this.setPos(x, y, z);
        this.setDeltaMovement(Vec3.ZERO);
        this.xo = x;
        this.yo = y;
        this.zo = z;
    }

    /**
     * @deprecated use {@link #setType(de.teamlapen.vampirism.entity.IVampirismBoat.BoatType)}
     */
    @Deprecated
    @Override
    public void setType(@NotNull Type type) {
    }

    /**
     * @deprecated use {@link #getBType()}
     */
    @NotNull
    @Deprecated
    @Override
    public Type getBoatType() {
        return Type.OAK;
    }

    @Override
    @NotNull
    public BoatType getBType() {
        return IVampirismBoat.BoatType.byId(this.entityData.get(DATA_ID_TYPE));
    }

    @Override
    public void setType(@NotNull BoatType type) {
        this.entityData.set(DATA_ID_TYPE, type.ordinal());
    }

    @NotNull
    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @NotNull
    @Override
    public Item getDropItem() {
        return switch (this.getBType()) {
            case DARK_SPRUCE -> ModItems.DARK_SPRUCE_CHEST_BOAT.get();
            case CURSED_SPRUCE -> ModItems.CURSED_SPRUCE_CHEST_BOAT.get();
        };
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag tag) {
        tag.putString("Type", this.getBType().getName());
        this.addChestVehicleSaveData(tag);
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag tag) {
        if (tag.contains("Type", 8)) {
            this.setType(BoatType.byName(tag.getString("Type")));
        }
        this.readChestVehicleSaveData(tag);
    }

    @NotNull
    @Override
    protected Component getTypeName() {
        return EntityType.CHEST_BOAT.getDescription();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_ID_TYPE, IVampirismBoat.BoatType.DARK_SPRUCE.ordinal());
    }
}
