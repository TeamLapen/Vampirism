package de.teamlapen.vampirism.fluids;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.core.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fml.ModList;

import javax.annotation.Nonnull;


public class BloodFluid extends VampirismFluid {
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
    public float getHeight(@Nonnull FluidState fluidState, @Nonnull BlockGetter blockReader, @Nonnull BlockPos blockPos) {
        return 0;
    }

    @Override
    public float getOwnHeight(@Nonnull FluidState fluidState) {
        return 0;
    }

    @Nonnull
    @Override
    public VoxelShape getShape(@Nonnull FluidState fluidState, @Nonnull BlockGetter blockReader, @Nonnull BlockPos blockPos) {
        return Shapes.block();
    }

    @Override
    public int getTickDelay(@Nonnull LevelReader worldReader) {
        return 5;
    }

    @Override
    public boolean isSource(@Nonnull FluidState state) {
        return false;
    }

    @Override
    protected boolean canBeReplacedWith(@Nonnull FluidState fluidState, @Nonnull BlockGetter blockReader, @Nonnull BlockPos blockPos, @Nonnull Fluid fluid, @Nonnull Direction direction) {
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
    protected Vec3 getFlow(@Nonnull BlockGetter blockReader, @Nonnull BlockPos blockPos, @Nonnull FluidState fluidState) {
        return Vec3.ZERO;
    }
}
