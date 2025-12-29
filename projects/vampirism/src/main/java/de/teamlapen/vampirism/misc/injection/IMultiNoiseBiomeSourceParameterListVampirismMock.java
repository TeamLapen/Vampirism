package de.teamlapen.vampirism.misc.injection;

import de.teamlapen.vampirism.misc.extension.IMultiNoiseBiomeSourceParameterList;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList;

@Deprecated
public interface IMultiNoiseBiomeSourceParameterListVampirismMock extends IMultiNoiseBiomeSourceParameterList {

    interface PresetVampirismMock extends IMultiNoiseBiomeSourceParameterList.Preset {
        @Override
        default MultiNoiseBiomeSourceParameterList.Preset.SourceProvider getProvider() {
            throw new IllegalStateException("This class is only supported as injection class");
        }

        @Override
        default void setProvider(MultiNoiseBiomeSourceParameterList.Preset.SourceProvider provider) {
            throw new IllegalStateException("This class is only supported as injection class");
        }
    }

}
