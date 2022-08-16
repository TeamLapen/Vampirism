package de.teamlapen.vampirism.misc;

import de.teamlapen.vampirism.entity.IVampirismBoat;
import de.teamlapen.vampirism.entity.VampirismBoatEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;

import javax.annotation.Nonnull;

public class VampirismDispenseBoatBehavior extends DefaultDispenseItemBehavior {
    private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();
    private final IVampirismBoat.BoatType type;
    private final boolean isChestBoat;

    public VampirismDispenseBoatBehavior(IVampirismBoat.BoatType type) {
        this(type, false);
    }

    public VampirismDispenseBoatBehavior(IVampirismBoat.BoatType type, boolean isChestBoat) {
        this.type = type;
        this.isChestBoat = isChestBoat;
    }

    /**
     * from {@link net.minecraft.core.dispenser.BoatDispenseItemBehavior#execute(net.minecraft.core.BlockSource, net.minecraft.world.item.ItemStack)}
     * TODO 1.20 recheck
     */
    @Nonnull
    public ItemStack execute(BlockSource p_123375_, ItemStack p_123376_) {
        Direction direction = p_123375_.getBlockState().getValue(DispenserBlock.FACING);
        Level level = p_123375_.getLevel();
        double d0 = p_123375_.x() + (double)((float)direction.getStepX() * 1.125F);
        double d1 = p_123375_.y() + (double)((float)direction.getStepY() * 1.125F);
        double d2 = p_123375_.z() + (double)((float)direction.getStepZ() * 1.125F);
        BlockPos blockpos = p_123375_.getPos().relative(direction);
        VampirismBoatEntity boat = this.isChestBoat ? new VampirismBoatEntity(level, d0, d1, d2) : new VampirismBoatEntity(level, d0, d1, d2); // class changed
        boat.setType(this.type);
        boat.setYRot(direction.toYRot());
        double d3;
        if (boat.canBoatInFluid(level.getFluidState(blockpos))) {
            d3 = 1.0D;
        } else {
            if (!level.getBlockState(blockpos).isAir() || !boat.canBoatInFluid(level.getFluidState(blockpos.below()))) {
                return this.defaultDispenseItemBehavior.dispense(p_123375_, p_123376_);
            }

            d3 = 0.0D;
        }

        boat.setPos(d0, d1 + d3, d2);
        level.addFreshEntity(boat);
        p_123376_.shrink(1);
        return p_123376_;
    }

    protected void playSound(BlockSource p_82485_1_) {
        p_82485_1_.getLevel().levelEvent(1000, p_82485_1_.getPos(), 0);
    }
}
