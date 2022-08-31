package de.teamlapen.vampirism.blockentity;

import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModTiles;
import de.teamlapen.vampirism.core.ModVillage;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.stream.Collectors;

public class MotherBlockEntity extends BlockEntity {

    private final ServerBossEvent bossEvent = new ServerBossEvent(Component.translatable("block.vampirism.mother"), BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.NOTCHED_10);
    private boolean isVulnerable;
    private int rootVulnerableTicks;

    public MotherBlockEntity(BlockPos pos, BlockState state) {
        super(ModTiles.MOTHER.get(), pos, state);
        bossEvent.setProgress(1);
    }


    public static void serverTick(Level level, BlockPos blockPos, BlockState blockState, MotherBlockEntity e) {
        if (e.rootVulnerableTicks == 0) {
            e.setRootVulnerability(true);
        }

        if (e.rootVulnerableTicks > 0) {
            e.rootVulnerableTicks--;
        }
    }

    private Set<VulnerabelCursedRootedDirtBlockEntity> getVulnerabilities() {
        return getVulnerablePositions().stream().map(pos -> level.getBlockEntity(pos)).filter(VulnerabelCursedRootedDirtBlockEntity.class::isInstance).map(VulnerabelCursedRootedDirtBlockEntity.class::cast).collect(Collectors.toSet());
    }

    private Set<BlockPos> getVulnerablePositions() {
        return ((ServerLevel) this.level).getPoiManager().findAll(holder -> holder.is(ModVillage.VULNERABLE_ROOTS.getKey()), pos -> true, this.worldPosition, 20, PoiManager.Occupancy.ANY).collect(Collectors.toSet());
    }

    public void notifyDestroyedRoot(BlockPos pos) {
        Set<VulnerabelCursedRootedDirtBlockEntity> vulnerabilities = getVulnerabilities();
        if(checkState(vulnerabilities)) {
            this.rootVulnerableTicks = 100;
            this.setRootVulnerability(false);
            this.bossEvent.setProgress(vulnerabilities.stream().filter(VulnerabelCursedRootedDirtBlockEntity::isActive).count() / (float) vulnerabilities.size());
        } else {
            this.bossEvent.setProgress(0);
        }
    }

    private void setRootVulnerability(boolean vulnerable) {
        getVulnerabilities().stream().filter(VulnerabelCursedRootedDirtBlockEntity::isActive).forEach(e -> e.setVulnerability(vulnerable));
        if (vulnerable) {
            this.bossEvent.setColor(BossEvent.BossBarColor.RED);
        } else {
            this.bossEvent.setColor(BossEvent.BossBarColor.WHITE);
        }
    }

    public void attack() {
        if (isVulnerable) {
            // damage
        }
    }

    private void destroy() {
        getVulnerablePositions().forEach(pos -> level.setBlockAndUpdate(pos, ModBlocks.CURSED_ROOTED_DIRT.get().defaultBlockState()));
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        this.bossEvent.removeAllPlayers();
    }

    public void addPlayer(ServerPlayer player) {
        this.bossEvent.addPlayer(player);
    }

    private boolean checkState(Set<VulnerabelCursedRootedDirtBlockEntity> vulnerabilities) {
        if (vulnerabilities.stream().noneMatch(VulnerabelCursedRootedDirtBlockEntity::isActive)) {
            setVulnerable();
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
            return false;
        }
        return true;
    }

    private void setVulnerable() {
        this.isVulnerable = true;
    }

    public boolean isVulnerable() {
        return this.isVulnerable;
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putBoolean("isVulnerable", this.isVulnerable);
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        this.isVulnerable = tag.getBoolean("isVulnerable");
    }
}
