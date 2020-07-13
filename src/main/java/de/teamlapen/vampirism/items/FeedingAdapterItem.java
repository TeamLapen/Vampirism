package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.blocks.BloodContainerBlock;
import de.teamlapen.vampirism.core.ModFluids;
import de.teamlapen.vampirism.fluids.BloodHelper;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;

public class FeedingAdapterItem extends VampirismItem {
    private final static String regName = "feeding_adapter";

    public FeedingAdapterItem() {
        super(regName, new Item.Properties().maxStackSize(1).group(VampirismMod.creativeTab));
    }


    @Nonnull
    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.DRINK;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 15;
    }


    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, @Nonnull Hand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        VampirePlayer vampire = VampirePlayer.get(playerIn);
        if (vampire.getLevel() == 0) return new ActionResult<>(ActionResultType.PASS, stack);


        if (vampire.getBloodStats().needsBlood() && !BloodHelper.getBloodContainerInInventory(playerIn.inventory, true, false).isEmpty()) {
            playerIn.setActiveHand(handIn);
            return new ActionResult<>(ActionResultType.SUCCESS, stack);
        }
        return new ActionResult<>(ActionResultType.PASS, stack);
    }


    @Override
    public void onUsingTick(ItemStack stack, LivingEntity player, int count) {
        if (!(player instanceof PlayerEntity) || !player.isAlive()) {
            player.stopActiveHand();
            return;
        }
        ItemStack bloodContainer = BloodHelper.getBloodContainerInInventory(((PlayerEntity) player).inventory, true, false);
        FluidStack fluidStack = BloodContainerBlock.getFluidFromItemStack(bloodContainer);
        int blood = fluidStack.isEmpty() || fluidStack.getFluid() != ModFluids.blood ? 0 : fluidStack.getAmount();
        VampirePlayer vampire = VampirePlayer.get((PlayerEntity) player);
        if (vampire.getLevel() == 0 || blood == 0 || !vampire.getBloodStats().needsBlood()) {
            player.stopActiveHand();
            return;
        }


        if (blood > 0 && count == 1) {
            int drink = Math.min(blood, 3 * VReference.FOOD_TO_FLUID_BLOOD);
            BloodContainerBlock.writeFluidToItemStack(bloodContainer, new FluidStack(ModFluids.blood, blood - drink));
            vampire.drinkBlood(Math.round(((float) drink) / VReference.FOOD_TO_FLUID_BLOOD), 0.3F, false);

            blood = blood - drink;
            if (blood > 0) {
                player.setActiveHand(player.getActiveHand());
            }
        }
    }

}
