package de.teamlapen.vampirism.api.entity.minion;

import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.ILordPlayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Task for minion entity
 */
public interface IMinionTask<T extends IMinionTask.IMinionTaskDesc> extends IForgeRegistryEntry<IMinionTask<?>> {

    @Nullable
    T activateTask(@Nullable PlayerEntity lord, @Nullable IMinionEntity minion, IMinionInventory inventory);

    void deactivateTask(T desc);

    ITextComponent getName();

    default boolean isAvailable(IPlayableFaction<?> faction, @Nullable ILordPlayer player) {
        return true;
    }

    T readFromNBT(CompoundNBT nbt);

    default void tickActive(T desc, @Nonnull Supplier<Optional<IMinionEntity>> minionGetter, @Nonnull IMinionInventory inventory) {
        this.tickBackground(desc, inventory);
    }

    default void tickBackground(T desc, @Nonnull IMinionInventory inventory) {
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
        public IMinionTask<NoDesc> getTask() {
            return task;
        }
    }

}
