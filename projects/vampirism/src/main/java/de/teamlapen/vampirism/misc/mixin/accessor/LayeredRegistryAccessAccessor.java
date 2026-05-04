package de.teamlapen.vampirism.misc.mixin.accessor;

import de.teamlapen.vampirism.misc.extension.ILayeredRegistryAccess;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.RegistryAccess;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(LayeredRegistryAccess.class)
public interface LayeredRegistryAccessAccessor extends ILayeredRegistryAccess {

    @Final
    @Mutable
    @Accessor("values")
    @Override
    void vampirism$setValues(List<RegistryAccess.Frozen> values);
}
