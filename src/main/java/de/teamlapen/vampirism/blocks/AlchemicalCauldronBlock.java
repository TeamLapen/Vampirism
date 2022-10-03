package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.blockentity.AlchemicalCauldronBlockEntity;
import de.teamlapen.vampirism.core.ModSounds;
import de.teamlapen.vampirism.core.ModStats;
import de.teamlapen.vampirism.core.ModTiles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class AlchemicalCauldronBlock extends AbstractFurnaceBlock {
    /**
     * 0: No liquid,
     * 1: Liquid,
     * 2: Boiling liquid
     */
    public static final IntegerProperty LIQUID = IntegerProperty.create("liquid", 0, 2);
    protected static final VoxelShape cauldronShape = makeShape();

    private static @NotNull VoxelShape makeShape() {
        VoxelShape a = Block.box(2, 0, 2, 14, 9, 14);
        VoxelShape b = Block.box(1, 9, 1, 15, 13, 15);
        VoxelShape c = Block.box(2, 13, 2, 14, 14, 14);
        return Shapes.or(a, b, c);
    }

    public AlchemicalCauldronBlock() {
        super(Block.Properties.of(Material.METAL).strength(4f).noOcclusion());
        this.registerDefaultState(this.stateDefinition.any().setValue(LIQUID, 0).setValue(FACING, Direction.NORTH).setValue(LIT, false));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void animateTick(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos, @NotNull RandomSource rng) {
        super.animateTick(state, world, pos, rng);
        if (state.getValue(LIQUID) == 2) {
            world.playLocalSound(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, ModSounds.BOILING.get(), SoundSource.BLOCKS, 0.05F, 1, false);
        }
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level p_153212_, @NotNull BlockState p_153213_, @NotNull BlockEntityType<T> p_153214_) {
        return p_153212_.isClientSide() ? null : createTickerHelper(p_153214_, ModTiles.ALCHEMICAL_CAULDRON.get(), AlchemicalCauldronBlockEntity::serverTick);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new AlchemicalCauldronBlockEntity(pos, state);
    }

    @NotNull
    @Override
    public VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter worldIn, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return cauldronShape;
    }

    @Override
    public void setPlacedBy(@NotNull Level world, @NotNull BlockPos blockPos, @NotNull BlockState blockState, @NotNull LivingEntity entity, @NotNull ItemStack stack) {
        super.setPlacedBy(world, blockPos, blockState, entity, stack);
        BlockEntity tile = world.getBlockEntity(blockPos);
        if (entity instanceof Player && tile instanceof AlchemicalCauldronBlockEntity cauldronBlockEntity) {
            cauldronBlockEntity.setOwnerID((Player) entity);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
        builder.add(LIT, FACING, LIQUID);
    }

    @Override
    protected void openContainer(@NotNull Level world, @NotNull BlockPos blockPos, @NotNull Player playerEntity) {
        BlockEntity tile = world.getBlockEntity(blockPos);
        if (tile instanceof AlchemicalCauldronBlockEntity) {
            playerEntity.openMenu((MenuProvider) tile);
            playerEntity.awardStat(ModStats.interact_alchemical_cauldron);
        }
    }
}
