package de.teamlapen.vampirism.mixin;

import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.StructureSettings;
import net.minecraft.world.level.levelgen.feature.configurations.StructureFeatureConfiguration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(StructureSettings.class)
public interface DimensionStructureSettingsAccessor {


    @Accessor("structureConfig")
    void setStructureSeparation_vampirism(Map<StructureFeature<?>, StructureFeatureConfiguration> separation);


}
