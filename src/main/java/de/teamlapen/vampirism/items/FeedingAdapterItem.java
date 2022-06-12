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

public class FeedingAdapterItem extends Item {

    public FeedingAdapterItem() {
        super(new Item.Properties().stacksTo(1).tab(VampirismMod.creativeTab));
    }


    @Nonnull
    @Override
    public UseAction getUseAnimation(ItemStack stack) {
        return UseAction.DRINK;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 15;
    }

    @Override
    public void onUsingTick(ItemStack stack, LivingEntity player, int count) {
        if (!(player instanceof PlayerEntity) || !player.isAlive()) {
            player.releaseUsingItem();
            return;
        }
        ItemStack bloodContainer = BloodHelper.getBloodContainerInInventory(((PlayerEntity) player).inventory, true, false);
        FluidStack fluidStack = BloodContainerBlock.getFluidFromItemStack(bloodContainer);
        int blood = fluidStack.isEmpty() || fluidStack.getFluid() != ModFluids.BLOOD.get() ? 0 : fluidStack.getAmount();
        VampirePlayer vampire = VampirePlayer.getOpt((PlayerEntity) player).resolve().orElse(null);
        if (vampire == null || vampire.getLevel() == 0 || blood == 0 || !vampire.getBloodStats().needsBlood()) {
            player.releaseUsingItem();
            return;
        }


        if (blood > 0 && count == 1) {
            int drink = Math.min(blood, 3 * VReference.FOOD_TO_FLUID_BLOOD);
            BloodContainerBlock.writeFluidToItemStack(bloodContainer, new FluidStack(ModFluids.BLOOD.get(), blood - drink));
            vampire.drinkBlood(Math.round(((float) drink) / VReference.FOOD_TO_FLUID_BLOOD), 0.3F, false);

            blood = blood - drink;
            if (blood > 0) {
                player.startUsingItem(player.getUsedItemHand());
            }
        }
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, @Nonnull Hand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);
        return VampirePlayer.getOpt(playerIn).map(vampire -> {
            if (vampire.getLevel() == 0) return new ActionResult<>(ActionResultType.PASS, stack);


            if (vampire.getBloodStats().needsBlood() && !BloodHelper.getBloodContainerInInventory(playerIn.inventory, true, false).isEmpty()) {
                playerIn.startUsingItem(handIn);
                return new ActionResult<>(ActionResultType.SUCCESS, stack);
            }
            return new ActionResult<>(ActionResultType.PASS, stack);
        }).orElse(new ActionResult<>(ActionResultType.PASS, stack));
    }

}
