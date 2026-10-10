package de.teamlapen.faction.api.world.entities.minion;

import de.teamlapen.faction.api.factions.IFaction;
import de.teamlapen.faction.api.factions.IFactionEntity;
import de.teamlapen.faction.api.factions.IPlayableFaction;
import de.teamlapen.faction.api.util.SafeCast;
import de.teamlapen.faction.api.world.entities.player.IFactionPlayer;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class MinionEntryProperties<T extends IFactionPlayer<T>> {

    @Nullable
    private BiFunction<T, IMinionEntry<T>, IMinionData> dataProvider;
    @Nullable
    private Holder<EntityType<? extends IMinionEntity>> entityType;
    @Nullable
    private Holder<? extends IPlayableFaction<T>> faction;
    private final List<MinionStat> minionStats = new ArrayList<>();
    private final List<MinionAppearance<?>> minionAppearances = new ArrayList<>();
    private int maxLevel = 1;

    public MinionEntryProperties<T> withProvider(BiFunction<T, IMinionEntry<T>, IMinionData> dataProvider) {
        this.dataProvider = dataProvider;
        return this;
    }

    public MinionEntryProperties<T> withEntityType(Holder<EntityType<?>> entityType) {
        this.entityType = (Holder<EntityType<? extends IMinionEntity>>) (Object) entityType;
        return this;
    }

    public  MinionEntryProperties<T> withFaction(Holder<? extends IPlayableFaction<? extends T>> faction) {
        this.faction = SafeCast.cast(faction);
        return this;
    }

    public MinionEntryProperties<T> withMinionStats(MinionStat... minionStats) {
        this.minionStats.addAll(Arrays.asList(minionStats));
        return this;
    }

    public MinionEntryProperties<T> withMinionAppearances(MinionAppearance<?>... minionAppearances) {
        this.minionAppearances.addAll(Arrays.asList(minionAppearances));
        return this;
    }

    public MinionEntryProperties<T> withMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
        return this;
    }

    public BiFunction<T, IMinionEntry<T>, IMinionData> getDataProvider() {
        return Objects.requireNonNull(this.dataProvider);
    }

    public Holder<EntityType<? extends IMinionEntity>> getEntityType() {
        return Objects.requireNonNull(this.entityType);
    }

    public Holder<? extends IPlayableFaction<T>> getFaction() {
        return Objects.requireNonNull(this.faction);
    }

    public List<MinionStat> getMinionStats() {
        return List.copyOf(this.minionStats);
    }
    public List<MinionAppearance<?>> getMinionAppearances() {
        return List.copyOf(this.minionAppearances);
    }
    public int getMaxLevel() {
        return this.maxLevel;
    }
}
