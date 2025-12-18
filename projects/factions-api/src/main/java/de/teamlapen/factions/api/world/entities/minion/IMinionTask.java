package de.teamlapen.factions.api.world.entities.minion;

import com.mojang.serialization.Codec;
import de.teamlapen.factions.api.factions.lord.ILordPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Task for minion entity.
 * A task is a registry object and therefore a "Singleton" class. Use {@link net.neoforged.neoforge.registries.DeferredHolder} to retrieve an instance of a registered task
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

    default MutableComponent getName() {
        return Component.translatable(getDescriptionId());
    }

    String getDescriptionId();

    /**
     * @param player  The lord player entity if loaded
     * @return Whether the task can currently be given by the lord player
     */
    default boolean isAvailable(ILordPlayer<?> player) {
        return true;
    }

    /**
     * Read the task description from NBT.
     * Counterpart to {@link IMinionTaskDesc#serialize(ValueOutput)}
     */
    T load(ValueInput input);

    Codec<T> descriptionCodec();

    /**
     * Tick the task if the minion is loaded.
     * Server side only
     *
     * @param desc         Task description
     * @param minionGetter Getter for the minion entity. Only use if necessary as it's a costly operation. Optional can be empty if there is an issue.
     * @param minionData   The minion data.
     */
    default void tickActive(T desc, Supplier<Optional<IMinionEntity>> minionGetter, Q minionData) {
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
    default void tickBackground(T desc, Q minionData) {
    }


    /**
     * Hold minion specific state for a task
     */
    interface IMinionTaskDesc<Q extends IMinionData> {

        Codec<IMinionTask.IMinionTaskDesc<IMinionData>> TASK_CODEC = new MinionTaskCodec<>();

        /**
         * @return The task this belongs to
         */
        IMinionTask<?, Q> getTask();

        default void serialize(ValueOutput output) {

        }

    }

    /**
     * Can be used if the task is stateless and therefore does not need to store any information
     */
    @SuppressWarnings("ClassCanBeRecord")
    class NoDesc<Q extends IMinionData> implements IMinionTaskDesc<Q> {

        public static <Q extends IMinionData> Codec<NoDesc<Q>> codec(Supplier<? extends IMinionTask<NoDesc<Q>, Q>> task) {
            return Codec.unit(() -> new NoDesc<>(task.get()));
        }

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
