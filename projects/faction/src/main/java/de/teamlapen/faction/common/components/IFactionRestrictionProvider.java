package de.teamlapen.faction.common.components;

import de.teamlapen.faction.api.world.items.components.IFactionRestriction;
import org.jetbrains.annotations.Nullable;

public interface IFactionRestrictionProvider {

    @Nullable
    IFactionRestriction getFactionRestriction();
}
