package de.teamlapen.vampirism.fluids;

import de.teamlapen.vampirism.api.items.IBloodContainerItem;
import de.teamlapen.vampirism.config.Configs;
import de.teamlapen.vampirism.core.ModFluids;
import de.teamlapen.vampirism.core.ModItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

/**
 * Provides several utility methods that are related to blood
 */
public class BloodHelper {

    /**
     * Returns the first {@link IBloodContainerItem} on the players hotbar
     *
     * @param inventory
     * @param onlyNonFull
     * @return
     */
    public static ItemStack getBloodContainerInHotbar(InventoryPlayer inventory, boolean onlyNonFull) {
        int hotbarSize = InventoryPlayer.getHotbarSize();
        for (int i = 0; i < hotbarSize; i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (stack != null && stack.getItem() instanceof IBloodContainerItem) {
                if (!onlyNonFull || (((IBloodContainerItem) stack.getItem()).fill(stack, new FluidStack(ModFluids.blood, 1000), false) > 0)) {
                    return stack;
                }
            }
        }
        return null;
    }

    /**
     * Returns the first glas bottle stack on the players hotbar
     *
     * @param inventory
     * @return
     */
    public static ItemStack getGlassBottleInHotbar(InventoryPlayer inventory) {
        int hotbarSize = InventoryPlayer.getHotbarSize();
        for (int i = 0; i < hotbarSize; i++) {
            ItemStack itemStack = inventory.getStackInSlot(i);
            if (itemStack != null && itemStack.getItem().equals(Items.GLASS_BOTTLE)) {
                return itemStack;
            }
        }
        return null;
    }

    /**
     * Fills the blood in container in the players inventory using multiple possible ways
     *
     * @param player
     * @param amt    Fluid amount in mB
     * @return Blood amount that could not be filled
     */
    public static int fillBloodIntoInventory(EntityPlayer player, int amt) {
        if (amt <= 0) return 0;
        ItemStack stack = getBloodContainerInHotbar(player.inventory, true);
        if (stack != null) {
            int actualAmount = amt - ((IBloodContainerItem) stack.getItem()).fill(stack, new FluidStack(ModFluids.blood, amt), true);
            if (actualAmount > 0) return fillBloodIntoInventory(player, actualAmount);
            return 0;
        }
        ItemStack glas = getGlassBottleInHotbar(player.inventory);
        if (glas != null && Configs.autoConvertGlasBottles) {
            ItemStack bloodBottle = new ItemStack(ModItems.bloodBottle, 1, 0);
            int actualAmount = amt - (ModItems.bloodBottle).fill(bloodBottle, new FluidStack(ModFluids.blood, amt), true);
            glas.stackSize--;
            if (glas.stackSize == 0) {
                player.inventory.deleteStack(glas);
            }
            if (!player.inventory.addItemStackToInventory(bloodBottle)) {
                player.dropItem(bloodBottle, false);
            }
            if (actualAmount > 0) return fillBloodIntoInventory(player, actualAmount);
        }
        return amt;

    }
}
