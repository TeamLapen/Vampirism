/**
 * Licenced under GNU GPLv3. See LICENCE.txt in this package.
 * Credits to bl4ckscor3's Sit https://github.com/bl4ckscor3/Sit/
 */
package de.teamlapen.vampirism.sit;

import de.teamlapen.vampirism.core.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SitEntity extends Entity {

    public static @NotNull SitEntity newEntity(@NotNull Level level, @NotNull BlockPos pos, double offset, Vec3 playerPos) {
        SitEntity e = ModEntities.dummy_sit_entity.get().create(level);
        e.setPos(pos.getX() + 0.5D, pos.getY() + offset, pos.getZ() + 0.5D);
        e.noPhysics = true;
        e.setPlayerPos(playerPos);
        return e;
    }

    @Nullable
    private Vec3 playerPos;

    public SitEntity(@NotNull EntityType<SitEntity> type, @NotNull Level level) {
        super(type, level);
    }

    @Override
    public @NotNull Vec3 getDismountLocationForPassenger(@NotNull LivingEntity passenger) {
        if (passenger instanceof Player player) {
            Vec3 resetPosition = this.getPlayerPos();

            if (resetPosition != null) {
                BlockPos belowResetPos = BlockPos.containing(resetPosition.x, resetPosition.y - 1, resetPosition.z);

                discard();

                if (!player.level().getBlockState(belowResetPos).isFaceSturdy(level(), belowResetPos, Direction.UP, SupportType.FULL)) {
                    return new Vec3(resetPosition.x, resetPosition.y + 1, resetPosition.z);
                } else {
                    return resetPosition;
                }
            }
        }

        discard();
        return super.getDismountLocationForPassenger(passenger);
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        SitUtil.addSitEntity(level(), blockPosition(), this);
    }

    @Override
    public void remove(RemovalReason reason) {
        this.ejectPassengers();
        SitUtil.removeSitEntity(level(), blockPosition());

        super.remove(reason);
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.contains("playerPosX")){
            this.playerPos = new Vec3(tag.getDouble("playerPosX"), tag.getDouble("playerPosY"), tag.getDouble("playerPosZ"));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        if (this.playerPos != null) {
            tag.putDouble("playerPosX", this.playerPos.x);
            tag.putDouble("playerPosY", this.playerPos.y);
            tag.putDouble("playerPosZ", this.playerPos.z);
        }
    }

    @Override
    public void recreateFromPacket(ClientboundAddEntityPacket p_146866_) {
        super.recreateFromPacket(p_146866_);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public void setPlayerPos(@Nullable Vec3 pos) {
        this.playerPos = pos;
    }

    public @Nullable Vec3 getPlayerPos() {
        return playerPos;
    }
}