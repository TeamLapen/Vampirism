package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.vampire.IVampire;
import de.teamlapen.vampirism.api.items.IFactionExclusiveItem;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.fluids.BloodHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Stores blood
 * Currently the only thing that can interact with the players bloodstats.
 * Can only store blood in {@link BloodBottleItem#capacity} tenth units.
 */
public class BloodBottleItem extends Item implements IFactionExclusiveItem {

    public static final int AMOUNT = 9;
    private static final int MULTIPLIER = VReference.FOOD_TO_FLUID_BLOOD;
    private static final int capacity = AMOUNT * MULTIPLIER;

    public static @NotNull ItemStack getStackWithDamage(int damage) {
        ItemStack stack = new ItemStack(ModItems.BLOOD_BOTTLE.get());
        stack.setDamageValue(damage);
        return stack;
    }

    /**
     * Set's the registry name and the unlocalized name
     */
    public BloodBottleItem() {
        super(new Properties().defaultDurability(AMOUNT).tab(VampirismMod.creativeTab).setNoRepair());
    }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, @NotNull LevelReader world, @NotNull BlockPos pos, Player player) {
        BlockEntity t = world.getBlockEntity(pos);
        return t != null && t.getCapability(ForgeCapabilities.FLUID_HANDLER).isPresent();
    }

    @Override
    public void fillItemCategory(@NotNull CreativeModeTab group, @NotNull NonNullList<ItemStack> list) {
        super.fillItemCategory(group, list);
        if (this.allowedIn(group)) {
            ItemStack stack = new ItemStack(ModItems.BLOOD_BOTTLE.get());
            stack.setDamageValue(9);
            list.add(stack);
        }
    }

    @Nullable
    @Override
    public IFaction<?> getExclusiveFaction(@NotNull ItemStack stack) {
        return VReference.VAMPIRE_FACTION;
    }

    @NotNull
    @Override
    public ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level worldIn, @NotNull LivingEntity entityLiving) {
        if (entityLiving instanceof IVampire) {
            int blood = BloodHelper.getBlood(stack);
            int drink = Math.min(blood, MULTIPLIER);
            ItemStack[] result = new ItemStack[1];
            int amt = BloodHelper.drain(stack, drink, IFluidHandler.FluidAction.EXECUTE, true, containerStack -> result[0] = containerStack);
            ((IVampire) entityLiving).drinkBlood(amt / MULTIPLIER, 0, false);
            return result[0];
        }
        return FluidUtil.getFluidHandler(stack).map(IFluidHandlerItem::getContainer).orElseGet(() -> super.finishUsingItem(stack, worldIn, entityLiving));
    }

    @Override
    public int getUseDuration(@NotNull ItemStack stack) {
        return 15;
    }


    @Override
    public ICapabilityProvider initCapabilities(@NotNull ItemStack stack, CompoundTag nbt) {
        return new BloodBottleFluidHandler(stack, capacity);
    }

    @NotNull
    @Override
    public UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public void onUsingTick(@NotNull ItemStack stack, LivingEntity player, int count) {
        if (player instanceof IVampire) return;
        if (!(player instanceof Player) || !player.isAlive()) {
            player.releaseUsingItem();
            return;
        }
        int blood = BloodHelper.getBlood(stack);
        VampirePlayer vampire = VampirePlayer.getOpt((Player) player).resolve().orElse(null);
        if (vampire == null || vampire.getLevel() == 0 || blood == 0 || !vampire.getBloodStats().needsBlood()) {
            player.releaseUsingItem();
            return;
        }

        if (blood > 0 && count == 1) {
            InteractionHand activeHand = player.getUsedItemHand();
            int drink = Math.min(blood, 3 * MULTIPLIER);
            if (BloodHelper.drain(stack, drink, IFluidHandler.FluidAction.EXECUTE, true, containerStack -> player.setItemInHand(activeHand, containerStack)) > 0) {
                vampire.drinkBlood(Math.round(((float) drink) / VReference.FOOD_TO_FLUID_BLOOD), 0.3F, false);
            }

            blood = BloodHelper.getBlood(stack);
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

            if (vampire.getBloodStats().needsBlood() && stack.getCount() == 1) {
                playerIn.startUsingItem(handIn);
                return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
            }
            return new InteractionResultHolder<>(InteractionResult.PASS, stack);
        }).orElse(new InteractionResultHolder<>(InteractionResult.PASS, stack));
    }

    @Override
    public boolean isBarVisible(@NotNull ItemStack stack) {
        return false;
    }
}
