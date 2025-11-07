/*
 * Licenced under GNU GPLv3. See LICENCE.txt in this package.
 * Credits to bl4ckscor3's Sit https://github.com/bl4ckscor3/Sit/
 */

package de.teamlapen.vampirism.misc.sit;

import com.mojang.serialization.Codec;
import de.teamlapen.vampirism.common.core.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SitEntity extends Entity {

    public static @NotNull SitEntity newEntity(@NotNull Level level, @NotNull BlockPos pos, double offset, Vec3 playerPos) {
        SitEntity e = ModEntities.SIT_DUMMY.get().create(level, EntitySpawnReason.MOB_SUMMONED);
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
    public void onAddedToLevel() {
        super.onAddedToLevel();
        SitUtil.addSitEntity(level(), blockPosition(), this);
    }

    @Override
    public void remove(RemovalReason reason) {
        this.ejectPassengers();
        SitUtil.removeSitEntity(level(), blockPosition());

        super.remove(reason);
    }

    @Override
    public boolean hurtServer(ServerLevel p_376804_, DamageSource p_376155_, float p_376892_) {
        return false;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder p_326003_) {

    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        input.read("playerPos", Codec.DOUBLE.listOf(3, 3)).ifPresent(data -> this.playerPos = new Vec3(data.get(0), data.get(1), data.get(2)));
    }

    @Override
    protected void addAdditionalSaveData(@NotNull ValueOutput output) {
        if (this.playerPos != null) {
            output.store("playerPos", Codec.DOUBLE.listOf(3, 3), List.of(this.playerPos.x, this.playerPos.y, this.playerPos.z));
        }
    }

    @Override
    public void recreateFromPacket(ClientboundAddEntityPacket packet) {
        super.recreateFromPacket(packet);
    }

    public void setPlayerPos(@Nullable Vec3 pos) {
        this.playerPos = pos;
    }

    public @Nullable Vec3 getPlayerPos() {
        return playerPos;
    }
}