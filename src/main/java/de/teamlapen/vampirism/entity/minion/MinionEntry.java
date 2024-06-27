package de.teamlapen.vampirism.entity.minion;

import de.teamlapen.vampirism.api.entity.factions.IMinionEntryBuilder;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.minion.IMinionData;
import de.teamlapen.vampirism.api.entity.minion.IMinionEntity;
import de.teamlapen.vampirism.api.entity.minion.IMinionEntry;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EntityType;

import java.util.List;
import java.util.function.Supplier;

public record MinionEntry<T extends IFactionPlayer<T>, Z extends IMinionData>(Holder<? extends IPlayableFaction<T>> faction,
                                                                              Supplier<Z> data,
                                                                              Supplier<EntityType<? extends IMinionEntity>> type,
                                                                              List<IMinionEntryBuilder.IMinionCommandBuilder.ICommandEntry<Z, ?>> commandArguments) implements IMinionEntry<T, Z> {
    public MinionEntry(MinionEntryBuilder<T, Z> builder) {
        this(builder.faction, builder.data, builder.commandBuilder.type(), builder.commandBuilder.commandArguments());
    }
}
