package de.teamlapen.vampirism.blockentity;

import de.teamlapen.vampirism.core.ModTags;
import de.teamlapen.vampirism.core.ModTiles;
import de.teamlapen.vampirism.world.VampirismWorld;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;


public class FogDiffuserBlockEntity extends BlockEntity {

    @NotNull
    private State state = State.IDLE;
    private boolean activated = false;
    private float bootProgress = 0;

    public FogDiffuserBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModTiles.FOG_DIFFUSER.get(), pPos, pBlockState);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.putString("state", this.state.name());
        pTag.putFloat("bootProgress", this.bootProgress);
        pTag.putBoolean("activated", this.activated);
    }

    @Override
    public void load(@NotNull CompoundTag pTag) {
        super.load(pTag);
        this.state = State.valueOf(pTag.getString("state"));
        this.bootProgress = pTag.getFloat("bootProgress");
        this.activated = pTag.getBoolean("activated");
    }

    protected int getRange() {
        return (int) (2.5 * 16);
    }

    public @NotNull State getState() {
        return state;
    }

    public float getBootProgress() {
        return bootProgress;
    }

    protected AABB getArea() {
        return getArea(this.getRange());
    }

    protected AABB getArea(int range) {
        return new AABB(this.worldPosition.offset(-range, -range, -range), this.worldPosition.offset(range, range, range));
    }

    public static void tick(Level level, BlockPos pos, BlockState state, FogDiffuserBlockEntity blockEntity) {
        switch (blockEntity.state) {
            case IDLE -> {
                if (blockEntity.activated) {
                    blockEntity.state = State.BOOTING;
                    blockEntity.bootProgress = 0;
                }
            }
            case BOOTING -> {
                if (level.getGameTime() % 128 == 0) {
                    blockEntity.bootProgress += 0.1;
                    if (blockEntity.bootProgress >= 1) {
                        blockEntity.state = State.ACTIVE;
                    }
                    blockEntity.updateFogArea(level);
                }
            }
            case ACTIVE -> {
            }
        }
    }

    public void updateFogArea(Level level) {
        VampirismWorld.getOpt(level).ifPresent(w -> w.updateTemporaryArtificialFog(this.worldPosition, switch (this.state) {
            case BOOTING -> getArea((int) (this.getRange() * this.bootProgress));
            case ACTIVE -> getArea();
            default -> null;
        }));
    }

    public void interact(ItemStack itemInHand) {
        if (!this.activated && itemInHand.is(ModTags.Items.PURE_BLOOD)) {
            this.activated = true;
            itemInHand.shrink(1);
        }
    }

    public enum State {
        IDLE,
        BOOTING,
        ACTIVE
    }
}
