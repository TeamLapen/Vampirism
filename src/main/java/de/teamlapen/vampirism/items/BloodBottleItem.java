package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.vampire.IVampire;
import de.teamlapen.vampirism.api.items.IFactionExclusiveItem;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.fluids.BloodHelper;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.item.Item;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

import javax.annotation.Nonnull;

/**
 * Stores blood
 * Currently the only thing that can interact with the players bloodstats.
 * Can only store blood in {@link BloodBottleItem#capacity} tenth units.
 */
public class BloodBottleItem extends Item implements IFactionExclusiveItem {

    public static final int AMOUNT = 9;
    private static final int MULTIPLIER = VReference.FOOD_TO_FLUID_BLOOD;
    private static final int capacity = AMOUNT * MULTIPLIER;

    public static ItemStack getStackWithDamage(int damage) {
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
    public boolean doesSneakBypassUse(ItemStack stack, IWorldReader world, BlockPos pos, PlayerEntity player) {
        TileEntity t = world.getBlockEntity(pos);
        return t != null && t.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null).isPresent();
    }

    @Override
    public void fillItemCategory(@Nonnull ItemGroup group, @Nonnull NonNullList<ItemStack> list) {
        super.fillItemCategory(group, list);
        if (this.allowdedIn(group)) {
            ItemStack stack = new ItemStack(ModItems.BLOOD_BOTTLE.get());
            stack.setDamageValue(9);
            list.add(stack);
        }
    }

    @Nonnull
    @Override
    public IFaction<?> getExclusiveFaction() {
        return VReference.VAMPIRE_FACTION;
    }

    @Nonnull
    @Override
    public ItemStack finishUsingItem(@Nonnull ItemStack stack, @Nonnull World worldIn, @Nonnull LivingEntity entityLiving) {
        if (entityLiving instanceof IVampire) {
            int blood = BloodHelper.getBlood(stack);
            int drink = Math.min(blood, MULTIPLIER);
            ItemStack[] result = new ItemStack[1];
            int amt = BloodHelper.drain(stack, drink, IFluidHandler.FluidAction.EXECUTE, true, containerStack -> {
                result[0] = containerStack;
            });
            ((IVampire) entityLiving).drinkBlood(amt / MULTIPLIER, 0, false);
            return result[0];
        }
        return FluidUtil.getFluidHandler(stack).map(IFluidHandlerItem::getContainer).orElseGet(() -> super.finishUsingItem(stack, worldIn, entityLiving));
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 15;
    }


    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundNBT nbt) {
        return new BloodBottleFluidHandler(stack, capacity);
    }

    @Nonnull
    @Override
    public UseAction getUseAnimation(ItemStack stack) {
        return UseAction.DRINK;
    }

    @Override
    public void onUsingTick(ItemStack stack, LivingEntity player, int count) {
        if (player instanceof IVampire) return;
        if (!(player instanceof PlayerEntity) || !player.isAlive()) {
            player.releaseUsingItem();
            return;
        }
        int blood = BloodHelper.getBlood(stack);
        VampirePlayer vampire = VampirePlayer.getOpt((PlayerEntity) player).resolve().orElse(null);
        if (vampire == null  || vampire.getLevel() == 0 || blood == 0 || !vampire.getBloodStats().needsBlood()) {
            player.releaseUsingItem();
            return;
        }

        if (blood > 0 && count == 1) {
            Hand activeHand = player.getUsedItemHand();
            int drink = Math.min(blood, 3 * MULTIPLIER);
            if (BloodHelper.drain(stack, drink, IFluidHandler.FluidAction.EXECUTE, true, containerStack -> {
                player.setItemInHand(activeHand, containerStack);
            }) > 0) {
                vampire.drinkBlood(Math.round(((float) drink) / VReference.FOOD_TO_FLUID_BLOOD), 0.3F, false);
            }

            blood = BloodHelper.getBlood(stack);
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

            if (vampire.getBloodStats().needsBlood() && stack.getCount() == 1) {
                playerIn.startUsingItem(handIn);
                return new ActionResult<>(ActionResultType.SUCCESS, stack);
            }
            return new ActionResult<>(ActionResultType.PASS, stack);
        }).orElse(new ActionResult<>(ActionResultType.PASS, stack));
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return false;
    }
}
