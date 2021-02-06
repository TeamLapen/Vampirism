package de.teamlapen.vampirism.mixin;

import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;
import net.minecraft.world.gen.settings.StructureSeparationSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(DimensionStructuresSettings.class)
public interface DimensionStructureSettingsAccessor {


    @Accessor("field_236193_d_")
    void setStructureSeparation_vampirism(Map<Structure<?>, StructureSeparationSettings> separation);


}
