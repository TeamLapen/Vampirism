package de.teamlapen.vampirism.misc.mixin.accessor;

import de.teamlapen.vampirism.misc.extension.IRegistriesDatapackGenerator;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.registries.RegistriesDatapackGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("deprecation")
@Mixin(RegistriesDatapackGenerator.class)
public interface RegistriesDatapackGeneratorAccessor extends IRegistriesDatapackGenerator {

    @Override
    @Accessor("registries")
    CompletableFuture<HolderLookup.Provider> getRegistries();
}
