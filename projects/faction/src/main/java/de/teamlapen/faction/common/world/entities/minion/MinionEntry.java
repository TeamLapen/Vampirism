package de.teamlapen.faction.common.world.entities.minion;

import de.teamlapen.faction.api.factions.IPlayableFaction;
import de.teamlapen.faction.api.world.entities.minion.*;
import de.teamlapen.faction.api.world.entities.player.IFactionPlayer;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class MinionEntry<T extends IFactionPlayer<T>> implements IMinionEntry<T> {

    private final BiFunction<T, IMinionEntry<T>, IMinionData> dataProvider;
    private final Holder<EntityType<? extends IMinionEntity>> entityType;
    private final Holder<? extends IPlayableFaction<T>> faction;
    private final List<MinionStat> minionStats;
    private final List<MinionAppearance<?>> minionAppearances;

    public MinionEntry(MinionEntryProperties<T> properties) {
        this.dataProvider = properties.getDataProvider();
        this.entityType = properties.getEntityType();
        this.faction = properties.getFaction();
        this.minionStats = properties.getMinionStats();
        this.minionAppearances = properties.getMinionAppearances();
    }

    @Override
    public IMinionData createData(T factionPlayer, IMinionEntry<T> entry) {
        return this.dataProvider.apply(factionPlayer, entry);
    }

    @Override
    public Holder<EntityType<? extends IMinionEntity>> type() {
        return this.entityType;
    }

    @Override
    public Holder<? extends IPlayableFaction<T>> faction() {
        return this.faction;
    }

    @Override
    public List<MinionStat> minionStats() {
        return this.minionStats;
    }

    @Override
    public List<MinionAppearance<?>> appearances() {
        return this.minionAppearances;
    }

    @Override
    public @Nullable MinionStat minionStat(Holder<DataComponentType<?>> type) {
        return this.minionStats.stream().filter(x -> x.getIdentifier().get() == type.value()).findFirst().orElse(null);
    }
}
