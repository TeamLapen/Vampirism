package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.items.IBloodContainerItem;
import de.teamlapen.vampirism.config.Configs;
import de.teamlapen.vampirism.core.ModFluids;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;

import java.util.List;

/**
 * Stores blood
 * Currently the only thing that can interact with the players bloodstats.
 * Can only store blood in {@link ItemBloodBottle#capacity} tenth units.
 */
public class ItemBloodBottle extends VampirismItem implements IBloodContainerItem {

    public static final int AMOUNT = 9;
    private static final String name = "bloodBottle";
    private static final int MULTIPLIER = VReference.FOOD_TO_FLUID_BLOOD;
    private static final int capacity = AMOUNT * MULTIPLIER;

    /**
     * Set's the registry name and the unlocalized name
     */
    public ItemBloodBottle() {
        super(name);
        this.setMaxStackSize(1);
        this.setHasSubtypes(true);
    }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, IBlockAccess world, BlockPos pos, EntityPlayer player) {
        Block b = world.getBlockState(pos).getBlock();
        return b instanceof IFluidHandler || b instanceof ITileEntityProvider && world.getTileEntity(pos) instanceof IFluidHandler;
    }


    @Override
    public FluidStack drain(ItemStack container, int maxDrain, boolean doDrain) {
        int currentAmt = getBlood(container);
        if (currentAmt == 0) return null;
        FluidStack stack = new FluidStack(ModFluids.blood, Math.min(currentAmt, getAdjustedAmount(maxDrain)));
        if (doDrain) {
            setBlood(container, currentAmt - stack.amount);
            //TODO might cause crashes with other mods, although this is probably legit
            if (getBlood(container) == 0 && Configs.autoConvertGlasBottles) {
                VampirismMod.log.i("BloodBottle", "Replaced blood bottle by glas bottle, during IFluidContainerItem#drain. If there is a crash afterwards, please contact the authors of Vampirism");//TODO and remove at some point
                container.setItem(Items.glass_bottle);
            }
        }
        return stack;
    }

    @Override
    public int fill(ItemStack container, FluidStack resource, boolean doFill) {
        if (resource == null) return 0;
        if (!resource.getFluid().equals(ModFluids.blood)) {
            return 0;
        }
        if (!doFill) {
            return Math.min(capacity - getBlood(container), getAdjustedAmount(resource.amount));
        }

        int itemamt = getBlood(container);
        int toFill = Math.min(capacity - itemamt, getAdjustedAmount(resource.amount));
        setBlood(container, itemamt + toFill);
        return toFill;


    }

    public int getBlood(ItemStack stack) {
        return stack.getItemDamage() * MULTIPLIER;
    }

    @Override
    public int getCapacity(ItemStack container) {
        return capacity;
    }

    @Override
    public FluidStack getFluid(ItemStack container) {
        return new FluidStack(ModFluids.blood, getBlood(container) * MULTIPLIER);
    }

    @Override
    public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
        subItems.add(new ItemStack(itemIn, 1));
        subItems.add(new ItemStack(itemIn, 1, AMOUNT));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
        //Won't be called when the player is sneaking since {@link ItemBloodBottle#doesSneakBypassUse} returns true to allow blood container interaction
        if (worldIn.isRemote) {
            return new ActionResult<>(EnumActionResult.PASS, itemStackIn);
        }
        if (!worldIn.isRemote) {
            VampirePlayer vampire = VampirePlayer.get(playerIn);
            if (vampire.getLevel() == 0) return new ActionResult<>(EnumActionResult.PASS, itemStackIn);
//            Cannot drain blood from the bar anymore
//            if (playerIn.isSneaking()) {//Remove blood from bar
//                int playerBlood = vampire.getBloodLevel();
//                if (playerBlood > 0) {
//                    int i = fill(itemStackIn, new FluidStack(ModFluids.blood, VReference.FOOD_TO_FLUID_BLOOD), true);
//                    if (i > 0) {
//                        vampire.getBloodStats().consumeBlood(1);
//                    }
//                }
//            } else {//Fill blood bar
//                if (vampire.getBloodStats().needsBlood()) {
//                    if (drain(itemStackIn, VReference.FOOD_TO_FLUID_BLOOD, true) != null) {
//                        vampire.getBloodStats().addBlood(1, 0);
//                    }
//                }
//            }
            if (vampire.getBloodStats().needsBlood()) {
                if (drain(itemStackIn, VReference.FOOD_TO_FLUID_BLOOD, true) != null) {
                    vampire.getBloodStats().addBlood(1, 0);
                    return new ActionResult<>(EnumActionResult.SUCCESS, itemStackIn);
                }
            }

        }
        return new ActionResult<>(EnumActionResult.FAIL, itemStackIn);
    }


    public void setBlood(ItemStack stack, int amt) {
        stack.setItemDamage(amt / MULTIPLIER);
    }

    /**
     * Returns a amount which is a multiple of capacity%10
     *
     * @param amt
     * @return
     */
    private int getAdjustedAmount(int amt) {
        return amt - amt % MULTIPLIER;
    }
}
