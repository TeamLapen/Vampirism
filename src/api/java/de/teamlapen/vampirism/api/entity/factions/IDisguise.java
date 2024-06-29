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

    record None(Holder<? extends IPlayableFaction<?>> faction) implements IDisguise {

        @Override
        public @NotNull Holder<? extends IPlayableFaction<?>> getOriginalFaction() {
            return this.faction;
        }

        @Override
        public @Nullable Holder<? extends IFaction<?>> getViewedFaction(@Nullable Holder<? extends IFaction<?>> viewerFaction) {
            return this.faction;
        }

        @Override
        public void disguiseAs(@Nullable Holder<? extends IFaction<?>> faction) {

        }

        @Override
        public void unDisguise() {

        }

        @Override
        public boolean isDisguised() {
            return false;
        }
    }
}
