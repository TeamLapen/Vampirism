package de.teamlapen.factions.api.factions;

import net.minecraft.core.Holder;

public interface IFactionPredicates {

    IFactionPredicate.Builder builder();

    IFactionPredicate.Builder builder(Holder<? extends IFaction<?>> ownFaction);
}
