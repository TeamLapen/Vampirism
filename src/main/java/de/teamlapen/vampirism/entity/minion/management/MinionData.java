package de.teamlapen.vampirism.entity.minion.management;

import net.minecraft.inventory.Inventory;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * 1.14
 *
 * @author maxanier
 */
public class MinionData implements INBTSerializable<CompoundNBT> {

    private Inventory inventory;
    private float health;
    private String name;

    @Override
    public void deserializeNBT(CompoundNBT nbt) {

    }

    @Override
    public CompoundNBT serializeNBT() {
        return new CompoundNBT();
    }
}
