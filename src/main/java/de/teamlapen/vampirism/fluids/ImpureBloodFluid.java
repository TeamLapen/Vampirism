package de.teamlapen.vampirism.fluids;

import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.Item;
import net.minecraft.item.Rarity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.fluids.FluidAttributes;

public class ImpureBloodFluid extends VampirismFluid {
    public ImpureBloodFluid() {
        super("impure_blood");
    }

    @Override
    protected FluidAttributes createAttributes(Fluid p_createAttributes_1_) {
        return FluidAttributes.builder("impure_blood", new ResourceLocation(REFERENCE.MODID, "blocks/impure_blood_still"), new ResourceLocation(REFERENCE.MODID, "blocks/impure_blood_flow")).color(0xEEFF1111).density(1300).temperature(309).viscosity(3000).rarity(Rarity.UNCOMMON).build();
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return null;
    }

    @Override
    public Item getFilledBucket() {
        return null;
    }

    @Override
    protected boolean func_215665_a(IFluidState p_215665_1_, IBlockReader p_215665_2_, BlockPos p_215665_3_, Fluid p_215665_4_, Direction p_215665_5_) {
        return false;
    }

    @Override
    protected Vec3d func_215663_a(IBlockReader p_215663_1_, BlockPos p_215663_2_, IFluidState p_215663_3_) {
        return null;
    }

    @Override
    public int getTickRate(IWorldReader p_205569_1_) {
        return 0;
    }

    @Override
    protected float getExplosionResistance() {
        return 0;
    }

    @Override
    public float func_215662_a(IFluidState p_215662_1_, IBlockReader p_215662_2_, BlockPos p_215662_3_) {
        return 0;
    }

    @Override
    public float func_223407_a(IFluidState p_223407_1_) {
        return 0;
    }

    @Override
    protected BlockState getBlockState(IFluidState state) {
        return null;
    }

    @Override
    public boolean isSource(IFluidState state) {
        return false;
    }

    @Override
    public int getLevel(IFluidState p_207192_1_) {
        return 0;
    }

    @Override
    public VoxelShape func_215664_b(IFluidState p_215664_1_, IBlockReader p_215664_2_, BlockPos p_215664_3_) {
        return null;
    }
}
