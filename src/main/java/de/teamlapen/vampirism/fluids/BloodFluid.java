package de.teamlapen.vampirism.fluids;

import de.teamlapen.vampirism.core.ModFluids;
import de.teamlapen.vampirism.core.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;

import org.jetbrains.annotations.NotNull;


public class BloodFluid extends Fluid {
    public BloodFluid() {
        super();
    }

    @Override
    public int getAmount(@NotNull FluidState fluidState) {
        return 0;
    }

    @NotNull
    @Override
    public Item getBucket() {
        return ModItems.BLOOD_BUCKET.get();
    }

    @Override
    public float getHeight(@NotNull FluidState fluidState, @NotNull BlockGetter blockReader, @NotNull BlockPos blockPos) {
        return 0;
    }

    @Override
    public float getOwnHeight(@NotNull FluidState fluidState) {
        return 0;
    }

    @NotNull
    @Override
    public VoxelShape getShape(@NotNull FluidState fluidState, @NotNull BlockGetter blockReader, @NotNull BlockPos blockPos) {
        return Shapes.block();
    }

    @Override
    public int getTickDelay(@NotNull LevelReader worldReader) {
        return 5;
    }

    @Override
    public boolean isSource(@NotNull FluidState state) {
        return false;
    }

    @Override
    protected boolean canBeReplacedWith(@NotNull FluidState fluidState, @NotNull BlockGetter blockReader, @NotNull BlockPos blockPos, @NotNull Fluid fluid, @NotNull Direction direction) {
        return false;
    }

    @NotNull
    @Override
    protected BlockState createLegacyBlock(@NotNull FluidState state) {
        return Blocks.AIR.defaultBlockState();
    }

    @Override
    protected float getExplosionResistance() {
        return 100.0F;
    }

    @NotNull
    @Override
    protected Vec3 getFlow(@NotNull BlockGetter blockReader, @NotNull BlockPos blockPos, @NotNull FluidState fluidState) {
        return Vec3.ZERO;
    }

    @NotNull
    @Override
    public  FluidType getFluidType() {
        return ModFluids.BLOOD_TYPE.get();
    }
}
