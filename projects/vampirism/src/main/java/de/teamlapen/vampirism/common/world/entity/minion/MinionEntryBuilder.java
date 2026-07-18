package de.teamlapen.vampirism.common.world.entity.minion;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import de.teamlapen.faction.api.factions.IPlayableFaction;
import de.teamlapen.faction.api.factions.lord.IMinionEntryBuilder;
import de.teamlapen.faction.api.util.SafeCast;
import de.teamlapen.faction.api.world.entities.minion.IMinionData;
import de.teamlapen.faction.api.world.entities.minion.IMinionEntity;
import de.teamlapen.faction.api.world.entities.minion.IMinionEntry;
import de.teamlapen.faction.api.world.entities.player.IFactionPlayer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.*;

public class MinionEntryBuilder<T extends IFactionPlayer<T>, Z extends IMinionData> implements IMinionEntryBuilder<T, Z> {

    public final Holder<? extends IPlayableFaction<T>> faction;
    public final IMinionEntry.IMinionCreator<T, Z> data;
    public IMinionCommandBuilder<T, Z> commandBuilder;

    public MinionEntryBuilder(Holder<? extends IPlayableFaction<T>> faction, @NotNull IMinionEntry.IMinionCreator<T, Z> data) {
        this.faction = faction;
        this.data = data;
    }

    @Override
    public @NotNull MinionEntryBuilder<T, Z> commandBuilder(@NotNull Supplier<? extends EntityType<? extends IMinionEntity>> type, @NotNull Consumer<IMinionCommandBuilder<T, Z>> builder) {
        this.commandBuilder = new MinionCommandBuilder<>(SafeCast.cast(type));
        builder.accept(this.commandBuilder);
        return this;
    }

    @Override
    public @NotNull MinionEntry<T, Z> build() {
        return new MinionEntry<>(this);
    }

    public static class MinionCommandBuilder<T extends IFactionPlayer<T>, Z extends IMinionData> implements IMinionCommandBuilder<T, Z> {

        protected final Supplier<EntityType<? extends IMinionEntity>> type;
        protected final List<ICommandArgument<Z, ?>> commandArguments = new ArrayList<>();

        public MinionCommandBuilder(@NotNull Supplier<EntityType<? extends IMinionEntity>> type) {
            this.type = type;
        }

        @Override
        public @NotNull Supplier<EntityType<? extends IMinionEntity>> type() {
            return this.type;
        }

        @Override
        public @NotNull List<ICommandArgument<Z, ?>> commandArguments() {
            return this.commandArguments;
        }

        @Override
        public <L> @NotNull IMinionCommandBuilder<T, Z> with(@NotNull String name, @NotNull L defaultValue, @NotNull ArgumentType<L> type, @NotNull BiConsumer<Z, L> setter, @NotNull BiFunction<CommandContext<CommandSourceStack>, String, L> getter) {
            this.commandArguments.add(new CommandArgument<>(name, defaultValue, type, setter, getter));
            return this;
        }

        public record CommandArgument<Z extends IMinionData, T>(String name, T defaultValue, ArgumentType<T> type, BiConsumer<Z, T> setter, BiFunction<CommandContext<CommandSourceStack>, String, T> getter) implements ICommandArgument<Z, T> {
        }
    }
}
