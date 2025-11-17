package de.teamlapen.vampirism.api;

import de.teamlapen.vampirism.api.entity.ISundamageRegistry;
import de.teamlapen.vampirism.api.entity.IVampirismEntityRegistry;
import de.teamlapen.vampirism.api.entity.factions.IFactionRegistry;
import de.teamlapen.vampirism.api.entity.player.actions.IActionManager;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampireVisionRegistry;
import de.teamlapen.vampirism.api.general.IBloodConversionRegistry;
import de.teamlapen.vampirism.api.items.IExtendedBrewingRecipeRegistry;
import de.teamlapen.vampirism.api.settings.ISettingsProvider;

public interface IVampirismServices {

    IActionManager actionManager();

    IVampireVisionRegistry visionRegistry();

    IFactionRegistry factionRegistry();

    ISundamageRegistry sundamageRegistry();

    IVampirismEntityRegistry entityRegistry();

    IExtendedBrewingRecipeRegistry extendedBrewingRecipeRegistry();

    ISettingsProvider settings();

    IBloodConversionRegistry bloodConversionRegistry();
}
