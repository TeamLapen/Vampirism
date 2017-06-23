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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Simple placer item for alchemical fire. Also used in recipes
 *
 * @author maxanier
 */
public class ItemAlchemicalFire extends VampirismItem {

    private static final String regName = "item_alchemical_fire";

    public ItemAlchemicalFire() {
        super(regName);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, playerIn, tooltip, advanced);
        tooltip.add(UtilLib.translate("item.vampirism.item_alchemical_fire.desc1"));
        tooltip.add(UtilLib.translate("item.vampirism.item_alchemical_fire.desc2"));
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        pos = pos.offset(facing);

        if (!player.canPlayerEdit(pos, facing, player.getHeldItem(hand))) {
            return EnumActionResult.FAIL;
        } else {
            if (worldIn.isAirBlock(pos)) {
                worldIn.playSound(player, pos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, itemRand.nextFloat() * 0.4F + 0.8F);
                worldIn.setBlockState(pos, ModBlocks.alchemicalFire.getDefaultState().withProperty(BlockAlchemicalFire.AGE, 15), 11);
            }

            return EnumActionResult.SUCCESS;
        }
    }


}
