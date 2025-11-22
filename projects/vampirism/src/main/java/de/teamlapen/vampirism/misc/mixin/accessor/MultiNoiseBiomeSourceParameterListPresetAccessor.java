package de.teamlapen.vampirism.misc.mixin.accessor;

import de.teamlapen.vampirism.misc.extension.IMultiNoiseBiomeSourceParameterList;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MultiNoiseBiomeSourceParameterList.Preset.class)
public interface MultiNoiseBiomeSourceParameterListPresetAccessor extends IMultiNoiseBiomeSourceParameterList.Preset {

    @Override
    @Accessor
    MultiNoiseBiomeSourceParameterList.Preset.SourceProvider getProvider();

    @Override
    @Mutable
    @Accessor
    void setProvider(MultiNoiseBiomeSourceParameterList.Preset.SourceProvider provider);
}
