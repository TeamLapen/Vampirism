package de.teamlapen.vampirism.api.entity.minion;

import de.teamlapen.vampirism.api.entity.factions.IMinionEntryBuilder;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EntityType;

import java.util.List;
import java.util.function.Supplier;

public interface IMinionEntry<T extends IFactionPlayer<T>, Z extends IMinionData> {

    /**
     * @return a supplier to create a fresh {@link de.teamlapen.vampirism.api.entity.minion.IMinionData}
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
    List<IMinionEntryBuilder.IMinionCommandBuilder.ICommandEntry<Z, ?>> commandArguments();
}
