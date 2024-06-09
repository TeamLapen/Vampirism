package de.teamlapen.vampirism.api.entity.factions;

import net.minecraft.core.Holder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IDisguise {

    @NotNull
    Holder<? extends IPlayableFaction<?>> getOriginalFaction();

    @Nullable
    Holder<? extends IFaction<?>> getViewedFaction(@Nullable Holder<? extends IFaction<?>> viewerFaction);

    @Nullable
    default Holder<? extends IFaction<?>> getViewedFaction(@Nullable Holder<? extends IFaction<?>> viewerFaction, boolean ignoreDisguise) {
        return ignoreDisguise ? getOriginalFaction() : getViewedFaction(viewerFaction);
    }

    void disguiseAs(@Nullable Holder<? extends IFaction<?>> faction);

    void unDisguise();

    boolean isDisguised();
}
