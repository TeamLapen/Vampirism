package de.teamlapen.vampirism.blocks;

import com.mojang.serialization.MapCodec;
import de.teamlapen.vampirism.blockentity.AlchemicalCauldronBlockEntity;
import de.teamlapen.vampirism.core.ModSounds;
import de.teamlapen.vampirism.core.ModStats;
import de.teamlapen.vampirism.core.ModTiles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
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
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class AlchemicalCauldronBlock extends AbstractFurnaceBlock {

    public static final MapCodec<AlchemicalCauldronBlock> CODEC = simpleCodec(AlchemicalCauldronBlock::new);

    public static final EnumProperty<LiquidState> LIQUID = EnumProperty.create("liquid", LiquidState.class);

    private static final VoxelShape SHAPE = Shapes.or(
            Block.box(2, 0, 2, 14, 9, 14),
            Block.box(1, 9, 1, 15, 13, 15),
            Block.box(2, 13, 2, 14, 14, 14)
    );

    public AlchemicalCauldronBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(LIQUID, LiquidState.NONE).setValue(FACING, Direction.NORTH).setValue(LIT, false));
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (state.getValue(LIQUID) == LiquidState.BOILING) {
            level.playLocalSound(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, ModSounds.BOILING.get(), SoundSource.BLOCKS, 0.05F, 1, false);
        }
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide() ? null : createTickerHelper(blockEntityType, ModTiles.ALCHEMICAL_CAULDRON.get(), AlchemicalCauldronBlockEntity::serverTick);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AlchemicalCauldronBlockEntity(pos, state);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        BlockEntity tile = level.getBlockEntity(pos);
        if (placer instanceof Player && tile instanceof AlchemicalCauldronBlockEntity cauldronBlockEntity) {
            cauldronBlockEntity.setOwnerID((Player) placer);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(LIQUID);
    }

    @Override
    protected void openContainer(Level level, BlockPos pos, Player player) {
        BlockEntity tile = level.getBlockEntity(pos);
        if (tile instanceof AlchemicalCauldronBlockEntity) {
            player.openMenu((MenuProvider) tile);
            player.awardStat(ModStats.INTERACT_ALCHEMICAL_CAULDRON.get());
        }
    }

    @Override
    protected MapCodec<? extends AbstractFurnaceBlock> codec() {
        return CODEC;
    }

    public enum LiquidState implements StringRepresentable {
        NONE("none"),
        FILLED("filled"),
        BOILING("boiling");

        private final String name;

        LiquidState(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }
}
