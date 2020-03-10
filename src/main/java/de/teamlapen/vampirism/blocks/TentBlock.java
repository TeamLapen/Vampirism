package de.teamlapen.vampirism.blocks;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;
import com.mojang.datafixers.util.Pair;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.network.PlayEventPacket;
import de.teamlapen.vampirism.player.VampirismPlayer;
import de.teamlapen.vampirism.player.hunter.HunterPlayer;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.DiggingParticle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.*;
import net.minecraftforge.common.extensions.IForgeDimension;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.BiConsumer;

import static net.minecraft.state.properties.BlockStateProperties.HORIZONTAL_FACING;

/**
 * Part of a 2x2 block tent
 * Position property contains the position within the 4 block arrangement
 */
public class TentBlock extends VampirismBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final IntegerProperty POSITION = IntegerProperty.create("position", 0, 3);
    private static final String name = "tent";
    private static final Table<Direction, Integer, VoxelShape> shapes;
    private static final Map<PlayerEntity.SleepResult, ITextComponent> sleepResults;
    private static final Table<Integer, Direction, Pair<Double, Double>> offsets;

    public TentBlock() {
        this(name);
    }

    protected TentBlock(String name) {
        super(name, Properties.create(Material.WOOL).hardnessAndResistance(0.6f).sound(SoundType.CLOTH));
        this.setDefaultState(this.getStateContainer().getBaseState().with(FACING, Direction.NORTH).with(POSITION, 0).with(BedBlock.OCCUPIED, false));
    }

    @Override
    public boolean isNormalCube(BlockState state, @Nonnull IBlockReader worldIn, @Nonnull BlockPos pos) {
        return false;
    }

    @Override
    public ActionResultType onBlockActivated(BlockState blockState, World world, final BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockRayTraceResult rayTraceResult) {
        if (world.isRemote()) return ActionResultType.SUCCESS;
        if (HunterPlayer.getOpt(playerEntity).map(VampirismPlayer::getLevel).orElse(0) == 0) {
            playerEntity.sendStatusMessage(new TranslationTextComponent("text.vampirism.tent.cant_use"), true);
            return ActionResultType.SUCCESS;
        }
        IForgeDimension.SleepResult sleepResult = world.getDimension().canSleepAt(playerEntity, blockPos);
        if (sleepResult != IForgeDimension.SleepResult.BED_EXPLODES) {
            if (sleepResult == IForgeDimension.SleepResult.DENY) return ActionResultType.SUCCESS;
            if (blockState.get(BedBlock.OCCUPIED)) {
                playerEntity.sendStatusMessage(new TranslationTextComponent("text.vampirism.tent.occupied"), true);
                return ActionResultType.SUCCESS;
            } else {
                playerEntity.trySleep(blockPos).ifLeft(sleepResult1 -> {
                    if (sleepResult1 != null) {
                        playerEntity.sendStatusMessage(sleepResults.getOrDefault(sleepResult1, sleepResult1.getMessage()), true);
                    }
                }).ifRight(u -> {
                    this.setBedOccupied(blockState, world, blockPos, null, true);
                    setTentSleepPosition(playerEntity, blockPos, playerEntity.world.getBlockState(blockPos).get(POSITION), playerEntity.world.getBlockState(blockPos).get(HORIZONTAL_FACING));
                });
                return ActionResultType.SUCCESS;
            }
        } else {
            world.removeBlock(blockPos, false);
            BlockPos blockPos1 = blockPos.offset(blockState.get(HORIZONTAL_FACING).getOpposite());
            if (world.getBlockState(blockPos1).getBlock() == this) {
                world.removeBlock(blockPos1, false);
            }
            world.createExplosion(null, DamageSource.netherBedExplosion(), (double) blockPos.getX() + 0.5D, (double) blockPos.getY() + 0.5D, (double) blockPos.getZ() + 0.5D, 5.0F, true, Explosion.Mode.DESTROY);
            return ActionResultType.SUCCESS;
        }
    }

    @Override
    public boolean isValidPosition(BlockState blockState, IWorldReader worldReader, BlockPos blockPos) {
        return worldReader.getBlockState(blockPos).isAir(worldReader,blockPos);
    }

    @Override
    public void setBedOccupied(BlockState state, IWorldReader world, BlockPos pos, LivingEntity sleeper, boolean occupied) {
        if (world instanceof IWorldWriter) {
            forWholeTent(pos, state, ((direction, blockPos) -> {
                BlockState blockState = world.getBlockState(blockPos);
                if(blockState.getBlock() instanceof TentBlock){
                    ((IWorldWriter & IWorldReader)world).setBlockState(pos, world.getBlockState(pos).with(BlockStateProperties.OCCUPIED, occupied), 2);
                }
            }));
        }
    }


    @Override
    public void onFallenUpon(World worldIn, BlockPos pos, Entity entityIn, float fallDistance) {
        super.onFallenUpon(worldIn, pos, entityIn, fallDistance * 0.7F);
    }

    @Override
    public void onLanded(IBlockReader worldIn, Entity entityIn) {
        if (entityIn.isShiftKeyDown()) {
            super.onLanded(worldIn, entityIn);
        } else {
            Vec3d vec3d = entityIn.getMotion();
            if (vec3d.y < 0.0D) {
                double d0 = entityIn instanceof LivingEntity ? 1.0D : 0.8D;
                entityIn.setMotion(vec3d.x, -vec3d.y * (double)0.33F * d0, vec3d.z);
            }
        }

    }

    @Override
    public boolean isBed(BlockState state, IBlockReader world, BlockPos pos, @Nullable Entity player) {
        return true;
    }

    @Override
    public Direction getBedDirection(BlockState state, IWorldReader world, BlockPos pos) {
        switch (state.get(POSITION)){
            case 0:
            case 3:
                return state.get(HORIZONTAL_FACING).getOpposite();
            default:
                return state.get(HORIZONTAL_FACING);
        }
    }

    @Override
    public void onBlockHarvested(World world, @Nonnull BlockPos blockPos, BlockState blockState, @Nonnull PlayerEntity playerEntity) {
        forWholeTent(blockPos,blockState, (direction, blockPos1) -> {
            VampirismMod.dispatcher.sendToAllAround(new PlayEventPacket(1,blockPos1, Block.getStateId(world.getBlockState(blockPos1))),world.getDimension().getType(), blockPos1.getX(), blockPos1.getY(), blockPos1.getZ(), 64);
            world.destroyBlock(blockPos1, true);
            spawnDrops(world.getBlockState(blockPos1), world, blockPos1, null, playerEntity, playerEntity.getHeldItemMainhand());
        });
    }



    @Override
    public void onReplaced(BlockState oldState, @Nonnull World world, @Nonnull BlockPos blockPos, @Nonnull BlockState newState, boolean p_196243_5_) {
        super.onReplaced(oldState, world, blockPos, newState, p_196243_5_);
        forWholeTent(blockPos,oldState, (direction, blockPos1) -> {
            world.destroyBlock(blockPos1, true);
        });
    }

    private void forWholeTent(BlockPos blockPos, BlockState blockState, BiConsumer<Direction, BlockPos> consumer){
        BlockPos main = blockPos;
        Direction dir = blockState.get(FACING);
        int p = blockState.get(POSITION);
        if (p == 0) {
            dir = dir.getOpposite();
        } else if (p == 1) {
            main = blockPos.offset(dir.rotateY());
        } else if (p == 2) {
            main = blockPos.offset(dir.rotateY()).offset(dir.getOpposite());
        } else if (p == 3) {
            main = blockPos.offset(dir);
            dir = dir.getOpposite();
        }
        consumer.accept(dir, blockPos);
        BlockPos cur = main;
        if (cur != blockPos) consumer.accept(dir, cur);
        cur = main.offset(dir);
        if (cur != blockPos) consumer.accept(dir, cur);
        cur = main.offset(dir.rotateYCCW());
        if (cur != blockPos) consumer.accept(dir, cur);
        cur = main.offset(dir).offset(dir.rotateYCCW());
        if (cur != blockPos) consumer.accept(dir, cur);
    }


    @Nonnull
    @Override
    public VoxelShape getShape(BlockState blockState, IBlockReader blockReader, BlockPos blockPos, ISelectionContext context) {
        return shapes.get(blockState.get(FACING),blockState.get(POSITION));
    }

    @Override
    public boolean addDestroyEffects(BlockState state, World world, BlockPos pos, ParticleManager manager) {
        return true;
    }

    /**
     * copied from {@link net.minecraft.client.particle.ParticleManager#addBlockDestroyEffects(net.minecraft.util.math.BlockPos, net.minecraft.block.BlockState)} but which much lesser particles
     */
    public void spawnParticles(World world, BlockPos pos, BlockState state) {
        VoxelShape voxelshape = state.getShape(world, pos);
        voxelshape.forEachBox((p_199284_3_, p_199284_5_, p_199284_7_, p_199284_9_, p_199284_11_, p_199284_13_) -> {
            double d1 = Math.min(1.0D, p_199284_9_ - p_199284_3_);
            double d2 = Math.min(1.0D, p_199284_11_ - p_199284_5_);
            double d3 = Math.min(1.0D, p_199284_13_ - p_199284_7_);
            int i = Math.max(2, MathHelper.ceil(d1 / 0.25D));
            int j = Math.max(2, MathHelper.ceil(d2 / 0.25D));
            int k = Math.max(2, MathHelper.ceil(d3 / 0.25D));

            for (int l = 0; l < i / 2; ++l) {
                for (int i1 = 0; i1 < j / 2; ++i1) {
                    for (int j1 = 0; j1 < k / 2; ++j1) {
                        double d4 = ((double) l + 0.5D) / (double) i;
                        double d5 = ((double) i1 + 0.5D) / (double) j;
                        double d6 = ((double) j1 + 0.5D) / (double) k;
                        double d7 = d4 * d1 + p_199284_3_;
                        double d8 = d5 * d2 + p_199284_5_;
                        double d9 = d6 * d3 + p_199284_7_;
                        Minecraft.getInstance().particles.addEffect((new DiggingParticle(world, (double) pos.getX() + d7, (double) pos.getY() + d8, (double) pos.getZ() + d9, d4 - 0.5D, d5 - 0.5D, d6 - 0.5D, state)).setBlockPos(pos));
                    }
                }
            }

        });
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING, POSITION, BlockStateProperties.OCCUPIED);
    }

    public static void setTentSleepPosition(PlayerEntity player, BlockPos blockPos, int position, Direction facing) {
        player.setPosition(blockPos.getX() + offsets.get(position, facing).getFirst(), blockPos.getY() + 0.0625, blockPos.getZ() + offsets.get(position, facing).getSecond());
    }

    static {
        VoxelShape NORTH = makeShape();
        VoxelShape EAST = UtilLib.rotateShape(NORTH, UtilLib.RotationAmount.NINETY);
        VoxelShape SOUTH = UtilLib.rotateShape(NORTH, UtilLib.RotationAmount.HUNDRED_EIGHTY);
        VoxelShape WEST = UtilLib.rotateShape(NORTH, UtilLib.RotationAmount.TWO_HUNDRED_SEVENTY);
        VoxelShape BACK = makeShapeBack1();
        VoxelShape BACKMIRROR = makeShapeBack2();
        VoxelShape SOUTHR = VoxelShapes.or(SOUTH,BACK);
        VoxelShape NORTHL = VoxelShapes.or(NORTH,BACKMIRROR);

        ImmutableTable.Builder<Direction, Integer, VoxelShape> shapeBuilder = ImmutableTable.builder();
        shapeBuilder.put(Direction.NORTH, 0, NORTH);
        shapeBuilder.put(Direction.NORTH, 1, NORTH);
        shapeBuilder.put(Direction.NORTH, 2, NORTHL);
        shapeBuilder.put(Direction.NORTH, 3, UtilLib.rotateShape(SOUTHR, UtilLib.RotationAmount.HUNDRED_EIGHTY));
        shapeBuilder.put(Direction.EAST, 0, EAST);
        shapeBuilder.put(Direction.EAST, 1, EAST);
        shapeBuilder.put(Direction.EAST, 2, UtilLib.rotateShape(NORTHL, UtilLib.RotationAmount.NINETY));
        shapeBuilder.put(Direction.EAST, 3, UtilLib.rotateShape(SOUTHR, UtilLib.RotationAmount.TWO_HUNDRED_SEVENTY));
        shapeBuilder.put(Direction.SOUTH, 0, SOUTH);
        shapeBuilder.put(Direction.SOUTH, 1, SOUTH);
        shapeBuilder.put(Direction.SOUTH, 2, UtilLib.rotateShape(NORTHL, UtilLib.RotationAmount.HUNDRED_EIGHTY));
        shapeBuilder.put(Direction.SOUTH, 3, SOUTHR);
        shapeBuilder.put(Direction.WEST, 0, WEST);
        shapeBuilder.put(Direction.WEST, 1, WEST);
        shapeBuilder.put(Direction.WEST, 2, UtilLib.rotateShape(NORTHL, UtilLib.RotationAmount.TWO_HUNDRED_SEVENTY));
        shapeBuilder.put(Direction.WEST, 3, UtilLib.rotateShape(SOUTHR, UtilLib.RotationAmount.NINETY));
        shapes = shapeBuilder.build();

        ImmutableTable.Builder<Integer, Direction, Pair<Double, Double>> offsetsBuilder = ImmutableTable.builder();
        offsetsBuilder.put(0, Direction.NORTH, Pair.of(1.0, 1.6));
        offsetsBuilder.put(0, Direction.EAST, Pair.of(-0.6, 1.0));
        offsetsBuilder.put(0, Direction.SOUTH, Pair.of(0.0, -0.6));
        offsetsBuilder.put(0, Direction.WEST, Pair.of(1.6, 0.0));
        offsetsBuilder.put(1, Direction.NORTH, Pair.of(1.0, -0.6));
        offsetsBuilder.put(1, Direction.EAST, Pair.of(1.6, 1.0));
        offsetsBuilder.put(1, Direction.SOUTH, Pair.of(0.0, 1.6));
        offsetsBuilder.put(1, Direction.WEST, Pair.of(-0.6, 0.0));
        offsetsBuilder.put(2, Direction.NORTH, Pair.of(1.0, 0.4));
        offsetsBuilder.put(2, Direction.EAST, Pair.of(0.6, 1.0));
        offsetsBuilder.put(2, Direction.SOUTH, Pair.of(0.0, 0.6));
        offsetsBuilder.put(2, Direction.WEST, Pair.of(0.4, 0.0));
        offsetsBuilder.put(3, Direction.NORTH, Pair.of(1.0, 0.6));
        offsetsBuilder.put(3, Direction.EAST, Pair.of(0.4, 1.0));
        offsetsBuilder.put(3, Direction.SOUTH, Pair.of(0.0, 0.4));
        offsetsBuilder.put(3, Direction.WEST, Pair.of(0.6, 0.0));
        offsets = offsetsBuilder.build();

        ImmutableMap.Builder<PlayerEntity.SleepResult, ITextComponent> sleepBuilder = ImmutableMap.builder();
        sleepBuilder.put(PlayerEntity.SleepResult.NOT_POSSIBLE_NOW, new TranslationTextComponent("text.vampirism.tent.no_sleep"));
        sleepBuilder.put(PlayerEntity.SleepResult.TOO_FAR_AWAY, new TranslationTextComponent("text.vampirism.tent.too_far_away"));
        sleepBuilder.put(PlayerEntity.SleepResult.OBSTRUCTED, new TranslationTextComponent("text.vampirism.tent.obstructed"));
        sleepResults = sleepBuilder.build();
    }

    private static VoxelShape makeShape() {
        return VoxelShapes.or(
                Block.makeCuboidShape(0, 0, 0, 16, 1, 16),
                Block.makeCuboidShape(0.5, 1, 0, 1.4, 1.45, 16),
                Block.makeCuboidShape(0.9, 1.4, 0, 1.8, 1.85, 16),
                Block.makeCuboidShape(1.3, 1.8, 0, 2.2, 2.25, 16),
                Block.makeCuboidShape(1.7, 2.2, 0, 2.6, 2.65, 16),
                Block.makeCuboidShape(2.1, 2.6, 0, 3.0, 3.05, 16),
                Block.makeCuboidShape(2.5, 3.0, 0, 3.4, 3.45, 16),
                Block.makeCuboidShape(2.9, 3.4, 0, 3.8, 3.85, 16),
                Block.makeCuboidShape(3.3, 3.8, 0, 4.2, 4.25, 16),
                Block.makeCuboidShape(3.7, 4.2, 0, 4.6, 4.65, 16),
                Block.makeCuboidShape(4.1, 4.6, 0, 5.0, 5.05, 16),
                Block.makeCuboidShape(4.5, 5.0, 0, 5.4, 5.45, 16),
                Block.makeCuboidShape(4.9, 5.4, 0, 5.8, 5.85, 16),
                Block.makeCuboidShape(5.3, 5.8, 0, 6.2, 6.25, 16),
                Block.makeCuboidShape(5.7, 6.2, 0, 6.6, 6.65, 16),
                Block.makeCuboidShape(6.1, 6.6, 0, 7.0, 7.05, 16),
                Block.makeCuboidShape(6.5, 7.0, 0, 7.4, 7.45, 16),
                Block.makeCuboidShape(6.9, 7.4, 0, 7.8, 7.85, 16),
                Block.makeCuboidShape(7.3, 7.8, 0, 8.2, 8.25, 16),
                Block.makeCuboidShape(7.7, 8.2, 0, 8.6, 8.65, 16),
                Block.makeCuboidShape(8.1, 8.6, 0, 9.0, 9.05, 16),
                Block.makeCuboidShape(8.5, 9.0, 0, 9.4, 9.45, 16),
                Block.makeCuboidShape(8.9, 9.4, 0, 9.8, 9.85, 16),
                Block.makeCuboidShape(9.3, 9.8, 0, 10.2, 10.25, 16),
                Block.makeCuboidShape(9.7, 10.2, 0, 10.6, 10.65, 16),
                Block.makeCuboidShape(10.1, 10.6, 0, 11.0, 11.05, 16),
                Block.makeCuboidShape(10.5, 11.0, 0, 11.4, 11.45, 16),
                Block.makeCuboidShape(10.9, 11.4, 0, 11.8, 11.85, 16),
                Block.makeCuboidShape(11.3, 11.8, 0, 12.2, 12.25, 16),
                Block.makeCuboidShape(11.7, 12.2, 0, 12.6, 12.65, 16),
                Block.makeCuboidShape(12.1, 12.6, 0, 13.0, 13.05, 16),
                Block.makeCuboidShape(12.5, 13.0, 0, 13.4, 13.45, 16),
                Block.makeCuboidShape(12.9, 13.4, 0, 13.8, 13.85, 16),
                Block.makeCuboidShape(13.3, 13.8, 0, 14.2, 14.25, 16),
                Block.makeCuboidShape(13.7, 14.2, 0, 14.6, 14.65, 16),
                Block.makeCuboidShape(14.1, 14.6, 0, 15.0, 15.05, 16),
                Block.makeCuboidShape(14.5, 15.0, 0, 15.4, 15.45, 16),
                Block.makeCuboidShape(14.9, 15.4, 0, 15.8, 15.85, 16),
                Block.makeCuboidShape(15, 15, 0, 16, 16, 16)
        );
    }

    private static VoxelShape makeShapeBack2() {
        return VoxelShapes.or(
                Block.makeCuboidShape(15, 1, 0, 16, 15.85, 1),
                Block.makeCuboidShape(14, 1, 0, 15, 14.65, 1),
                Block.makeCuboidShape(13, 1, 0, 14, 13.85, 1),
                Block.makeCuboidShape(12, 1, 0, 13, 12.65, 1),
                Block.makeCuboidShape(11, 1, 0, 12, 11.85, 1),
                Block.makeCuboidShape(10, 1, 0, 11, 10.65, 1),
                Block.makeCuboidShape(9, 1, 0, 10, 9.85, 1),
                Block.makeCuboidShape(8, 1, 0, 9, 8.65, 1),
                Block.makeCuboidShape(7, 1, 0, 8, 7.85, 1),
                Block.makeCuboidShape(6, 1, 0, 7, 6.65, 1),
                Block.makeCuboidShape(5, 1, 0, 6, 5.85, 1),
                Block.makeCuboidShape(4, 1, 0, 5, 4.65, 1),
                Block.makeCuboidShape(3, 1, 0, 4, 3.85, 1),
                Block.makeCuboidShape(2, 1, 0, 3, 2.65, 1),
                Block.makeCuboidShape(1, 1, 0, 2, 1.85, 1));
    }

    private static VoxelShape makeShapeBack1() {
        return VoxelShapes.or(
                Block.makeCuboidShape(14, 1, 0, 15, 1.85, 1),
                Block.makeCuboidShape(13, 1, 0, 14, 2.65, 1),
                Block.makeCuboidShape(12, 1, 0, 13, 3.85, 1),
                Block.makeCuboidShape(11, 1, 0, 12, 4.65, 1),
                Block.makeCuboidShape(10, 1, 0, 11, 5.85, 1),
                Block.makeCuboidShape(9, 1, 0, 10, 6.65, 1),
                Block.makeCuboidShape(8, 1, 0, 9, 7.85, 1),
                Block.makeCuboidShape(7, 1, 0, 8, 8.65, 1),
                Block.makeCuboidShape(6, 1, 0, 7, 9.85, 1),
                Block.makeCuboidShape(5, 1, 0, 6, 10.65, 1),
                Block.makeCuboidShape(4, 1, 0, 5, 11.85, 1),
                Block.makeCuboidShape(3, 1, 0, 4, 12.65, 1),
                Block.makeCuboidShape(2, 1, 0, 3, 13.85, 1),
                Block.makeCuboidShape(1, 1, 0, 2, 14.65, 1),
                Block.makeCuboidShape(0, 1, 0, 1, 15.85, 1));
    }
}
