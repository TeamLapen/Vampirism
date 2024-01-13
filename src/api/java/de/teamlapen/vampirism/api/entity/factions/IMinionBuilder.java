package de.teamlapen.vampirism.api.entity.factions;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import de.teamlapen.vampirism.api.entity.minion.IMinionData;
import de.teamlapen.vampirism.api.entity.minion.IMinionEntity;
import de.teamlapen.vampirism.api.entity.minion.IMinionTask;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public interface IMinionBuilder<T extends IFactionPlayer<T>,Z extends IMinionData> {

    IMinionCommandBuilder<T,Z> commandBuilder(@NotNull Supplier<EntityType<? extends IMinionEntity>> type);

    ILordPlayerBuilder<T> build();

    interface IMinionCommandBuilder<T extends IFactionPlayer<T>,Z extends IMinionData> {

        <L> IMinionCommandBuilder<T,Z> with(@NotNull String name, L defaultValue, @NotNull ArgumentType<L> type, BiConsumer<Z,L> setter, BiFunction<CommandContext<CommandSourceStack>, String, L> getter);

        IMinionBuilder<T,Z> build();

        interface ICommandEntry<Z extends IMinionData,T> {

            String name();

            T defaultValue();

            ArgumentType<T> type();

            BiFunction<CommandContext<CommandSourceStack>, String, T> getter();

            BiConsumer<Z,T> setter();
        }
    }
}
