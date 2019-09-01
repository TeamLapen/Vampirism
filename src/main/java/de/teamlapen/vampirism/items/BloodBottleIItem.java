package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.fluids.BloodHelper;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.UseAction;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

/**
 * Stores blood
 * Currently the only thing that can interact with the players bloodstats.
 * Can only store blood in {@link BloodBottleIItem#capacity} tenth units.
 */
public class BloodBottleIItem extends VampirismItem {

    public static final int AMOUNT = 9;
    private static final String name = "blood_bottle";
    private static final int MULTIPLIER = VReference.FOOD_TO_FLUID_BLOOD;
    private static final int capacity = AMOUNT * MULTIPLIER;

    /**
     * Set's the registry name and the unlocalized name
     */
    public BloodBottleIItem() {
        super(name, new Properties().containerItem(Items.GLASS_BOTTLE).defaultMaxDamage(AMOUNT).group(VampirismMod.creativeTab));
    }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, IWorldReader world, BlockPos pos, PlayerEntity player) {
        TileEntity t = world.getTileEntity(pos);
        return t != null && t.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null).isPresent();
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.DRINK;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 15;
    }


    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundNBT nbt) {
        return new BloodBottleFluidHandler(stack, capacity);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        VampirePlayer vampire = VampirePlayer.get(playerIn);
        if (vampire.getLevel() == 0) return new ActionResult<>(ActionResultType.PASS, stack);


        if (vampire.getBloodStats().needsBlood()) {
            playerIn.setActiveHand(handIn);
            return new ActionResult<>(ActionResultType.SUCCESS, stack);
        }

        return new ActionResult<>(ActionResultType.PASS, stack);
    }

    @Override
    public void onUsingTick(ItemStack stack, LivingEntity player, int count) {
        if (!(player instanceof PlayerEntity)) {
            player.stopActiveHand();
            return;
        }
        int blood = BloodHelper.getBlood(stack);
        VampirePlayer vampire = VampirePlayer.get((PlayerEntity) player);
        if (vampire.getLevel() == 0 || blood == 0 || !vampire.getBloodStats().needsBlood()) {
            player.stopActiveHand();
            return;
        }


        if (blood > 0 && count == 1) {
            Hand activeHand = player.getActiveHand();
            int drink = Math.min(blood, 3 * MULTIPLIER);
            if (BloodHelper.drain(stack, drink, IFluidHandler.FluidAction.EXECUTE, true) > 0) {
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
