package de.teamlapen.vampirism.common.world.entity.player.vampire.properties;

import de.teamlapen.factions.api.factions.IDisguise;
import de.teamlapen.factions.api.factions.IFaction;
import de.teamlapen.factions.api.factions.IPlayableFaction;
import de.teamlapen.factions.common.util.ModCodecs;
import de.teamlapen.sync.PropertyParentSync;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.common.world.entity.player.vampire.VampirePlayer;
import net.minecraft.core.Holder;
import org.jetbrains.annotations.Nullable;

public class VampireDisguise extends PropertyParentSync implements IDisguise {
    private final VampirePlayer vampire;
    private boolean isDisguised;
    @Nullable
    private Holder<? extends IFaction<?>> disguiseFaction;

    public VampireDisguise(VampirePlayer vampire) {
        super(vampire);
        this.vampire = vampire;
    }

    @Override
    public void unDisguise() {
        disguiseAs(null);
    }

    @Override
    public void disguiseAs(@Nullable Holder<? extends IFaction<?>> faction) {
        this.disguiseFaction = faction;
        this.isDisguised = faction != null && !IFaction.is(faction, actualFaction());
        this.vampire.asEntity().refreshDisplayName();
    }

    @Override
    public Holder<? extends IPlayableFaction<?>> actualFaction() {
        return this.vampire.getFaction();
    }

    @Override
    public Holder<? extends IFaction<?>> getViewedFaction(@Nullable Holder<? extends IFaction<?>> viewerFaction) {
        return this.disguiseFaction == null ? actualFaction() : this.disguiseFaction;
    }

    @Override
    public boolean isDisguised() {
        return this.isDisguised;
    }

    @Override
    protected void registerProperties() {
        this.registerProperty(VResourceLocation.mod("disguise_faction")).nullable(ModCodecs.faction()).provider(() -> disguiseFaction).commonLoader(d -> {
            var old = this.disguiseFaction;
            this.disguiseFaction = d;
            this.isDisguised = disguiseFaction != null && !IFaction.is(this.disguiseFaction, actualFaction());
            return IFaction.is(old, this.disguiseFaction);
        }).register();
    }

    @Override
    protected void onPropertyChanged() {
        this.vampire.asEntity().refreshDisplayName();
    }

}
