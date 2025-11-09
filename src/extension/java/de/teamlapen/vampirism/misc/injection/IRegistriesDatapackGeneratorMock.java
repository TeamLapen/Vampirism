package de.teamlapen.vampirism.misc.injection;

import de.teamlapen.vampirism.misc.extension.IRegistriesDatapackGenerator;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

public interface IRegistriesDatapackGeneratorMock extends IRegistriesDatapackGenerator {
    @Override
    default CompletableFuture<HolderLookup.Provider> getRegistries() {
        return null;
    }
}
