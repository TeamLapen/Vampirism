package de.teamlapen.vampirism.inventory.diffuser;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class DiffuserFuelSlot extends Slot {

    private final DiffuserMenu diffuserMenu;

    public DiffuserFuelSlot(DiffuserMenu diffuserMenu, Container pContainer, int pSlot, int pX, int pY) {
        super(pContainer, pSlot, pX, pY);
        this.diffuserMenu = diffuserMenu;
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack pStack) {
        return this.diffuserMenu.isFuel(pStack);
    }

}
