package de.teamlapen.vampirism.entity.minion;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import de.teamlapen.vampirism.api.entity.factions.IMinionEntryBuilder;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.minion.IMinionData;
import de.teamlapen.vampirism.api.entity.minion.IMinionEntity;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class MinionEntryBuilder<T extends IFactionPlayer<T>, Z extends IMinionData> implements IMinionEntryBuilder<T,Z> {

    public final Holder<? extends IPlayableFaction<T>> faction;
    public final Supplier<Z> data;
    public IMinionCommandBuilder<T,Z> commandBuilder;

    public MinionEntryBuilder(Holder<? extends IPlayableFaction<T>> faction, @NotNull Supplier<Z> data) {
        this.faction = faction;
        this.data = data;
    }

    @Override
    public MinionEntryBuilder<T,Z> commandBuilder(@NotNull IMinionCommandBuilder<T, Z> builder) {
        commandBuilder = builder;
        return this;
    }

    @Override
    public MinionEntry<T, Z> build() {
        return new MinionEntry<>(this);
    }

    public static class MinionCommandBuilder<T extends IFactionPlayer<T>, Z extends IMinionData> implements IMinionCommandBuilder<T,Z>{

        protected final Supplier<EntityType<? extends IMinionEntity>> type;
        protected final List<ICommandEntry<Z,?>> commandArguments = new ArrayList<>();

        public MinionCommandBuilder(@NotNull Supplier<EntityType<? extends IMinionEntity>> type) {
            this.type = type;
        }

        @Override
        public Supplier<EntityType<? extends IMinionEntity>> type() {
            return this.type;
        }

        @Override
        public List<ICommandEntry<Z, ?>> commandArguments() {
            return this.commandArguments;
        }

        @Override
        public <L> IMinionCommandBuilder<T, Z> with(@NotNull String name, L defaultValue, @NotNull ArgumentType<L> type, BiConsumer<Z, L> setter, BiFunction<CommandContext<CommandSourceStack>, String, L> getter) {
            this.commandArguments.add(new CommandEntry<>(name, defaultValue, type, setter, getter));
            return this;
        }

        public record CommandEntry<Z extends IMinionData,T>(String name, T defaultValue, ArgumentType<T> type, BiConsumer<Z,T> setter, BiFunction<CommandContext<CommandSourceStack>, String, T> getter) implements ICommandEntry<Z,T> {
        }
    }
}
