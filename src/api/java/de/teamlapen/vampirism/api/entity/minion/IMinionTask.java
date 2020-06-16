package de.teamlapen.vampirism.api.entity.minion;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.registries.IForgeRegistryEntry;

/**
 * Task for minion entity
 */
public interface IMinionTask<T extends IMinionTask.IMinionTaskDesc> extends IForgeRegistryEntry<IMinionTask<?>> {

    T activateTask();

    void deactivateTask(T desc);

    T readFromNBT(CompoundNBT nbt);

    default void tickActive(T desc) {
        this.tickBackground(desc);
    }

    default void tickBackground(T desc) {
    }

    interface IMinionTaskDesc {
        IMinionTask<?> getTask();

        default void writeToNBT(CompoundNBT nbt) {
        }
    }

    class NoDesc implements IMinionTaskDesc {
        private final IMinionTask<NoDesc> task;

        public NoDesc(IMinionTask<NoDesc> task) {
            this.task = task;
        }

        @Override
        public IMinionTask<?> getTask() {
            return task;
        }
    }

}
