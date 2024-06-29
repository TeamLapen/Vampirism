package de.teamlapen.vampirism.items;

import de.teamlapen.lib.lib.util.ModDisplayItemGenerator;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.vampire.IVampire;
import de.teamlapen.vampirism.api.items.IFactionExclusiveItem;
import de.teamlapen.vampirism.core.ModDataComponents;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.core.tags.ModFactionTags;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.entity.vampire.DrinkBloodContext;
import de.teamlapen.vampirism.fluids.BloodHelper;
import de.teamlapen.vampirism.items.component.BottleBlood;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
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
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import org.jetbrains.annotations.NotNull;

/**
 * Stores blood
 * Currently the only thing that can interact with the players bloodstats.
 * Can only store blood in {@link BloodBottleItem#CAPACITY} tenth units.
 */
public class BloodBottleItem extends Item implements IFactionExclusiveItem, ModDisplayItemGenerator.CreativeTabItemProvider {

    public static final int AMOUNT = 9;
    private static final int MULTIPLIER = VReference.FOOD_TO_FLUID_BLOOD;
    public static final int CAPACITY = AMOUNT * MULTIPLIER;

    public static @NotNull ItemStack getStackWithDamage(int damage) {
        ItemStack stack = new ItemStack(ModItems.BLOOD_BOTTLE.get());
        stack.set(ModDataComponents.BOTTLE_BLOOD, new BottleBlood(damage));
        return stack;
    }

    /**
     * Set's the registry name and the unlocalized name
     */
    public BloodBottleItem() {
        super(new Properties());
    }

    @Override
    public boolean isEnchantable(@NotNull ItemStack stack) {
        return false;
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return false;
    }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, @NotNull LevelReader world, @NotNull BlockPos pos, Player player) {
        if (world instanceof Level level) {
            return level.getCapability(Capabilities.FluidHandler.BLOCK, pos, null) != null;
        }
        return false;
    }

    @Override
    public void generateCreativeTab(CreativeModeTab.@NotNull ItemDisplayParameters parameters, CreativeModeTab.Output output) {
        for (int i = 0; i <= BloodBottleItem.AMOUNT; i++) {
            ItemStack stack = getDefaultInstance();
            stack.set(ModDataComponents.BOTTLE_BLOOD, new BottleBlood(i));
            output.accept(stack, i == 0 || i == BloodBottleItem.AMOUNT ? CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS : CreativeModeTab.TabVisibility.SEARCH_TAB_ONLY);
        }
    }

    @Override
    public @NotNull TagKey<IFaction<?>> getExclusiveFaction(@NotNull ItemStack stack) {
        return ModFactionTags.IS_VAMPIRE;
    }

    @NotNull
    @Override
    public ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level worldIn, @NotNull LivingEntity entityLiving) {
        if (entityLiving instanceof IVampire) {
            ItemStack copy = stack.copy();
            int blood = BloodHelper.getBlood(stack);
            int drink = Math.min(blood, MULTIPLIER);
            ItemStack[] result = new ItemStack[1];
            int amt = BloodHelper.drain(stack, drink, IFluidHandler.FluidAction.EXECUTE, true, containerStack -> result[0] = containerStack);
            ((IVampire) entityLiving).drinkBlood(amt / MULTIPLIER, 0, new DrinkBloodContext(copy));
            return result[0];
        }
        return FluidUtil.getFluidHandler(stack).map(IFluidHandlerItem::getContainer).orElseGet(() -> super.finishUsingItem(stack, worldIn, entityLiving));
    }

    @Override
    public int getUseDuration(ItemStack pStack, LivingEntity p_344979_) {
        return 15;
    }

    @NotNull
    @Override
    public UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public void onUseTick(@NotNull Level level, @NotNull LivingEntity pLivingEntity, @NotNull ItemStack stack, int count) {
        if (pLivingEntity instanceof IVampire) return;
        if (!(pLivingEntity instanceof Player) || !pLivingEntity.isAlive()) {
            pLivingEntity.releaseUsingItem();
            return;
        }
        ItemStack copy = stack.copy();
        int blood = BloodHelper.getBlood(stack);
        VampirePlayer vampire = VampirePlayer.get((Player) pLivingEntity);
        if (vampire.getLevel() == 0 || blood == 0 || !vampire.getBloodStats().needsBlood()) {
            pLivingEntity.releaseUsingItem();
            return;
        }

        if (blood > 0 && count == 1) {
            InteractionHand activeHand = pLivingEntity.getUsedItemHand();
            int drink = Math.min(blood, 3 * MULTIPLIER);
            if (BloodHelper.drain(stack, drink, IFluidHandler.FluidAction.EXECUTE, true, containerStack -> pLivingEntity.setItemInHand(activeHand, containerStack)) > 0) {
                vampire.drinkBlood(Math.round(((float) drink) / VReference.FOOD_TO_FLUID_BLOOD), 0.45F, false, new DrinkBloodContext(copy));
            }

            blood = BloodHelper.getBlood(stack);
            if (blood > 0) {
                pLivingEntity.startUsingItem(pLivingEntity.getUsedItemHand());
            }
        }
    }

    @NotNull
    @Override
    public InteractionResultHolder<ItemStack> use(@NotNull Level worldIn, @NotNull Player playerIn, @NotNull InteractionHand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);
        VampirePlayer vampire = VampirePlayer.get(playerIn);
        if (vampire.getLevel() == 0) return new InteractionResultHolder<>(InteractionResult.PASS, stack);

        if (vampire.getBloodStats().needsBlood() && stack.getCount() == 1) {
            playerIn.startUsingItem(handIn);
            return new InteractionResultHolder<>(InteractionResult.sidedSuccess(worldIn.isClientSide), stack);
        }
        return new InteractionResultHolder<>(InteractionResult.PASS, stack);
    }

    @Override
    public boolean isBarVisible(@NotNull ItemStack stack) {
        return false;
    }
}
