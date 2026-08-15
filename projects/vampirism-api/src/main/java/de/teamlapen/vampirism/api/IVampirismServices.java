package de.teamlapen.vampirism.api;

import de.teamlapen.vampirism.api.general.IBloodConversionRegistry;
import de.teamlapen.vampirism.api.world.entity.ISundamageRegistry;
import de.teamlapen.vampirism.api.world.entity.IVampirismEntityRegistry;
import de.teamlapen.vampirism.api.world.items.IExtendedBrewingRecipeRegistry;

public interface IVampirismServices {

    ISundamageRegistry sunDamageRegistry();

    IVampirismEntityRegistry entityRegistry();

    IExtendedBrewingRecipeRegistry extendedBrewingRecipeRegistry();

    IBloodConversionRegistry bloodConversionRegistry();
}
