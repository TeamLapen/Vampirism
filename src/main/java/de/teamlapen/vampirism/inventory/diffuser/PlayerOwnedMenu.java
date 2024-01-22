package de.teamlapen.vampirism.inventory.diffuser;

import de.teamlapen.vampirism.blockentity.PlayerOwnedBlockEntity;
import de.teamlapen.vampirism.network.PlayerOwnedBlockEntityLockPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.network.IContainerFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class PlayerOwnedMenu extends AbstractContainerMenu {

    private final Player player;
    private final PlayerOwnedBlockEntity.LockDataHolder lockData;

    protected PlayerOwnedMenu(@Nullable MenuType<?> pMenuType, int pContainerId, Player player, @NotNull PlayerOwnedBlockEntity.LockDataHolder lockData) {
        super(pMenuType, pContainerId);
        this.player = player;
        this.lockData = lockData;
    }

    public boolean hasOwner() {
        return this.lockData.getOwner() != null;
    }

    public boolean isOwner(Player player) {
        return this.lockData.getOwner() != null && this.lockData.getOwner().equals(player.getUUID());
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        if (!this.player.level().isClientSide && player instanceof ServerPlayer serverPlayer && !lockData.canAccess(serverPlayer)) {
            serverPlayer.closeContainer();
        }
    }

    public PlayerOwnedBlockEntity.@NotNull Lock getLockStatus() {
        return this.lockData.getLockStatus();
    }

    public void updateLockStatus(PlayerOwnedBlockEntity.@NotNull Lock lockStatus) {
        this.lockData.updateStatus(this.player, lockStatus);
        broadcastChanges();
    }

    public void setLockStatus(PlayerOwnedBlockEntity.@NotNull Lock lockStatus) {
        this.lockData.updateStatus(this.player, lockStatus);
    }

    public CustomPacketPayload updatePackage() {
        return new PlayerOwnedBlockEntityLockPacket(this.containerId, this.lockData);
    }

    protected static abstract class Factory<T extends PlayerOwnedMenu> implements IContainerFactory<T> {
        @Override
        public T create(int windowId, Inventory inv, FriendlyByteBuf data) {
            return create(windowId, inv, PlayerOwnedBlockEntity.LockDataHolder.createFromBuffer(data));
        }

        public abstract T create(int windowId, Inventory inv, PlayerOwnedBlockEntity.LockDataHolder lockData);
    }
}
