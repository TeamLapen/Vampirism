package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IFactionPlayerHandler;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.client.gui.RevertBackScreen;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.player.hunter.skills.HunterSkills;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Placed in some churches
 */
public class ChurchAltarBlock extends VampirismBlock {

    private final static String regName = "church_altar";
    public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;


    public ChurchAltarBlock() {
        super(regName, Properties.create(Material.WOOD).hardnessAndResistance(0.5f));
        this.setDefaultState(this.getStateContainer().getBaseState().with(FACING, Direction.NORTH));

    }


    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext ctx) {
        return super.getStateForPlacement(ctx).with(FACING, ctx.getPlacementHorizontalFacing().getOpposite());
    }

    @Override
    public boolean isNormalCube(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return false;
    }

    @Override
    public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        IFactionPlayerHandler handler = VampirismAPI.getFactionPlayerHandler(player);
        ItemStack heldItem = player.getHeldItem(hand);
        if (handler.isInFaction(VReference.VAMPIRE_FACTION)) {
            if (world.isRemote) {
                Minecraft.getInstance().displayGuiScreen(new RevertBackScreen());
            }
            return true;
        } else if (!heldItem.isEmpty()) {
            if (ModItems.holy_salt_water.equals(heldItem.getItem())) {
                if (world.isRemote) return true;
                boolean enhanced = handler.isInFaction(VReference.HUNTER_FACTION) && ((IHunterPlayer) handler.getCurrentFactionPlayer()).getSkillHandler().isSkillEnabled(HunterSkills.holy_water_enhanced);
                ItemStack newStack = new ItemStack(enhanced ? ModItems.holy_water_bottle_enhanced : ModItems.holy_water_bottle_normal, heldItem.getCount());
                player.setHeldItem(hand, newStack);
                return true;
            } else if (ModItems.pure_salt.equals(heldItem.getItem())) {
                if (world.isRemote) return true;
                player.setHeldItem(hand, new ItemStack(ModItems.holy_salt, heldItem.getCount()));
            }
        }
        return false;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }
}
