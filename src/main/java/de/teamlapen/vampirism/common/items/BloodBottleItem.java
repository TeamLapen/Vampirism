package de.teamlapen.vampirism.common.items;

import de.teamlapen.lib.common.items.BaseDisplayItemGenerator;
import de.teamlapen.vampirism.api.components.IBottleBlood;
import de.teamlapen.vampirism.common.core.ModDataComponents;
import de.teamlapen.vampirism.common.core.ModItems;
import de.teamlapen.vampirism.common.entity.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.common.entity.vampire.DrinkBloodContext;
import de.teamlapen.vampirism.common.items.component.BottleBlood;
import de.teamlapen.vampirism.common.util.BloodHelper;
import de.teamlapen.vampirism.common.util.Helper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;

import static de.teamlapen.vampirism.api.components.IBottleBlood.MULTIPLIER;

/**
 * Can only store blood in {@link BloodBottleItem#CAPACITY} tenth units.
 */
public class BloodBottleItem extends Item implements BaseDisplayItemGenerator.CreativeTabItemProvider {

    public static final int AMOUNT = IBottleBlood.MAX_VALUE;
    public static final int CAPACITY = AMOUNT * MULTIPLIER;

    public static ItemStack createStackWithBlood(int blood) {
        ItemStack stack = new ItemStack(ModItems.BLOOD_BOTTLE.get());
        stack.set(ModDataComponents.BOTTLE_BLOOD, new BottleBlood(blood));
        return stack;
    }

    public static BottleBlood getBloodContents(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.BOTTLE_BLOOD, BottleBlood.EMPTY);
    }

    public static int getBlood(ItemStack stack) {
        return getBloodContents(stack).blood();
    }

    public BloodBottleItem(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        if (livingEntity instanceof Player player && Helper.isVampire(livingEntity)) {
            VampirePlayer vampire = VampirePlayer.get(player);
            ItemStack consumed = stack.copyWithCount(1);
            int blood = BloodHelper.getBlood(consumed);

            if (blood > 0) {
                ItemStack[] leftover = new ItemStack[1];
                BloodHelper.drain(consumed, blood, IFluidHandler.FluidAction.EXECUTE, true, container -> leftover[0] = container);
                vampire.drinkBlood(blood / MULTIPLIER, 0, new DrinkBloodContext(consumed).setReturnsSpareBlood(false));

                if (stack.getCount() == 1) {
                    return leftover[0];
                } else {
                    stack.shrink(1);
                    if (!player.getInventory().add(leftover[0])) {
                        player.drop(leftover[0], false);
                    }
                    return stack;
                }
            }
        }

        return FluidUtil.getFluidHandler(stack).map(IFluidHandlerItem::getContainer).orElseGet(() -> super.finishUsingItem(stack, level, livingEntity));
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        VampirePlayer vampire = VampirePlayer.get(player);
        if (vampire.getLevel() == 0) return InteractionResult.PASS;

        ItemStack stack = player.getItemInHand(hand);
        if (vampire.getBloodStats().needsBlood() && getBlood(stack) > 0) {
            player.startUsingItem(hand);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, LevelReader levelReader, BlockPos pos, Player player) {
        if (levelReader instanceof Level level) {
            return level.getCapability(Capabilities.Fluid.BLOCK, pos, null) != null;
        }
        return false;
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        int blood = getBlood(stack);
        return blood == 0 || blood == AMOUNT ? 16 : 1;
    }

    @Override
    public void generateCreativeTab(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output) {
        for (int i = 0; i <= AMOUNT; i++) {
            output.accept(createStackWithBlood(i), i == 0 || i == AMOUNT ? CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS : CreativeModeTab.TabVisibility.SEARCH_TAB_ONLY);
        }
    }
}
