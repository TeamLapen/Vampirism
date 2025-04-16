package de.teamlapen.vampirism.blocks;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.blockentity.TotemBlockEntity;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModStats;
import de.teamlapen.vampirism.core.ModTiles;
import de.teamlapen.vampirism.util.FactionCodec;
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
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
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
                    FactionCodec.faction().fieldOf("faction").forGetter(inst2 -> inst2.faction),
                    propertiesCodec()
            ).apply(inst, TotemTopBlock::new)
    );

    private static final VoxelShape SHAPE = Shapes.or(Block.box(3, 0, 3, 13, 10, 13), Block.box(1, 1, 1, 15, 9, 15));

    private static final List<TotemTopBlock> blocks = new ArrayList<>();

    public static @NotNull List<TotemTopBlock> getBlocks() {
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
    public boolean canEntityDestroy(BlockState state, BlockGetter world, BlockPos pos, Entity entity) {
        return false;
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @NotNull
    @Override
    public RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public float getExplosionResistance() {
        return Float.MAX_VALUE;
    }

    @Override
    public void neighborChanged(@NotNull BlockState state, @NotNull Level worldIn, @NotNull BlockPos pos, @NotNull Block blockIn, @NotNull Orientation orientation, boolean isMoving) {
        if (worldIn.isClientSide) return;
        BlockEntity tile = worldIn.getBlockEntity(pos);
        if (tile instanceof TotemBlockEntity) {
            ((TotemBlockEntity) tile).updateTileStatus();
            worldIn.blockEvent(pos, this, 1, 0); //Notify client about render update
        }
    }

    @NotNull
    @Override
    public VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter worldIn, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPE;
    }

    public boolean isCrafted() {
        return crafted;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return ModTiles.TOTEM.get().create(pos, state);
    }

    @Override
    public void onRemove(@NotNull BlockState state, @NotNull Level worldIn, @NotNull BlockPos pos, @NotNull BlockState newState, boolean isMoving) {
        if (!(newState.getBlock() instanceof TotemTopBlock)) {
            worldIn.removeBlockEntity(pos);
        }
    }

    @NotNull
    @Override
    public InteractionResult useWithoutItem(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos, @NotNull Player player, @NotNull BlockHitResult hit) {
        if (world.isClientSide) return InteractionResult.SUCCESS;
        TotemBlockEntity t = getTile(world, pos);
        if (t != null && world.getBlockState(pos.below()).getBlock().equals(ModBlocks.TOTEM_BASE.get())) {
            player.awardStat(ModStats.INTERACT_WITH_TOTEM.get());
            t.initiateCapture(player);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, @NotNull Level world, @NotNull BlockPos pos, @NotNull Player player, boolean willHarvest, FluidState fluid) {
        TotemBlockEntity tile = getTile(world, pos);
        if (tile != null) {
            if (!tile.canPlayerRemoveBlock(player)) {
                return false;
            }
        }
        if (super.onDestroyedByPlayer(state, world, pos, player, willHarvest, fluid)) {
            if (tile != null && tile.getControllingFaction() != null) {
                tile.notifyNearbyPlayers(Component.translatable("text.vampirism.village.village_abandoned"));
            }
            return true;
        } else {
            return false;
        }
    }

    @Nullable
    private TotemBlockEntity getTile(@NotNull Level world, @NotNull BlockPos pos) {
        BlockEntity tile = world.getBlockEntity(pos);
        if (tile instanceof TotemBlockEntity) return (TotemBlockEntity) tile;
        return null;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        return createTickerHelper(type, ModTiles.TOTEM.get(), level.isClientSide() ? TotemBlockEntity::clientTick : TotemBlockEntity::serverTick);
    }
}
