package de.teamlapen.vampirism.blockentity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.inventory.diffuser.PlayerOwnedMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public abstract class PlayerOwnedBlockEntity extends BaseContainerBlockEntity {

    private final LockDataHolder lockDataHolder = new LockDataHolder();

    protected PlayerOwnedBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }

    public void setOwned(Player player) {
        this.lockDataHolder.owner = player.getUUID();
    }

    public static boolean tryAccess(Player player, PlayerOwnedBlockEntity blockEntity, Component displayName) {
        if (!player.isSpectator() && !blockEntity.lockDataHolder.canAccess(player)) {
            player.displayClientMessage(Component.translatable("container.isLocked", displayName), true);
            player.playNotifySound(SoundEvents.CHEST_LOCKED, SoundSource.BLOCKS, 1.0F, 1.0F);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean canOpen(@NotNull Player pPlayer) {
        return super.canOpen(pPlayer) && tryAccess(pPlayer, this, this.getName());
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag pTag) {
        super.saveAdditional(pTag);
        if (this.lockDataHolder.owner != null) {
            pTag.putUUID("owner", this.lockDataHolder.owner);
            pTag.putString("lockStatus", this.lockDataHolder.lockStatus.getSerializedName());
        }
    }

    @Override
    public void load(@NotNull CompoundTag pTag) {
        super.load(pTag);
        if (pTag.hasUUID("owner")) {
            this.lockDataHolder.owner = pTag.getUUID("owner");
            this.lockDataHolder.lockStatus = Lock.get(pTag.getString("lockStatus"));
        }
    }

    @Override
    protected final @NotNull PlayerOwnedMenu createMenu(int pContainerId, @NotNull Inventory pInventory) {
        return createMenu(pContainerId, pInventory, lockDataHolder);
    }

    protected abstract  PlayerOwnedMenu createMenu(int pContainerId, @NotNull Inventory pInventory, @NotNull LockDataHolder lockData);

    public void writeExtraData(FriendlyByteBuf buffer) {
        this.lockDataHolder.writeToBuffer(buffer);
    }

    public static class LockDataHolder {
        public static final Codec<LockDataHolder> CODEC = RecordCodecBuilder.create(inst ->
                inst.group(
                        Codec.STRING.xmap(UUID::fromString, UUID::toString).optionalFieldOf("owner").forGetter(l -> java.util.Optional.ofNullable(l.owner)),
                        StringRepresentable.fromEnum(Lock::values).fieldOf("lockStatus").forGetter(LockDataHolder::getLockStatus)
                ).apply(inst, LockDataHolder::new)
        );

        private @Nullable UUID owner;
        private Lock lockStatus = Lock.PUBLIC;

        public LockDataHolder() {
        }

        public LockDataHolder(Lock lockStatus) {
            this.lockStatus = lockStatus;
        }

        @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
        private LockDataHolder(Optional<UUID> owner, Lock lockStatus) {
            this.owner = owner.orElse(null);
            this.lockStatus = lockStatus;
        }

        public @Nullable UUID getOwner() {
            return owner;
        }

        public Lock getLockStatus() {
            return lockStatus;
        }

        public boolean canAccess(Player player) {
            return this.lockStatus == Lock.PUBLIC || this.isOwner(player);
        }

        public boolean isOwner(Player player) {
            return this.owner != null && this.owner.equals(player.getUUID());
        }


        private void writeToBuffer(FriendlyByteBuf buffer) {
            buffer.writeBoolean(this.owner != null);
            if (owner != null) {
                buffer.writeUUID(this.owner);
                buffer.writeEnum(this.lockStatus);
            }
        }

        private void readFromBuffer(FriendlyByteBuf buffer) {
            if (buffer.readBoolean()) {
                this.owner = buffer.readUUID();
                this.lockStatus = buffer.readEnum(Lock.class);
            }
        }

        public static LockDataHolder createFromBuffer(FriendlyByteBuf buffer) {
            var holder = new LockDataHolder();
            holder.readFromBuffer(buffer);
            return holder;
        }

        public void updateStatus(Player player, Lock lockStatus) {
            if (this.isOwner(player)) {
                this.lockStatus = lockStatus;
            }
        }
    }

    public enum Lock implements StringRepresentable {
        PUBLIC("public"),
        PRIVATE("private");

        private final String name;

        Lock(String name) {
            this.name = name;
        }

        @Override
        public @NotNull String getSerializedName() {
            return this.name;
        }
        public static Lock get(String key) {
            for (Lock value : values()) {
                if (value.name.equals(key)) {
                    return value;
                }
            }
            return PUBLIC;
        }
    }
}
