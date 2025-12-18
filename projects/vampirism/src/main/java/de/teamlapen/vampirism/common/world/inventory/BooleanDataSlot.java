package de.teamlapen.vampirism.common.world.inventory;

import net.minecraft.world.inventory.DataSlot;

public class BooleanDataSlot extends DataSlot {

    private boolean bValue;

    @Deprecated
    @Override
    public int get() {
        return this.bValue ? 1 : 0;
    }

    @Deprecated
    @Override
    public void set(int pValue) {
        this.bValue = pValue == 1;
    }

    public boolean getAsBoolean() {
        return this.bValue;
    }

    public void set(boolean pValue) {
        this.bValue = pValue;
    }
}
