package de.teamlapen.vampirism.mixin;

import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MultiNoiseBiomeSourceParameterList.Preset.class)
public interface MultiNoiseBiomeSourceParameterListPresetAccessor {

    @Accessor
    MultiNoiseBiomeSourceParameterList.Preset.SourceProvider getProvider();

    @Mutable
    @Accessor
    void setProvider(MultiNoiseBiomeSourceParameterList.Preset.SourceProvider provider);
}
