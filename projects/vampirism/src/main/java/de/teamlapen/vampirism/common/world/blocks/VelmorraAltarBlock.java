package de.teamlapen.vampirism.common.world.blocks;

import com.mojang.serialization.MapCodec;
import de.teamlapen.vampirism.common.core.ModBlockEntities;
import de.teamlapen.vampirism.common.core.ModDataComponents;
import de.teamlapen.vampirism.common.core.ModItems;
import de.teamlapen.vampirism.common.world.blockentity.VelmorraAltarBlockEntity;
import de.teamlapen.vampirism.common.world.items.component.RitualKnifeType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;

public class VelmorraAltarBlock extends BaseEntityBlock {

    public static final MapCodec<VelmorraAltarBlock> CODEC = simpleCodec(VelmorraAltarBlock::new);
    /**
     * Set as soon as any knife is inserted (the insertion draws the player's blood)
     */
    public static final BooleanProperty HAS_BLOOD = BooleanProperty.create("has_blood");
    public static final BooleanProperty SEEKING_KNIFE = BooleanProperty.create("seeking_knife");
    public static final BooleanProperty PASSAGE_KNIFE = BooleanProperty.create("passage_knife");
    public static final BooleanProperty ANCHORING_KNIFE = BooleanProperty.create("anchoring_knife");
    /**
     * The ritual is being channeled, the knives glow
     */
    public static final BooleanProperty CHANNELING = BooleanProperty.create("channeling");
    /**
     * The portal is open, the knives are fully inserted. Otherwise they are only halfway inserted.
     */
    public static final BooleanProperty OPEN = BooleanProperty.create("open");
    private static final Map<RitualKnifeType, BooleanProperty> KNIFE_PROPERTIES = new EnumMap<>(Map.of(
            RitualKnifeType.SEEKING, SEEKING_KNIFE,
            RitualKnifeType.PASSAGE, PASSAGE_KNIFE,
            RitualKnifeType.ANCHORING, ANCHORING_KNIFE
    ));
    private static final VoxelShape SHAPE = Shapes.or(
            Shapes.box(0, 0, 0, 1, 0.1875, 1),
            Shapes.box(0.1875, 0.1875, 0.1875, 0.8125, 0.375, 0.8125),
            Shapes.box(0, 0.375, 0, 1, 1, 1)
    );

    public static BooleanProperty knifeProperty(RitualKnifeType type) {
        return KNIFE_PROPERTIES.get(type);
    }

    public VelmorraAltarBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(HAS_BLOOD, false)
                .setValue(SEEKING_KNIFE, false)
                .setValue(PASSAGE_KNIFE, false)
                .setValue(ANCHORING_KNIFE, false)
                .setValue(CHANNELING, false)
                .setValue(OPEN, false));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new VelmorraAltarBlockEntity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide() ? null : createTickerHelper(blockEntityType, ModBlockEntities.VELMORRA_ALTAR.get(), VelmorraAltarBlockEntity::serverTick);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(HAS_BLOOD, SEEKING_KNIFE, PASSAGE_KNIFE, ANCHORING_KNIFE, CHANNELING, OPEN);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (stack.is(ModItems.RITUAL_KNIFE) && stack.has(ModDataComponents.RITUAL_KNIFE_TYPE)) {
            if (level.isClientSide()) {
                return InteractionResult.SUCCESS;
            }
            if (level.getBlockEntity(pos) instanceof VelmorraAltarBlockEntity be) {
                return be.insertKnife(player, stack) ? InteractionResult.SUCCESS_SERVER : InteractionResult.FAIL;
            }
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!player.getMainHandItem().isEmpty()) {
            return InteractionResult.PASS;
        }
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        if (level.getBlockEntity(pos) instanceof VelmorraAltarBlockEntity be) {
            boolean success = player.isSecondaryUseActive() ? be.removeKnife(player) : be.tryActivate(player);
            return success ? InteractionResult.SUCCESS_SERVER : InteractionResult.FAIL;
        }
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }
}
