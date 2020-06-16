package de.teamlapen.vampirism.entity.minion.management;

import de.teamlapen.vampirism.api.entity.minion.IMinionTask;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.registries.ForgeRegistryEntry;


public class SimpleMinionTask extends ForgeRegistryEntry<IMinionTask<?>> implements IMinionTask<IMinionTask.NoDesc> {

    @Override
    public NoDesc activateTask() {
        return new NoDesc(this);
    }

    @Override
    public void deactivateTask(NoDesc desc) {

    }

    @Override
    public NoDesc readFromNBT(CompoundNBT nbt) {
        return new NoDesc(this);
    }
}
