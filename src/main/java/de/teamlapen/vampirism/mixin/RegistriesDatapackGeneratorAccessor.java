package de.teamlapen.vampirism.mixin;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.registries.RegistriesDatapackGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("deprecation")
@Mixin(RegistriesDatapackGenerator.class)
public interface RegistriesDatapackGeneratorAccessor {

    @Accessor("registries")
    CompletableFuture<HolderLookup.Provider> getRegistries();
}
