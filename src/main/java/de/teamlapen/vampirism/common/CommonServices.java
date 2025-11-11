package de.teamlapen.vampirism.common;

import de.teamlapen.vampirism.common.recipes.RecipesSync;
import de.teamlapen.vampirism.common.util.Services;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;

public class CommonServices extends Services {

    private final RecipesSync recipes = new RecipesSync();


    public CommonServices(ModContainer container) {
        super(container);
    }

    public RecipesSync recipes() {
        return this.recipes;
    }

    @Override
    protected void registerModBus(IEventBus bus) {

    }

    @Override
    protected void registerGameBus(IEventBus bus) {
        bus.register(this.recipes);
    }
}
