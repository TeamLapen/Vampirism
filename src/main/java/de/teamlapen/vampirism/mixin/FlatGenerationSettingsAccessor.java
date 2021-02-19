package de.teamlapen.vampirism.mixin;

import net.minecraft.world.gen.FlatGenerationSettings;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.structure.Structure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;


@Mixin(FlatGenerationSettings.class)
public interface FlatGenerationSettingsAccessor {

    @Accessor("STRUCTURES")
    static Map<Structure<?>, StructureFeature<?, ?>> getStructures_vampirism() {
        throw new IllegalStateException("Mixin not applied");
    }
}
