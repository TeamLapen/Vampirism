package de.teamlapen.vampirism.inventory;

import de.teamlapen.lib.lib.inventory.InventoryContainer;
import de.teamlapen.lib.lib.inventory.InventorySlot;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * 1.10
 *
 * @author maxanier
 */
public class AlchemicalCauldronContainer extends InventoryContainer {

    private int cookTime;
    private int totalCookTime;
    private int furnaceBurnTime;
    private int currentItemBurnTime;

    public AlchemicalCauldronContainer(InventoryPlayer invPlayer, InventorySlot.IInventorySlotInventory te) {
        super(invPlayer, te);
    }

    @Override
    public void addListener(IContainerListener listener) {
        super.addListener(listener);
        listener.sendAllWindowProperties(this, this.tile);
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();

        for (int i = 0; i < this.listeners.size(); ++i) {
            IContainerListener icontainerlistener = this.listeners.get(i);

            if (this.cookTime != this.tile.getField(2)) {
                icontainerlistener.sendWindowProperty(this, 2, this.tile.getField(2));
            }

            if (this.furnaceBurnTime != this.tile.getField(0)) {
                icontainerlistener.sendWindowProperty(this, 0, this.tile.getField(0));
            }

            if (this.currentItemBurnTime != this.tile.getField(1)) {
                icontainerlistener.sendWindowProperty(this, 1, this.tile.getField(1));
            }

            if (this.totalCookTime != this.tile.getField(3)) {
                icontainerlistener.sendWindowProperty(this, 3, this.tile.getField(3));
            }
        }

        this.cookTime = this.tile.getField(2);
        this.furnaceBurnTime = this.tile.getField(0);
        this.currentItemBurnTime = this.tile.getField(1);
        this.totalCookTime = this.tile.getField(3);
    }

    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int data) {
        this.tile.setField(id, data);
    }
}
