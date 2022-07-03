package de.teamlapen.vampirism.api.entity.minion;

import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.player.ILordPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Task for minion entity.
 * A task is a registry object and therefore a "Singleton" class. Use {@link ObjectHolder} to retrieve an instance of a registered task
 * For each class there is a {@link IMinionTaskDesc} that holds the state of the task per minion during runtime and can be serialized to NBT.
 * Minions only hold their respective {@link IMinionTaskDesc} which also includes a reference to the task instance it belongs to
 */
public interface IMinionTask<T extends IMinionTask.IMinionTaskDesc<Q>, Q extends IMinionData> {

    /**
     * Called when a new task should be started
     *
     * @param lord   The player entity if loaded
     * @param minion The minion entity if loaded
     * @param data   The minion data. Do not store
     * @return Either a new {@link IMinionTaskDesc} that holds potentially relevant information or null if it was not possible to activate the task (e.g. because the player has to be loaded)
     */
    @Nullable
    T activateTask(@Nullable Player lord, @Nullable IMinionEntity minion, Q data);

    /**
     * Called before another task is activated
     *
     * @param desc The task description for this task
     */
    void deactivateTask(T desc);

    Component getName();

    /**
     * @param faction The faction of the lord
     * @param player  The lord player entity if loaded
     * @return Whether the task can currently be given by the lord player
     */
    default boolean isAvailable(IFaction<?> faction, @Nullable ILordPlayer player) {
        return true;
    }

    /**
     * Read the task description from NBT.
     * Counterpart to {@link IMinionTaskDesc#writeToNBT(CompoundTag)}
     */
    T readFromNBT(CompoundTag nbt);

    /**
     * Tick the task if the minion is loaded.
     * Server side only
     *
     * @param desc         Task description
     * @param minionGetter Getter for the minion entity. Only use if necessary as it's a costly operation. Optional can be empty if there is an issue.
     * @param minionData   The minion data.
     */
    default void tickActive(T desc, @Nonnull Supplier<Optional<IMinionEntity>> minionGetter, @Nonnull Q minionData) {
        this.tickBackground(desc, minionData);
    }

    /**
     * Tick the task if the minion isn't loaded
     * <p>
     * Server side only
     *
     * @param desc       Task description
     * @param minionData The minion data
     */
    default void tickBackground(T desc, @Nonnull Q minionData) {
    }


    /**
     * Hold minion specific state for a task
     */
    interface IMinionTaskDesc<Q extends IMinionData> {
        /**
         * @return The task this belongs to
         */
        IMinionTask<?, Q> getTask();

        /**
         * Write data to NBT. Counterpart to {@link IMinionTask#readFromNBT(CompoundTag)}
         */
        default void writeToNBT(CompoundTag nbt) {
        }


    }

    /**
     * Can be used if the task is stateless and therefore does not need to store any information
     */
    @SuppressWarnings("ClassCanBeRecord")
    class NoDesc<Q extends IMinionData> implements IMinionTaskDesc<Q> {
        private final IMinionTask<NoDesc<Q>, Q> task;

        public NoDesc(IMinionTask<NoDesc<Q>, Q> task) {
            this.task = task;
        }


        @Override
        public IMinionTask<NoDesc<Q>, Q> getTask() {
            return task;
        }
    }

}
