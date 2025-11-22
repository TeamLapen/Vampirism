package de.teamlapen.vampirism.misc.extension;

import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

public interface IRegistriesDatapackGenerator {
    CompletableFuture<HolderLookup.Provider> getRegistries();

}
