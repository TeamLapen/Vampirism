package de.teamlapen.vampirism.api;

import de.teamlapen.vampirism.api.entity.ISundamageRegistry;
import de.teamlapen.vampirism.api.entity.IVampirismEntityRegistry;
import de.teamlapen.factions.api.factions.IFactionRegistry;
import de.teamlapen.vampirism.api.general.IBloodConversionRegistry;
import de.teamlapen.vampirism.api.items.IExtendedBrewingRecipeRegistry;
import de.teamlapen.vampirism.api.settings.ISettingsProvider;

public interface IVampirismServices {

    IFactionRegistry factionRegistry();

    ISundamageRegistry sundamageRegistry();

    IVampirismEntityRegistry entityRegistry();

    IExtendedBrewingRecipeRegistry extendedBrewingRecipeRegistry();

    ISettingsProvider settings();

    IBloodConversionRegistry bloodConversionRegistry();
}
