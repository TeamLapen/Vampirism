package de.teamlapen.vampirism.inventory.container;

import de.teamlapen.lib.lib.inventory.InventoryContainer;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.core.ModContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IWorldPosCallable;
import net.minecraftforge.fml.network.IContainerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * 1.14
 *
 * @author maxanier
 */
public class ExtendedPotionTableContainer extends InventoryContainer {
    public static final SelectorInfo[] SELECTOR_INFOS = new SelectorInfo[]{
            new SelectorInfo(Ingredient.fromItems(Items.BLAZE_POWDER), 44, 34),
            new SelectorInfo(stack -> VampirismAPI.extendedBrewingRecipeRegistry().isValidExtraIngredient(stack), 44, 34),
            new SelectorInfo(stack -> VampirismAPI.extendedBrewingRecipeRegistry().isValidIngredient(stack), 44, 34),
            new SelectorInfo(stack -> VampirismAPI.extendedBrewingRecipeRegistry().isValidInput(stack), 44, 34),
            new SelectorInfo(stack -> VampirismAPI.extendedBrewingRecipeRegistry().isValidInput(stack), 44, 34),
            new SelectorInfo(stack -> VampirismAPI.extendedBrewingRecipeRegistry().isValidInput(stack), 44, 34),
            new SelectorInfo(stack -> VampirismAPI.extendedBrewingRecipeRegistry().isValidInput(stack), 44, 34),
            new SelectorInfo(stack -> VampirismAPI.extendedBrewingRecipeRegistry().isValidInput(stack), 44, 34)};


    private final boolean extended;

    public boolean isExtendedTable() {
        return this.extended;
    }

    @Override
    protected boolean isSlotEnabled(int id) {
        return id < 5 || this.isExtendedTable();
    }

    public ExtendedPotionTableContainer(int id, PlayerInventory playerInventory, IWorldPosCallable worldPos, @Nonnull IInventory inventory, boolean extended) {
        super(ModContainer.extended_potion_table, id, playerInventory, worldPos, inventory, SELECTOR_INFOS);
        assert inventory.getSizeInventory() >= (extended ? 8 : 6);
        this.extended = extended;
    }


    public static class Factory implements IContainerFactory<ExtendedPotionTableContainer> {

        @Nullable
        @Override
        public ExtendedPotionTableContainer create(int windowId, PlayerInventory inv, PacketBuffer data) {
            if (data == null)
                return new ExtendedPotionTableContainer(windowId, inv, IWorldPosCallable.DUMMY, new Inventory(6), false);
            boolean extraSlots = data.readBoolean(); //Anything read here has to be written to buffer in open method (in ExtendedPotionTableTileEntity)
            return new ExtendedPotionTableContainer(windowId, inv, IWorldPosCallable.DUMMY, new Inventory(extraSlots ? 8 : 6), extraSlots);
        }
    }
}
