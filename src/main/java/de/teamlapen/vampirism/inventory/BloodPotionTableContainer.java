package de.teamlapen.vampirism.inventory;

import de.teamlapen.lib.lib.inventory.InventoryContainer;
import de.teamlapen.lib.lib.inventory.InventorySlot;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.entity.player.hunter.HunterPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * Table to create blood potions
 */
public class BloodPotionTableContainer extends Container {
    private final BlockPos pos;
    private final HunterPlayer hunterPlayer;
    private final World world;
    private final IInventory inventory = new InventoryBasic("vampirism.blood_potion_table", false, 4);

    public BloodPotionTableContainer(InventoryPlayer playerInventory, BlockPos pos, World world) {

        this.pos = pos;
        this.hunterPlayer = HunterPlayer.get(playerInventory.player);
        this.world = world;

        InventorySlot.IItemSelector bloodfilter = new InventorySlot.IItemSelector() {
            @Override
            public boolean isItemAllowed(@Nonnull ItemStack item) {
                return ModItems.vampireBlood.equals(item.getItem());
            }
        };

        this.addSlotToContainer(new InventoryContainer.FilterSlot(inventory, 0, 115, 55, bloodfilter));
        this.addSlotToContainer(new InventoryContainer.FilterSlot(inventory, 1, 137, 55, bloodfilter));
        this.addSlotToContainer(new InventoryContainer.FilterSlot(inventory, 2, 126, 14, new InventorySlot.IItemSelector() {
            @Override
            public boolean isItemAllowed(ItemStack item) {
                return ModItems.itemGarlic.equals(item.getItem());
            }
        }));
        this.addSlotToContainer(new Slot(inventory, 3, 101, 22));

        for (int k = 0; k < 3; ++k) {
            for (int i1 = 0; i1 < 9; ++i1) {
                this.addSlotToContainer(new Slot(playerInventory, i1 + k * 9 + 9, 8 + i1 * 18, 84 + k * 18));
            }
        }

        for (int l = 0; l < 9; ++l) {
            this.addSlotToContainer(new Slot(playerInventory, l, 8 + l * 18, 142));
        }
    }

    public boolean canCurrentlyCraft() {
        return true;
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return playerIn.getDistanceSq((double) this.pos.getX() + 0.5D, (double) this.pos.getY() + 0.5D, (double) this.pos.getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        super.onContainerClosed(playerIn);

        if (!this.world.isRemote) {

            for (int i = 0; i < inventory.getSizeInventory(); ++i) {
                ItemStack itemstack = this.inventory.removeStackFromSlot(i);

                if (itemstack != null) {
                    playerIn.dropItem(itemstack, false);
                }
            }
        }
    }

    public void onCraftingClicked() {
        VampirismMod.log.t("Crafting clicked");
    }
}
