package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.core.ModSounds;
import de.teamlapen.vampirism.core.ModStats;
import de.teamlapen.vampirism.core.ModTiles;
import de.teamlapen.vampirism.tileentity.AlchemicalCauldronTileEntity;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Random;


public class AlchemicalCauldronBlock extends AbstractFurnaceBlock {
    public static final String regName = "alchemical_cauldron";
    /**
     * 0: No liquid,
     * 1: Liquid,
     * 2: Boiling liquid
     */
    public static final IntegerProperty LIQUID = IntegerProperty.create("liquid", 0, 2);
    protected static final VoxelShape cauldronShape = makeShape();

    private static VoxelShape makeShape() {
        VoxelShape a = Block.box(2, 0, 2, 14, 9, 14);
        VoxelShape b = Block.box(1, 9, 1, 15, 13, 15);
        VoxelShape c = Block.box(2, 13, 2, 14, 14, 14);
        return Shapes.or(a, b, c);
    }

    public AlchemicalCauldronBlock() {
        super(Block.Properties.of(Material.METAL).strength(4f).noOcclusion());
        this.registerDefaultState(this.stateDefinition.any().setValue(LIQUID, 0).setValue(FACING, Direction.NORTH).setValue(LIT, false));
        this.setRegistryName(REFERENCE.MODID, regName);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, Random rng) {
        super.animateTick(state, world, pos, rng);
        if (state.getValue(LIQUID) == 2) {
            world.playLocalSound(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, ModSounds.boiling, SoundSource.BLOCKS, 0.05F, 1, false);
        }
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_153212_, BlockState p_153213_, BlockEntityType<T> p_153214_) {
        return p_153212_.isClientSide() ? null : createTickerHelper(p_153214_, ModTiles.alchemical_cauldron, AlchemicalCauldronTileEntity::serverTick);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AlchemicalCauldronTileEntity(pos, state);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return cauldronShape;
    }

    @Override
    public void setPlacedBy(Level world, BlockPos blockPos, BlockState blockState, LivingEntity entity, ItemStack stack) {
        super.setPlacedBy(world, blockPos, blockState, entity, stack);
        BlockEntity tile = world.getBlockEntity(blockPos);
        if (entity instanceof Player && tile instanceof AlchemicalCauldronTileEntity) {
            ((AlchemicalCauldronTileEntity) tile).setOwnerID((Player) entity);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIT, FACING, LIQUID);
    }

    @Override
    protected void openContainer(Level world, BlockPos blockPos, Player playerEntity) {
        BlockEntity tile = world.getBlockEntity(blockPos);
        if (tile instanceof AlchemicalCauldronTileEntity) {
            playerEntity.openMenu((MenuProvider) tile);
            playerEntity.awardStat(ModStats.interact_alchemical_cauldron);
        }
    }
}
