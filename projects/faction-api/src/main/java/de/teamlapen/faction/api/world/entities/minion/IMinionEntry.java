package de.teamlapen.faction.api.world.entities.minion;

import de.teamlapen.faction.api.factions.IPlayableFaction;
import de.teamlapen.faction.api.factions.lord.IMinionEntryBuilder;
import de.teamlapen.faction.api.world.entities.ICustomizationHolder;
import de.teamlapen.faction.api.world.entities.player.IFactionPlayer;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public interface IMinionEntry<T extends IFactionPlayer<T>> {

    IMinionData createData(T factionPlayer, IMinionEntry<T> entry);

    Holder<EntityType<? extends IMinionEntity>> type();

    Holder<? extends IPlayableFaction<T>> faction();


    List<MinionStat> minionStats();

    @Nullable
    MinionStat minionStat(Holder<DataComponentType<?>> type);

}
