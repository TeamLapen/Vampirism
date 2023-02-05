/*
 * Licenced under GNU GPLv3. See LICENCE.txt in this package.
 * Credits to bl4ckscor3's Sit https://github.com/bl4ckscor3/Sit/
 */

package de.teamlapen.vampirism.sit;

import de.teamlapen.vampirism.core.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

public class SitEntity extends Entity {

    public static @NotNull SitEntity newEntity(@NotNull Level level, @NotNull BlockPos pos, double offset) {
        SitEntity e = ModEntities.dummy_sit_entity.get().create(level);
        e.setPos(pos.getX() + 0.5D, pos.getY() + offset, pos.getZ() + 0.5D);
        e.noPhysics = true;
        return e;
    }

    public SitEntity(@NotNull EntityType<SitEntity> type, @NotNull Level level) {
        super(type, level);
    }


    @Override
    public @NotNull Vec3 getDismountLocationForPassenger(LivingEntity passenger) {
        if (passenger instanceof Player) {
            BlockPos pos = SitUtil.getPreviousPlayerPosition((Player) passenger, this);

            if (pos != null) {
                remove(RemovalReason.DISCARDED);
                return new Vec3(pos.getX(), pos.getY(), pos.getZ());
            }
        }

        remove(RemovalReason.DISCARDED);
        return super.getDismountLocationForPassenger(passenger);
    }

    @Override
    public void remove(RemovalReason reason) {
        super.remove(reason);

        SitUtil.removeSitEntity(level, blockPosition());
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
    }

    @Override
    public void recreateFromPacket(ClientboundAddEntityPacket p_146866_) {
        super.recreateFromPacket(p_146866_);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}