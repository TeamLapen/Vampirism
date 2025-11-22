package de.teamlapen.factions.api.factions.lord;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import de.teamlapen.factions.api.entities.minion.IMinionData;
import de.teamlapen.factions.api.entities.minion.IMinionEntity;
import de.teamlapen.factions.api.entities.minion.IMinionEntry;
import de.teamlapen.factions.api.entities.player.IFactionPlayer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.entity.EntityType;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public interface IMinionEntryBuilder<T extends IFactionPlayer<T>, Z extends IMinionData> {

    IMinionEntryBuilder<T, Z> commandBuilder(IMinionCommandBuilder<T, Z> builder);

    IMinionEntry<T, Z> build();

    interface IMinionCommandBuilder<T extends IFactionPlayer<T>, Z extends IMinionData> {

        <L> IMinionCommandBuilder<T, Z> with(String name, L defaultValue, ArgumentType<L> type, BiConsumer<Z, L> setter, BiFunction<CommandContext<CommandSourceStack>, String, L> getter);

        Supplier<EntityType<? extends IMinionEntity>> type();

        List<ICommandEntry<Z, ?>> commandArguments();

        interface ICommandEntry<Z extends IMinionData, T> {

            String name();

            T defaultValue();

            ArgumentType<T> type();

            BiFunction<CommandContext<CommandSourceStack>, String, T> getter();

            BiConsumer<Z, T> setter();
        }
    }
}
