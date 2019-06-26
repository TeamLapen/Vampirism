package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.fluids.BloodHelper;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

/**
 * Stores blood
 * Currently the only thing that can interact with the players bloodstats.
 * Can only store blood in {@link ItemBloodBottle#capacity} tenth units.
 */
public class ItemBloodBottle extends VampirismItem {

    public static final int AMOUNT = 9;
    private static final String name = "blood_bottle";
    private static final int MULTIPLIER = VReference.FOOD_TO_FLUID_BLOOD;
    private static final int capacity = AMOUNT * MULTIPLIER;

    /**
     * Set's the registry name and the unlocalized name
     */
    public ItemBloodBottle() {
        super(name, new Properties().containerItem(Items.GLASS_BOTTLE).defaultMaxDamage(AMOUNT).group(VampirismMod.creativeTab));
    }


    @Override
    public boolean doesSneakBypassUse(ItemStack stack, IWorldReader world, BlockPos pos, EntityPlayer player) {
        TileEntity t = world.getTileEntity(pos);
        return t != null && t.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null).isPresent();
    }



    @Override
    public EnumAction getUseAction(ItemStack stack) {
        return EnumAction.DRINK;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 15;
    }


    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) {
        return new BloodBottleFluidHandler(stack, capacity);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        VampirePlayer vampire = VampirePlayer.get(playerIn);
        if (vampire.getLevel() == 0) return new ActionResult<>(EnumActionResult.PASS, stack);


        if (vampire.getBloodStats().needsBlood()) {
            playerIn.setActiveHand(handIn);
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }

        return new ActionResult<>(EnumActionResult.PASS, stack);
    }

    @Override
    public void onUsingTick(ItemStack stack, EntityLivingBase player, int count) {
        if (!(player instanceof EntityPlayer)) {
            player.stopActiveHand();
            return;
        }
        int blood = BloodHelper.getBlood(stack);
        VampirePlayer vampire = VampirePlayer.get((EntityPlayer) player);
        if (vampire.getLevel() == 0 || blood == 0 || !vampire.getBloodStats().needsBlood()) {
            player.stopActiveHand();
            return;
        }


        if (blood > 0 && count == 1) {
            EnumHand activeHand = player.getActiveHand();
            int drink = Math.min(blood, 3 * MULTIPLIER);
            if (BloodHelper.drain(stack, drink, true, true) > 0) {
                vampire.drinkBlood(Math.round(((float) drink) / VReference.FOOD_TO_FLUID_BLOOD), 0.3F, false);
            }
            player.setHeldItem(activeHand, stack);

            blood = BloodHelper.getBlood(stack);
            if (blood > 0) {
                player.setActiveHand(player.getActiveHand());
            }
        }
    }
}
