package de.teamlapen.factions.api.factions.lord;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import de.teamlapen.factions.api.world.entities.minion.IMinionData;
import de.teamlapen.factions.api.world.entities.minion.IMinionEntity;
import de.teamlapen.factions.api.world.entities.minion.IMinionEntry;
import de.teamlapen.factions.api.world.entities.player.IFactionPlayer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.entity.EntityType;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface IMinionEntryBuilder<T extends IFactionPlayer<T>, Z extends IMinionData> {

    /**
     * Register a command for the minion
     * <p>
     * This makes the minion possible to spawn using the minion command
     */
    IMinionEntryBuilder<T, Z> commandBuilder(Supplier<? extends EntityType<? extends IMinionEntity>> type, Consumer<IMinionCommandBuilder<T, Z>> builder);

    IMinionEntry<T, Z> build();

    interface IMinionCommandBuilder<T extends IFactionPlayer<T>, Z extends IMinionData> {

        /**
         * add argument to the minion command
         */
        <L> IMinionCommandBuilder<T, Z> with(String name, L defaultValue, ArgumentType<L> type, BiConsumer<Z, L> setter, BiFunction<CommandContext<CommandSourceStack>, String, L> getter);

        /**
         * Entity type of the minion
         */
        Supplier<EntityType<? extends IMinionEntity>> type();

        /**
         * List of command arguments
         */
        List<ICommandArgument<Z, ?>> commandArguments();

        /**
         * Represents a command argument for the minion command
         */
        interface ICommandArgument<Z extends IMinionData, T> {

            String name();

            T defaultValue();

            ArgumentType<T> type();

            BiFunction<CommandContext<CommandSourceStack>, String, T> getter();

            BiConsumer<Z, T> setter();
        }
    }
}
