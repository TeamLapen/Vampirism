package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.blocks.BloodContainerBlock;
import de.teamlapen.vampirism.core.ModFluids;
import de.teamlapen.vampirism.fluids.BloodHelper;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.NotNull;

public class FeedingAdapterItem extends Item {

    public FeedingAdapterItem() {
        super(new Item.Properties().stacksTo(1).tab(VampirismMod.creativeTab));
    }


    @NotNull
    @Override
    public UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public int getUseDuration(@NotNull ItemStack stack) {
        return 15;
    }

    @Override
    public void onUsingTick(ItemStack stack, LivingEntity player, int count) {
        if (!(player instanceof Player) || !player.isAlive()) {
            player.releaseUsingItem();
            return;
        }
        ItemStack bloodContainer = BloodHelper.getBloodContainerInInventory(((Player) player).getInventory(), true, false);
        FluidStack fluidStack = BloodContainerBlock.getFluidFromItemStack(bloodContainer);
        int blood = fluidStack.isEmpty() || fluidStack.getFluid() != ModFluids.BLOOD.get() ? 0 : fluidStack.getAmount();
        VampirePlayer vampire = VampirePlayer.get((Player) player);
        if (vampire.getLevel() == 0 || blood == 0 || !vampire.getBloodStats().needsBlood()) {
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

    @NotNull
    @Override
    public InteractionResultHolder<ItemStack> use(@NotNull Level worldIn, @NotNull Player playerIn, @NotNull InteractionHand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);
        return VampirePlayer.getOpt(playerIn).map(vampire -> {
            if (vampire.getLevel() == 0) return new InteractionResultHolder<>(InteractionResult.PASS, stack);


            if (vampire.getBloodStats().needsBlood() && !BloodHelper.getBloodContainerInInventory(playerIn.getInventory(), true, false).isEmpty()) {
                playerIn.startUsingItem(handIn);
                return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
            }
            return new InteractionResultHolder<>(InteractionResult.PASS, stack);
        }).orElse(new InteractionResultHolder<>(InteractionResult.PASS, stack));
    }

}
