package de.teamlapen.vampirism.tileentity;

import de.teamlapen.lib.lib.inventory.InventorySlot;
import de.teamlapen.lib.lib.tile.InventoryTileEntity;
import de.teamlapen.vampirism.blocks.BlockPedestal;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ITickable;

public class TilePedestal extends InventoryTileEntity implements ITickable {

    public final ItemStack test;

    public TilePedestal() {
        super(new InventorySlot[]{new InventorySlot(0, 0)});
        test = new ItemStack(Items.IRON_SWORD);
    }

    @Override
    public void update() {

    }

    @Override
    public String getName() {
        return "vampirism.container." + BlockPedestal.regName;
    }
}
