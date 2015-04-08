package de.teamlapen.vampirism.tileEntity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import de.teamlapen.vampirism.util.Logger;

/**
 * Basic abstract class for TileEntitys which need a small inventory (with an gui)
 * 
 * @author Maxanier
 *
 */
public abstract class InventoryTileEntity extends TileEntity implements IInventory {

	public static interface IItemSelector {
		/**
		 * @param item
		 * @return whether the item is allowed or not
		 */
		public boolean isItemAllowed(ItemStack item);
	}
	
	public static class FilterSlot extends net.minecraft.inventory.Slot{
		IItemSelector selector;
		public FilterSlot(IInventory p_i1824_1_, int p_i1824_2_, int p_i1824_3_, int p_i1824_4_,IItemSelector selector) {
			super(p_i1824_1_, p_i1824_2_, p_i1824_3_, p_i1824_4_);
			this.selector=selector;
		}
		@Override
		public boolean isItemValid(ItemStack stack){
			if(selector!=null){
				return selector.isItemAllowed(stack);
			}
			return true;
		}
		
	}

	/**
	 * Used for handling the item exchange between player and block inventory. Should be created with InventoryTileEntity.getNewInventoryContainer()
	 * 
	 * @author Maxanier
	 *
	 */
	public static class InventoryContainer extends Container {

		private InventoryTileEntity tile;

		public InventoryContainer(InventoryPlayer invPlayer, InventoryTileEntity te) {
			tile = te;

			for (int i = 0; i < tile.slots.length; i++) {
				this.addSlotToContainer(new FilterSlot(tile, i, tile.slots[i].xDisplay, tile.slots[i].yDisplay,tile.slots[i].itemSelector));
			}

			int i;
			for (i = 0; i < 3; ++i) {
				for (int j = 0; j < 9; ++j) {
					this.addSlotToContainer(new net.minecraft.inventory.Slot(invPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
				}
			}
			for (i = 0; i < 9; ++i) {
				this.addSlotToContainer(new net.minecraft.inventory.Slot(invPlayer, i, 8 + i * 18, 142));
			}

		}

		@Override
		public boolean canInteractWith(EntityPlayer p_75145_1_) {
			return tile.isUseableByPlayer(p_75145_1_);
		}

		@Override
		public ItemStack transferStackInSlot(EntityPlayer player, int slot) {
			ItemStack stack = null;
			net.minecraft.inventory.Slot slotObject = (net.minecraft.inventory.Slot) inventorySlots.get(slot);

			// null checks and checks if the item can be stacked (maxStackSize > 1)
			if (slotObject != null && slotObject.getHasStack()) {
				ItemStack stackInSlot = slotObject.getStack();
				stack = stackInSlot.copy();
				// merges the item into player inventory since its in the tileEntity
				if (slot < tile.slots.length) {
					if (!this.mergeItemStack(stackInSlot, tile.slots.length, tile.slots.length + 36, true)) {
						return null;
					}
				}
				// places it into the tileEntity is possible since its in the player inventory
				else if (!this.mergeItemStack(stackInSlot, 0, tile.slots.length, false)) {
					return null;
				}

				if (stackInSlot.stackSize == 0) {
					slotObject.putStack(null);
				} else {
					slotObject.onSlotChanged();
				}

				if (stackInSlot.stackSize == stack.stackSize) {
					return null;
				}
				slotObject.onPickupFromSlot(player, stackInSlot);
			}
			return stack;
		}
	}

	/**
	 * Used for inventory slot description as well as for storing the actual inventory
	 * 
	 * @author Maxanier
	 *
	 */
	public static class Slot {
		public final IItemSelector itemSelector;
		public final int xDisplay, yDisplay;
		public ItemStack stack;

		public Slot(final Class<? extends Item> cls, int xDisplay, int yDisplay) {
			this(new IItemSelector() {

				@Override
				public boolean isItemAllowed(ItemStack item) {
					return cls.isInstance(item.getItem());
				}

			}, xDisplay, yDisplay);
		}
		
		public Slot(final Item item, int xDisplay, int yDisplay) {
			this(new IItemSelector() {
				@Override
				public boolean isItemAllowed(ItemStack stack) {
					return item.equals(stack.getItem());
				}

			}, xDisplay, yDisplay);
		}

		public Slot(IItemSelector selector, int xDisplay, int yDisplay) {
			itemSelector = selector;
			this.xDisplay = xDisplay;
			this.yDisplay = yDisplay;
		}

		public Slot(int xDisplay, int yDisplay) {
			this((IItemSelector) null, xDisplay, yDisplay);
		}
	}

	/**
	 * Maximal squared distance from which the player can access the inventory
	 */
	protected final int MAX_DIST_SQRT = 40;

	private Slot[] slots;

	/**
	 * @param slots
	 *            A slot 'description'. The array should contain one Slot instance for each inventory slot which should be created. The slots must each contain the position where they should be
	 *            displayed in the GUI, they can also contain a filter for which items are allowed. Make sure that these Slot instance are unique on server and client.
	 * @see de.teamlapen.vampirism.tileEntity.TileEntityBloodAltar4
	 */
	public InventoryTileEntity(Slot[] slots) {
		this.slots = slots;
	}

	@Override
	public void closeInventory() {

	}

	@Override
	public ItemStack decrStackSize(int slot, int amt) {
		ItemStack stack = getStackInSlot(slot);
		if (stack != null) {
			if (stack.stackSize <= amt) {
				setInventorySlotContents(slot, null);
			} else {
				stack = stack.splitStack(amt);
				if (stack.stackSize == 0) {
					setInventorySlotContents(slot, null);
				}
			}
			return stack;
		}
		return null;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	public Container getNewInventoryContainer(InventoryPlayer inv) {
		return new InventoryContainer(inv, this);
	}
	
	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	@Override
	public int getSizeInventory() {
		return slots.length;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return slots[slot].stack;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		ItemStack stack = getStackInSlot(slot);
		if (stack != null) {
			setInventorySlotContents(slot, null);
			return stack;
		}
		return null;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		Logger.i("test", "testing if valid "+stack+" "+slots[slot].itemSelector.isItemAllowed(stack));
		if (slots[slot].itemSelector != null){
			return slots[slot].itemSelector.isItemAllowed(stack);
		}
		return true;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return player.getDistanceSq(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5) < MAX_DIST_SQRT;
	}

	@Override
	public void openInventory() {

	}

	@Override
	public void readFromNBT(NBTTagCompound tagCompound) {
		super.readFromNBT(tagCompound);
		for (int i = 0; i < slots.length; i++) {
			slots[i].stack = null;
		}
		NBTTagList tagList = tagCompound.getTagList("Inventory", 10);
		for (int i = 0; i < tagList.tagCount(); i++) {
			NBTTagCompound tag = tagList.getCompoundTagAt(i);
			byte slot = tag.getByte("Slot");
			if (slot >= 0 && slot < slots.length) {
				slots[slot].stack = ItemStack.loadItemStackFromNBT(tag);
			}
		}
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		slots[slot].stack = stack;
		if (stack != null && stack.stackSize > getInventoryStackLimit()) {
			stack.stackSize = getInventoryStackLimit();
		}

	}

	@Override
	public void writeToNBT(NBTTagCompound tagCompound) {
		super.writeToNBT(tagCompound);

		NBTTagList itemList = new NBTTagList();
		for (int i = 0; i < slots.length; i++) {
			ItemStack stack = slots[i].stack;
			if (stack != null) {
				NBTTagCompound tag = new NBTTagCompound();
				tag.setByte("Slot", (byte) i);
				stack.writeToNBT(tag);
				itemList.appendTag(tag);
			}
		}
		tagCompound.setTag("Inventory", itemList);
	}
	

}
