package de.teamlapen.faction.api.world.entities.minion;

import de.teamlapen.faction.api.factions.IPlayableFaction;
import de.teamlapen.faction.api.factions.lord.IMinionEntryBuilder;
import de.teamlapen.faction.api.world.entities.player.IFactionPlayer;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EntityType;

import java.util.List;
import java.util.function.Supplier;

public interface IMinionEntry<T extends IFactionPlayer<T>, Z extends IMinionData> {

    /**
     * @return a supplier to create a fresh {@link IMinionData}
     */
    Supplier<Z> data();

    /**
     * @return The entity type of the minion
     */
    Supplier<EntityType<? extends IMinionEntity>> type();

    /**
     * @return The faction this minion belongs to
     */
    Holder<? extends IPlayableFaction<T>> faction();

    /**
     * @return The command arguments for this minion used by the MinionCommand to create a minion using commands
     */
    List<IMinionEntryBuilder.IMinionCommandBuilder.ICommandArgument<Z, ?>> commandArguments();
}
