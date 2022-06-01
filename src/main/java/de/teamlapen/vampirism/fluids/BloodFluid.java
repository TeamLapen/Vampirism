package de.teamlapen.vampirism.fluids;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.core.ModItems;
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
import net.minecraftforge.fml.ModList;

import javax.annotation.Nonnull;


public class BloodFluid extends Fluid {
    public BloodFluid() {
        super();
    }

    @Override
    public int getAmount(@Nonnull FluidState fluidState) {
        return 0;
    }

    @Nonnull
    @Override
    public Item getBucket() {
        return ModItems.BLOOD_BUCKET.get();
    }

    @Override
    public float getHeight(@Nonnull FluidState fluidState, @Nonnull IBlockReader blockReader, @Nonnull BlockPos blockPos) {
        return 0;
    }

    @Override
    public float getOwnHeight(@Nonnull FluidState fluidState) {
        return 0;
    }

    @Nonnull
    @Override
    public VoxelShape getShape(@Nonnull FluidState fluidState, @Nonnull IBlockReader blockReader, @Nonnull BlockPos blockPos) {
        return VoxelShapes.block();
    }

    @Override
    public int getTickDelay(@Nonnull IWorldReader worldReader) {
        return 5;
    }

    @Override
    public boolean isSource(@Nonnull FluidState state) {
        return false;
    }

    @Override
    protected boolean canBeReplacedWith(@Nonnull FluidState fluidState, @Nonnull IBlockReader blockReader, @Nonnull BlockPos blockPos, @Nonnull Fluid fluid, @Nonnull Direction direction) {
        return false;
    }

    @Nonnull
    @Override
    protected FluidAttributes createAttributes() {
        boolean integrations = ModList.get().isLoaded(REFERENCE.INTEGRATIONS_MODID);
        return FluidAttributes.builder(new ResourceLocation(REFERENCE.MODID, "block/blood_still"), new ResourceLocation(REFERENCE.MODID, "block/blood_flow")).translationKey(integrations ? "fluid.vampirism.blood.vampirism" : "fluid.vampirism.blood").color(0xEEFF1111).density(1300).temperature(309).viscosity(3000).rarity(Rarity.UNCOMMON).build(this);
    }

    @Nonnull
    @Override
    protected BlockState createLegacyBlock(@Nonnull FluidState state) {
        return Blocks.AIR.defaultBlockState();
    }

    @Override
    protected float getExplosionResistance() {
        return 100.0F;
    }

    @Nonnull
    @Override
    protected Vector3d getFlow(@Nonnull IBlockReader blockReader, @Nonnull BlockPos blockPos, @Nonnull FluidState fluidState) {
        return Vector3d.ZERO;
    }
}
