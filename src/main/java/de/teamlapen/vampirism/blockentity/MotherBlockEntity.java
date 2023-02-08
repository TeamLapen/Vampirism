package de.teamlapen.vampirism.blockentity;

import de.teamlapen.vampirism.blocks.mother.IRemainsBlock;
import de.teamlapen.vampirism.core.ModTiles;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MotherBlockEntity extends BlockEntity {

    private final ServerBossEvent bossEvent = new ServerBossEvent(Component.translatable("block.vampirism.mother"), BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.NOTCHED_10);
    private boolean canBeDestroyed = false;
    private boolean isFrozen = false;
    private int freezeTimer = 0;

    public MotherBlockEntity(BlockPos pos, BlockState state) {
        super(ModTiles.MOTHER.get(), pos, state);
        bossEvent.setProgress(1);
    }

    public static void serverTick(Level level, BlockPos blockPos, BlockState blockState, MotherBlockEntity e) {
        if (e.isFrozen && e.freezeTimer-- <= 0) {
            e.unFreezeFight(level, blockPos, blockState);
        }
    }

    public void updateFightStatus() {
        List<BlockState> vulnerabilities = ((IRemainsBlock) this.getBlockState().getBlock()).getConnector().getVulnerabilities(this.level, this.worldPosition).toList();
        long remainingVulnerabilities = vulnerabilities.stream().filter(state -> ((IRemainsBlock) (state.getBlock())).isVulnerable(state)).count();
        if (remainingVulnerabilities > 0) {
            this.bossEvent.setProgress(remainingVulnerabilities / (float) vulnerabilities.size());
        } else {
            this.bossEvent.setProgress(0);
            this.endFight();
        }
    }

    private void endFight() {
        this.bossEvent.removeAllPlayers();
        this.canBeDestroyed = true;
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        this.bossEvent.removeAllPlayers();
    }

    private void addPlayer(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            this.bossEvent.addPlayer(serverPlayer);
        }
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
        tag.putBoolean("isVulnerable", this.canBeDestroyed);
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        this.canBeDestroyed = tag.getBoolean("isVulnerable");
    }


    public boolean isCanBeDestroyed() {
        return this.canBeDestroyed;
    }

    public void onVulnerabilityHit(ServerPlayer p, boolean destroyed) {
        addPlayer(p);
        updateFightStatus();
        if (destroyed && !this.canBeDestroyed) {
            freezeFight();
        }
    }

    private void freezeFight() {
        this.isFrozen = true;
        ((IRemainsBlock) getBlockState().getBlock()).getConnector().foreach(this.level, this.worldPosition, (level, pos, state) -> ((IRemainsBlock) state.getBlock()).freeze(level, pos, state));
        this.bossEvent.setColor(BossEvent.BossBarColor.WHITE);
    }

    private void unFreezeFight(Level level, BlockPos blockPos, BlockState blockState) {
        this.isFrozen = false;
        this.freezeTimer = 20 * 10;
        ((IRemainsBlock) getBlockState().getBlock()).getConnector().foreach(level, blockPos, (level1, pos, state) -> ((IRemainsBlock) state.getBlock()).unFreeze(level1, pos, state));
        this.bossEvent.setColor(BossEvent.BossBarColor.RED);
    }
}
