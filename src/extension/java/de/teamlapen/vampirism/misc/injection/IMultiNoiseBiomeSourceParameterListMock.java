package de.teamlapen.vampirism.misc.injection;

import de.teamlapen.vampirism.misc.extension.IMultiNoiseBiomeSourceParameterList;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList;

public interface IMultiNoiseBiomeSourceParameterListMock extends IMultiNoiseBiomeSourceParameterList {

    interface PresetMock extends IMultiNoiseBiomeSourceParameterList.Preset {
        @Override
        default MultiNoiseBiomeSourceParameterList.Preset.SourceProvider getProvider() {
            return null;
        }

        @Override
        default void setProvider(MultiNoiseBiomeSourceParameterList.Preset.SourceProvider provider) {

        }
    }

}
