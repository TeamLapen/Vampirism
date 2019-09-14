package de.teamlapen.vampirism.items;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.blocks.CoffinBlock;
import de.teamlapen.vampirism.core.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

/**
 * Used to place coffings
 */
public class CoffinItem extends VampirismItem {
    public static final String name = "item_coffin";

    public CoffinItem() {
        super(name, new InjectionItem.Properties().group(VampirismMod.creativeTab));
    }


    @Override
    public ActionResultType onItemUse(ItemUseContext context) {

        ItemStack stack = context.getItem();
        World world = context.getWorld();
        PlayerEntity player = context.getPlayer();
        BlockPos targetPos = context.getPos();
        Direction side = context.getFace();

        if (side != Direction.UP) {
            return ActionResultType.FAIL;
        }

        if (world.isRemote)
            return ActionResultType.PASS;
        // Increasing yDisplay, so the coffin is placed on top of the block that was
        // clicked at except if the block is replaceable
        BlockState iblockstate = world.getBlockState(targetPos);
        boolean replaceable = iblockstate.isReplaceable(new BlockItemUseContext(context));

        BlockPos pos = replaceable ? targetPos : targetPos.up();

        // Direction the player is facing
        int direction = MathHelper.floor((player != null ? player.rotationYaw * 4F : 0) / 360F + 0.5D) & 3;
        Direction facing = Direction.byHorizontalIndex(direction);
        BlockPos other = pos.offset(facing);
        boolean other_replaceable = world.getBlockState(other).isReplaceable(new BlockItemUseContext(context));
        boolean flag1 = world.isAirBlock(pos) || replaceable;
        boolean flag2 = world.isAirBlock(other) || other_replaceable;

        if (player == null || player.canPlayerEdit(pos, side, stack) && player.canPlayerEdit(other, side, stack)) {
            if (flag1 && flag2 && UtilLib.doesBlockHaveSolidTopSurface(world, pos.down()) && UtilLib.doesBlockHaveSolidTopSurface(world, other.down())) {
                BlockState state1 = ModBlocks.coffin.getDefaultState().with(CoffinBlock.OCCUPIED, Boolean.FALSE).with(CoffinBlock.PART, CoffinBlock.CoffinPart.FOOT).with(CoffinBlock.FACING, facing);
                if (world.setBlockState(pos, state1, 3)) {
                    BlockState state2 = state1.with(CoffinBlock.PART, CoffinBlock.CoffinPart.HEAD).with(CoffinBlock.FACING, facing);
                    world.setBlockState(other, state2, 3);


                }
                stack.shrink(1);
                return ActionResultType.SUCCESS;
            }
        }
        return ActionResultType.FAIL;
    }
}
