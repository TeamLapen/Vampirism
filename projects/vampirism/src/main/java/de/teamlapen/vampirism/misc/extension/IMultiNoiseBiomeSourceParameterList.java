package de.teamlapen.vampirism.misc.extension;

import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList;

public interface IMultiNoiseBiomeSourceParameterList {

    interface Preset {
        MultiNoiseBiomeSourceParameterList.Preset.SourceProvider getProvider();

        void setProvider(MultiNoiseBiomeSourceParameterList.Preset.SourceProvider provider);

    }
}
