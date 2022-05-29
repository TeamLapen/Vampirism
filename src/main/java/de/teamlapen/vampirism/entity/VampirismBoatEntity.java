package de.teamlapen.vampirism.entity;

import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.items.VampirismBoatItem;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;

public class VampirismBoatEntity extends BoatEntity {

    private static final DataParameter<Integer> DATA_ID_TYPE = EntityDataManager.defineId(VampirismBoatEntity.class, DataSerializers.INT);


    public VampirismBoatEntity(EntityType<? extends VampirismBoatEntity> type, World level) {
        super(type, level);
    }

    public VampirismBoatEntity(World level, double x, double y, double z) {
        this(ModEntities.BOAT.get(), level);
        this.setPos(x,y,z);
        this.setDeltaMovement(Vector3d.ZERO);
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
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Nonnull
    @Override
    public Item getDropItem() {
        switch (this.getBType()) {
            case DARK_SPRUCE:
                return ModItems.DARK_SPRUCE_BOAT.get();
            case CURSED_SPRUCE:
                return ModItems.CURSED_SPRUCE_BOAT.get();
        }
        return Items.AIR;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_ID_TYPE, VampirismBoatItem.BoatType.DARK_SPRUCE.ordinal());
    }
}
