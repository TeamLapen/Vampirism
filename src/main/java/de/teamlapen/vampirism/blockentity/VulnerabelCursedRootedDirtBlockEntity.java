package de.teamlapen.vampirism.blockentity;

import de.teamlapen.vampirism.blocks.VulnerableCursedRootedDirtBlock;
import de.teamlapen.vampirism.core.ModTiles;
import de.teamlapen.vampirism.core.ModVillage;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class VulnerabelCursedRootedDirtBlockEntity extends BlockEntity {

    private int invulnerableTicks;
    private int health = 5;
    private BlockPos motherPos;

    public VulnerabelCursedRootedDirtBlockEntity(BlockPos pos, BlockState state) {
        super(ModTiles.VULNERABLE_CURSED_ROOTED_DIRT.get(), pos, state);
    }

    public void setVulnerability(boolean isVulnerable) {
        getLevel().setBlock(this.worldPosition, getBlockState().setValue(VulnerableCursedRootedDirtBlock.IS_INVULNERABLE, !isVulnerable), 3);
    }

    public boolean isActive() {
        return getBlockState().getValue(VulnerableCursedRootedDirtBlock.IS_ACTIVE);
    }

    private Optional<MotherBlockEntity> getMother() {
        if (motherPos == null) {
            ((ServerLevel) this.level).getPoiManager().find((poi) -> poi.is(ModVillage.MOTHER.getKey()),(pos) -> true, this.worldPosition, 10, PoiManager.Occupancy.ANY).ifPresent((pos) -> {
                motherPos = pos;
            });
            if (motherPos == null) {
                this.level.setBlock(this.worldPosition, this.getBlockState().setValue(VulnerableCursedRootedDirtBlock.IS_ACTIVE, false), 3);
            }
        }
        return Optional.ofNullable(motherPos).map(pos -> {
            var blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof MotherBlockEntity mother) {
                return mother;
            }
            return null;
        });
    }

    private void destroy() {
        this.level.setBlock(this.worldPosition, this.getBlockState().setValue(VulnerableCursedRootedDirtBlock.IS_ACTIVE, false), 3);
        getMother().ifPresent(mother -> mother.notifyDestroyedRoot(this.worldPosition));
    }

    public void attacked(@NotNull BlockState state, @NotNull ServerPlayer player) {
        if (state.getValue(VulnerableCursedRootedDirtBlock.IS_INVULNERABLE)) return;
        if (!state.getValue(VulnerableCursedRootedDirtBlock.IS_ACTIVE)) return;
        this.getMother().ifPresent(mother -> mother.addPlayer(player));
        if (health-- <= 0) {
            destroy();
        }
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("health", this.health);
        tag.putInt("invulnerableTicks", this.invulnerableTicks);
        if (this.motherPos != null) {
            tag.putIntArray("motherPos", new int[]{this.motherPos.getX(), this.motherPos.getY(), this.motherPos.getZ()});
        }
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        this.health = tag.getInt("health");
        this.invulnerableTicks = tag.getInt("invulnerableTicks");
        if (tag.contains("motherPos")) {
            int[] pos = tag.getIntArray("motherPos");
            this.motherPos = new BlockPos(pos[0], pos[1], pos[2]);
        }
    }
}
