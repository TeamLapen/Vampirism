package de.teamlapen.vampirism.entity.minion.management;


import de.teamlapen.vampirism.api.entity.minion.IMinionTask;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.registries.ForgeRegistryEntry;

import static de.teamlapen.vampirism.entity.minion.management.DefendAreaTask.Desc;


public class DefendAreaTask extends ForgeRegistryEntry<IMinionTask<?>> implements IMinionTask<Desc> {

    @Override
    public Desc activateTask() {
        return null;
    }

    @Override
    public void deactivateTask(Desc desc) {

    }

    @Override
    public Desc readFromNBT(CompoundNBT nbt) {
        return null;
    }

    public static class Desc implements IMinionTaskDesc {

        @Override
        public IMinionTask<?> getTask() {
            return MinionTasks.defend_area;
        }
    }
}
