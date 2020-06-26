package de.teamlapen.vampirism.fluids;

import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.item.Rarity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.fluids.FluidAttributes;

import javax.annotation.Nonnull;

public class ImpureBloodFluid extends VampirismFluid {

    public ImpureBloodFluid() {
        super("impure_blood");
    }

    @Nonnull
    @Override
    public VoxelShape func_215664_b(@Nonnull FluidState fluidState, @Nonnull IBlockReader blockReader, @Nonnull BlockPos blockPos) {
        return VoxelShapes.fullCube();
    }

    @Override
    public float getActualHeight(@Nonnull FluidState fluidState, @Nonnull IBlockReader blockReader, @Nonnull BlockPos blockPos) {
        return 0;
    }

    @Nonnull
    @Override
    public Item getFilledBucket() {
        return ModItems.impure_blood_bucket;
    }

    @Override
    public float getHeight(@Nonnull FluidState fluidState) {
        return 0;
    }

    @Override
    public int getLevel(@Nonnull FluidState fluidState) {
        return 0;
    }


    @Override
    public int getTickRate(@Nonnull IWorldReader worldReader) {
        return 5;
    }

    @Override
    public boolean isSource(@Nonnull FluidState state) {
        return false;
    }

    @Override
    protected boolean canDisplace(@Nonnull FluidState fluidState, @Nonnull IBlockReader blockReader, @Nonnull BlockPos blockPos, @Nonnull Fluid fluid, @Nonnull Direction direction) {
        return false;
    }

    @Nonnull
    @Override
    protected FluidAttributes createAttributes() {
        return FluidAttributes.builder(new ResourceLocation(REFERENCE.MODID, "block/impure_blood_still"), new ResourceLocation(REFERENCE.MODID, "block/impure_blood_flow")).color(0xEEFF1111).density(1300).temperature(309).viscosity(3000).rarity(Rarity.UNCOMMON).build(this);
    }

    @Nonnull
    @Override
    protected BlockState getBlockState(@Nonnull FluidState state) {
        return Blocks.AIR.getDefaultState();
    }

    @Override
    protected float getExplosionResistance() {
        return 0;
    }

    @Nonnull
    @Override
    protected Vector3d getFlow(@Nonnull IBlockReader blockReader, @Nonnull BlockPos blockPos, @Nonnull FluidState fluidState) {
        return Vector3d.ZERO;
    }
}
