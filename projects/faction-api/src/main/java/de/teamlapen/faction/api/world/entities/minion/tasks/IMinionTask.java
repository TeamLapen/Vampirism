package de.teamlapen.faction.api.world.entities.minion.tasks;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import de.teamlapen.faction.api.factions.IFaction;
import de.teamlapen.faction.api.factions.lord.ILordPlayer;
import de.teamlapen.faction.api.world.entities.minion.IMinionData;
import de.teamlapen.faction.api.world.entities.minion.IMinionEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Task for minion entity.
 * A task is a registry object and therefore a "Singleton" class. Use {@link net.neoforged.neoforge.registries.DeferredHolder} to retrieve an instance of a registered task
 * For each class there is a {@link de.teamlapen.faction.api.world.entities.minion.tasks.IMinionTask.IMinionTaskState} that holds the state of the task per minion during runtime and can be serialized to NBT.
 * Minions only hold their respective {@link de.teamlapen.faction.api.world.entities.minion.tasks.IMinionTask.IMinionTaskState} which also includes a reference to the task instance it belongs to
 */
public interface IMinionTask<Q extends IMinionData, T extends IMinionTask.IMinionTaskState> {

    /**
     * Called when a new task should be started
     *
     * @param lord   The player entity if loaded
     * @param minion The minion entity if loaded
     * @param data   The minion data. Do not store
     * @return Either a new {@link de.teamlapen.faction.api.world.entities.minion.tasks.IMinionTask.IMinionTaskState} that holds potentially relevant information or null if it was not possible to activate the task (e.g. because the player has to be loaded)
     */
    ActivateResult<T> activateTask(@Nullable Player lord, @Nullable IMinionEntity minion, Q data);

    /**
     * Called before another task is activated
     *
     * @param desc The task description for this task
     */
    void deactivateTask(T desc);

    Component getName();

    String getNameId();

    TagKey<IFaction<?>> allowedFactions();

    /**
     * @param player  The lord player entity if loaded
     * @return Whether the task can currently be given by the lord player
     */
    boolean isAvailable(ILordPlayer player);

    @Nullable
    Codec<T> stateCodec();

    /**
     * Tick the task if the minion is loaded.
     * Server side only
     *
     * @param desc         Task description
     * @param minionGetter Getter for the minion entity. Only use if necessary as it's a costly operation. Optional can be empty if there is an issue.
     * @param minionData   The minion data.
     */
    void tickActive(T desc, Supplier<Optional<IMinionEntity>> minionGetter, Q minionData);

    /**
     * Tick the task if the minion isn't loaded
     * <p>
     * Server side only
     *
     * @param desc       Task description
     * @param minionData The minion data
     */
    void tickBackground(T desc, Q minionData);


    /**
     * Hold minion specific state for a task
     */
    interface IMinionTaskState {

    }

    final class EmptyState implements IMinionTask.IMinionTaskState {
        public static final  EmptyState INSTANCE = new EmptyState();
    }

    record ActivateResult<T extends IMinionTask.IMinionTaskState>(@Nullable T data) {

        public static final ActivateResult<?> FAILED = new ActivateResult<>(null);

        @SuppressWarnings("unchecked")
        public static <T extends IMinionTask.IMinionTaskState> ActivateResult<T> failed() {
            return (ActivateResult<T>) FAILED;
        }

        public boolean successful() {
            return data != null;
        }
    }
}
