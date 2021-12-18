package de.teamlapen.vampirism.mixin;

import net.minecraft.core.Registry;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.function.Function;
import java.util.function.Supplier;

@Mixin(MultiNoiseBiomeSource.Preset.class)
public interface MultiNoiseBiomeSourcePresetAccessor {

    @Final
    @Accessor("parameterSource")
    Function<Registry<Biome>, Climate.ParameterList<Supplier<Biome>>> getPresetSupplier_vampirism();

    @Mutable
    @Final
    @Accessor("parameterSource")
    void setPresetSupplier_vampirism(Function<Registry<Biome>, Climate.ParameterList<Supplier<Biome>>> supplier);
}
