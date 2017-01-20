package de.teamlapen.vampirism.items;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.blocks.BlockAlchemicalFire;
import de.teamlapen.vampirism.core.ModBlocks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

/**
 * Simple placer item for alchemical fire. Also used in recipes
 *
 * @author maxanier
 */
public class ItemAlchemicalFire extends VampirismItem {

    private static final String regName = "itemAlchemicalFire";

    public ItemAlchemicalFire() {
        super(regName);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, playerIn, tooltip, advanced);
        tooltip.add(UtilLib.translate("item.vampirism.itemAlchemicalFire.desc1"));
        tooltip.add(UtilLib.translate("item.vampirism.itemAlchemicalFire.desc2"));
    }

    @Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        pos = pos.offset(facing);

        if (!playerIn.canPlayerEdit(pos, facing, stack)) {
            return EnumActionResult.FAIL;
        } else {
            if (worldIn.isAirBlock(pos)) {
                worldIn.playSound(playerIn, pos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, itemRand.nextFloat() * 0.4F + 0.8F);
                worldIn.setBlockState(pos, ModBlocks.alchemicalFire.getDefaultState().withProperty(BlockAlchemicalFire.AGE, 15), 11);
            }

            return EnumActionResult.SUCCESS;
        }
    }
}
