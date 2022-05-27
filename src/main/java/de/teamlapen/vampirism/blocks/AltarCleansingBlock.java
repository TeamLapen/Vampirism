package de.teamlapen.vampirism.blocks;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.player.hunter.skills.HunterSkills;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Placed in some churches
 */
public class AltarCleansingBlock extends VampirismHorizontalBlock {
    private static final VoxelShape SHAPEX = makeShape();
    private static final VoxelShape SHAPEZ = UtilLib.rotateShape(SHAPEX, UtilLib.RotationAmount.NINETY);

    private static VoxelShape makeShape() {
        VoxelShape a = Block.box(1, 0, 5, 15, 1, 12);
        VoxelShape b = Block.box(7, 1, 7, 9, 12, 11);
        VoxelShape c = Block.box(1, 9, 3, 15, 14, 13);
        VoxelShape r = Shapes.or(a, b);
        return Shapes.or(r, c);
    }


    public AltarCleansingBlock() {
        super(Properties.of(Material.WOOD).strength(0.5f).noOcclusion());
        this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH));
    }


    @Nonnull
    @Override
    public VoxelShape getShape(BlockState blockState, @Nonnull BlockGetter worldIn, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        Direction dir = blockState.getValue(FACING);
        if (dir == Direction.NORTH || dir == Direction.SOUTH) return SHAPEX;
        return SHAPEZ;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }

    @Nonnull
    @Override
    public String getDescriptionId() {
        return "block.vampirism.church_altar";
    }


    @Nonnull
    @Override
    public InteractionResult use(@Nonnull BlockState state, @Nonnull Level world, @Nonnull BlockPos pos, Player player, @Nonnull InteractionHand hand, @Nonnull BlockHitResult hit) {
        if (!player.isAlive()) return InteractionResult.PASS;
        LazyOptional<FactionPlayerHandler> handler = FactionPlayerHandler.getOpt(player);
        ItemStack heldItem = player.getItemInHand(hand);
        if (handler.map(h->h.isInFaction(VReference.VAMPIRE_FACTION)).orElse(false)) {
            if (world.isClientSide()) {
                VampirismMod.proxy.displayRevertBackScreen();
            }
            return InteractionResult.SUCCESS;
        } else if (!heldItem.isEmpty()) {
            if (ModItems.HOLY_SALT_WATER.get() == heldItem.getItem()) {
                if (world.isClientSide) return InteractionResult.SUCCESS;
                boolean enhanced = handler.map(h-> h.isInFaction(VReference.HUNTER_FACTION) && h.getCurrentFactionPlayer().map(IFactionPlayer::getSkillHandler).map(s -> s.isSkillEnabled(HunterSkills.HOLY_WATER_ENHANCED.get())).orElse(false)).orElse(false);
                ItemStack newStack = new ItemStack(enhanced ? ModItems.HOLY_WATER_BOTTLE_ENHANCED.get() : ModItems.HOLY_WATER_BOTTLE_NORMAL.get(), heldItem.getCount());
                player.setItemInHand(hand, newStack);
                return InteractionResult.SUCCESS;
            } else if (ModItems.PURE_SALT.get() == heldItem.getItem()) {
                if (world.isClientSide) return InteractionResult.SUCCESS;
                player.setItemInHand(hand, new ItemStack(ModItems.HOLY_SALT.get(), heldItem.getCount()));
            }
        }
        return InteractionResult.PASS;
    }
}
