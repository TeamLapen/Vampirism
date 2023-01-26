package de.teamlapen.vampirism.blockentity;

import de.teamlapen.vampirism.blocks.RemainsBlock;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public class MotherBlockEntity extends BlockEntity {

    private final ServerBossEvent bossEvent = new ServerBossEvent(Component.translatable("block.vampirism.mother"), BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.NOTCHED_10);

    public MotherBlockEntity(BlockPos pos, BlockState state) {
        super(ModTiles.MOTHER.get(), pos, state);
        bossEvent.setProgress(1);
    }

    public static void serverTick(Level level, BlockPos blockPos, BlockState blockState, MotherBlockEntity e) {
    }


    private List<RemainsBlock> getVulnerabilities() {
        return getVulnerablePositions().stream().map(pos -> level.getBlockState(pos).getBlock()).filter(RemainsBlock.class::isInstance).map(RemainsBlock.class::cast).collect(Collectors.toList()); //Must collect to list to be able to count multiple identical block instances
    }

    private List<BlockPos> getVulnerablePositions() {
        return ((ServerLevel) this.level).getPoiManager().findAll(holder -> holder.is(ModVillage.VULNERABLE_REMAINS.getKey()), pos -> true, this.worldPosition, 20, PoiManager.Occupancy.ANY).collect(Collectors.toList());
    }


    public void updateFightStatus(){
        List<RemainsBlock> vulnerabilities = getVulnerabilities();
        long remainingVulnerabilities = vulnerabilities.stream().filter(RemainsBlock::isVulnerable).count();
        if(remainingVulnerabilities > 0){
            this.bossEvent.setProgress(remainingVulnerabilities / (float) vulnerabilities.size());
        }
        else{
            this.bossEvent.setProgress(0);
            this.bossEvent.removeAllPlayers();
            this.dissolveStructure();
        }

    }



    private void dissolveStructure() {
        getVulnerablePositions().forEach(pos -> level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState())); //TODO remove non vulnerable remains
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        this.bossEvent.removeAllPlayers();
    }

    public void addPlayer(Player player) {
        if(player instanceof ServerPlayer serverPlayer) this.bossEvent.addPlayer(serverPlayer);
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
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
    }
}
