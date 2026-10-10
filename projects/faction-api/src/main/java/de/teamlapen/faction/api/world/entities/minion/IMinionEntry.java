package de.teamlapen.faction.api.world.entities.minion;

import de.teamlapen.faction.api.factions.IPlayableFaction;
import de.teamlapen.faction.api.world.entities.player.IFactionPlayer;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface IMinionEntry<T extends IFactionPlayer<T>> {

    IMinionData createData(T factionPlayer, IMinionEntry<T> entry);

    Holder<EntityType<? extends IMinionEntity>> type();

    Holder<? extends IPlayableFaction<T>> faction();

    int maxLevel();

    List<MinionStat> minionStats();

    List<MinionAppearance<?>> appearances();

    @Nullable
    MinionStat minionStat(Holder<DataComponentType<?>> type);

}
