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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class SitEntity extends Entity {

    public static SitEntity newEntity(World world, BlockPos pos, double offset) {
        SitEntity e = ModEntities.DUMMY_SIT_ENTITY.get().create(world);
        e.setPos(pos.getX() + 0.5D, pos.getY() + offset, pos.getZ() + 0.5D);
        e.noPhysics = true;
        return e;
    }

    public SitEntity(EntityType<SitEntity> type, World world) {
        super(type, world);
    }


    @Override
    public Vector3d getDismountLocationForPassenger(LivingEntity passenger) {
        if (passenger instanceof PlayerEntity) {
            BlockPos pos = SitUtil.getPreviousPlayerPosition((PlayerEntity) passenger, this);

            if (pos != null) {
                remove();
                return new Vector3d(pos.getX(), pos.getY(), pos.getZ());
            }
        }

        remove();
        return super.getDismountLocationForPassenger(passenger);
    }

    @Override
    public void remove() {
        super.remove();

        SitUtil.removeSitEntity(level, blockPosition());
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    protected void readAdditionalSaveData(CompoundNBT tag) {
    }

    @Override
    protected void addAdditionalSaveData(CompoundNBT tag) {
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}