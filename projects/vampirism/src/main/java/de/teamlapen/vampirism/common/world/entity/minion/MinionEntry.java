package de.teamlapen.vampirism.common.world.entity.minion;

import de.teamlapen.factions.api.factions.IPlayableFaction;
import de.teamlapen.factions.api.factions.lord.IMinionEntryBuilder;
import de.teamlapen.factions.api.world.entities.minion.IMinionData;
import de.teamlapen.factions.api.world.entities.minion.IMinionEntity;
import de.teamlapen.factions.api.world.entities.minion.IMinionEntry;
import de.teamlapen.factions.api.world.entities.player.IFactionPlayer;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EntityType;

import java.util.List;
import java.util.function.Supplier;

public record MinionEntry<T extends IFactionPlayer<T>, Z extends IMinionData>(Holder<? extends IPlayableFaction<T>> faction,
                                                                              Supplier<Z> data,
                                                                              Supplier<EntityType<? extends IMinionEntity>> type,
                                                                              List<IMinionEntryBuilder.IMinionCommandBuilder.ICommandArgument<Z, ?>> commandArguments) implements IMinionEntry<T, Z> {
    public MinionEntry(MinionEntryBuilder<T, Z> builder) {
        this(builder.faction, builder.data, builder.commandBuilder.type(), builder.commandBuilder.commandArguments());
    }
}
