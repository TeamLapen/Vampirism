package de.teamlapen.factions.common.components;

import de.teamlapen.factions.api.items.components.IFactionRestriction;
import org.jetbrains.annotations.Nullable;

public interface IFactionRestrictionProvider {

    @Nullable
    IFactionRestriction getFactionRestriction();
}
