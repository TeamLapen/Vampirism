package de.teamlapen.vampirism.misc.mixin.accessor;

import de.teamlapen.vampirism.misc.extension.IImmutableRegistryAccess;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(RegistryAccess.ImmutableRegistryAccess.class)
public interface ImmutableRegistryAccessAccessor extends IImmutableRegistryAccess {

    @Mutable
    @Final
    @Accessor("registries")
    @Override
    void vampirism$setRegistries(Map<? extends ResourceKey<? extends Registry<?>>, ? extends Registry<?>> registries);
}
