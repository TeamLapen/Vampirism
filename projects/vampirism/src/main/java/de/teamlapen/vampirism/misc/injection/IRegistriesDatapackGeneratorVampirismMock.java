package de.teamlapen.vampirism.misc.injection;

import de.teamlapen.vampirism.misc.extension.IRegistriesDatapackGenerator;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

@Deprecated
public interface IRegistriesDatapackGeneratorVampirismMock extends IRegistriesDatapackGenerator {
    @Override
    default CompletableFuture<HolderLookup.Provider> getRegistries() {
        throw new IllegalStateException("This class is only supported as injection class");
    }
}
