/**
 * Licenced under GNU GPLv3. See LICENCE.txt in this package.
 * Credits to bl4ckscor3's Sit https://github.com/bl4ckscor3/Sit/
 */
package de.teamlapen.vampirism.sit;

import de.teamlapen.vampirism.core.ModEntities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.util.BlockVoxelShape;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class SitEntity extends Entity {

    public static SitEntity newEntity(World world, BlockPos pos, double offset, Vector3d playerPos) {
        SitEntity e = ModEntities.DUMMY_SIT_ENTITY.get().create(world);
        e.setPos(pos.getX() + 0.5D, pos.getY() + offset, pos.getZ() + 0.5D);
        e.noPhysics = true;
        e.setPlayerPos(playerPos);
        return e;
    }

    private Vector3d playerPos;

    public SitEntity(EntityType<SitEntity> type, World world) {
        super(type, world);
    }

    @Override
    public Vector3d getDismountLocationForPassenger(LivingEntity passenger) {
        if (passenger instanceof PlayerEntity) {
            Vector3d resetPosition = this.getPlayerPos();

            if (resetPosition != null) {
                BlockPos belowResetPos = new BlockPos(Math.floor(resetPosition.x), Math.floor(resetPosition.y - 1), Math.floor(resetPosition.z));

                remove();

                if (!passenger.level.getBlockState(belowResetPos).isFaceSturdy(level, belowResetPos, Direction.UP, BlockVoxelShape.FULL)) {
                    return new Vector3d(resetPosition.x, resetPosition.y + 1, resetPosition.z);
                } else {
                    return resetPosition;
                }
            }
        }

        remove();
        return super.getDismountLocationForPassenger(passenger);
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        SitUtil.addSitEntity(level, blockPosition(), this);
    }

    @Override
    public void remove() {
        this.ejectPassengers();
        SitUtil.removeSitEntity(level, blockPosition());

        super.remove();
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    protected void readAdditionalSaveData(CompoundNBT tag) {
        if (tag.contains("playerPosX")){
            this.playerPos = new Vector3d(tag.getDouble("playerPosX"), tag.getDouble("playerPosY"), tag.getDouble("playerPosZ"));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundNBT tag) {
        if (this.playerPos != null) {
            tag.putDouble("playerPosX", this.playerPos.x);
            tag.putDouble("playerPosY", this.playerPos.y);
            tag.putDouble("playerPosZ", this.playerPos.z);
        }
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public void setPlayerPos(Vector3d pos) {
        this.playerPos = pos;
    }

    public Vector3d getPlayerPos() {
        return playerPos;
    }
}