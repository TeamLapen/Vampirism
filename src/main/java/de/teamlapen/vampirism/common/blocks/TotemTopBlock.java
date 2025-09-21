package de.teamlapen.vampirism.common.blocks;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.common.blockentity.TotemBlockEntity;
import de.teamlapen.vampirism.common.core.ModBlocks;
import de.teamlapen.vampirism.common.core.ModFactions;
import de.teamlapen.vampirism.common.core.ModStats;
import de.teamlapen.vampirism.common.core.ModBlockEntities;
import de.teamlapen.vampirism.common.serialization.ModCodecs;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Top of a two block multiblock structure.
 * Is destroyed if lower block is broken.
 * Can only be broken by player if tile entity allows it
 * <p>
 * Has both model renderer (with color/tint) and TESR (used for beam)
 */
public class TotemTopBlock extends BaseEntityBlock {

    public static final MapCodec<TotemTopBlock> CODEC = RecordCodecBuilder.mapCodec(inst ->
            inst.group(
                    Codec.BOOL.fieldOf("crafted").forGetter(TotemTopBlock::isCrafted),
                    ModCodecs.faction().fieldOf("faction").forGetter(inst2 -> inst2.faction),
                    propertiesCodec()
            ).apply(inst, TotemTopBlock::new)
    );

    private static final VoxelShape SHAPE = Shapes.or(
            Block.box(3, 0, 3, 13, 10, 13),
            Block.box(1, 1, 1, 15, 9, 15)
    );

    private static final List<TotemTopBlock> blocks = new ArrayList<>();

    public static List<TotemTopBlock> getBlocks() {
        return Collections.unmodifiableList(blocks);
    }

    @Nullable
    public final Holder<? extends IFaction<?>> faction;
    private final boolean crafted;

    public TotemTopBlock(Properties properties, boolean crafted, @Nullable Holder<? extends IFaction<?>> faction) {
        this(crafted, faction, properties);
    }

    /**
     * @param faction faction must be faction registryname;
     */
    public TotemTopBlock(boolean crafted, @Nullable Holder<? extends IFaction<?>> faction, Properties properties) {
        super(properties);
        this.faction = faction;
        this.crafted = crafted;
        blocks.add(this);
    }

    @Override
    public boolean canEntityDestroy(BlockState state, BlockGetter level, BlockPos pos, Entity entity) {
        return false;
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public float getExplosionResistance() {
        return Float.MAX_VALUE;
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, @Nullable Orientation orientation, boolean movedByPiston) {
        if (level.isClientSide) return;
        BlockEntity tile = level.getBlockEntity(pos);
        if (tile instanceof TotemBlockEntity totemBlockEntity) {
            totemBlockEntity.updateTileStatus();
            level.blockEvent(pos, this, 1, 0); //Notify client about render update
        }
    }

    public boolean isCrafted() {
        return crafted;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return ModBlockEntities.TOTEM.get().create(pos, state);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!(newState.getBlock() instanceof TotemTopBlock)) {
            level.removeBlockEntity(pos);
        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide) return InteractionResult.SUCCESS;

        TotemBlockEntity blockEntity = getBlockEntity(level, pos);
        if (blockEntity != null && level.getBlockState(pos.below()).getBlock().equals(ModBlocks.TOTEM_BASE.get())) {
            player.awardStat(ModStats.INTERACT_WITH_TOTEM.get());
            blockEntity.initiateCapture(player);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        TotemBlockEntity blockEntity = getBlockEntity(level, pos);
        if (blockEntity != null) {
            if (!blockEntity.canPlayerRemoveBlock(player)) {
                return false;
            }
        }
        if (super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid)) {
            if (blockEntity != null && !blockEntity.getControllingFaction().is(ModFactions.NEUTRAL.getId())) {
                blockEntity.notifyNearbyPlayers(Component.translatable("text.vampirism.village.village_abandoned"));
            }
            return true;
        } else {
            return false;
        }
    }

    @Nullable
    private TotemBlockEntity getBlockEntity(Level world, BlockPos pos) {
        return world.getBlockEntity(pos) instanceof TotemBlockEntity totemBlockEntity ? totemBlockEntity : null;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, ModBlockEntities.TOTEM.get(), level.isClientSide() ? TotemBlockEntity::clientTick : TotemBlockEntity::serverTick);
    }
}
