package de.teamlapen.vampirism.mixin;

import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;


@Mixin(FlatLevelGeneratorSettings.class)
public interface FlatGenerationSettingsAccessor {

    @Accessor("STRUCTURE_FEATURES")
    static Map<StructureFeature<?>, ConfiguredStructureFeature<?, ?>> getStructures_vampirism() {
        throw new IllegalStateException("Mixin not applied");
    }
}
