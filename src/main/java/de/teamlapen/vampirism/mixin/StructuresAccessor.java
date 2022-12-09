package de.teamlapen.vampirism.mixin;

import net.minecraft.core.HolderSet;
import net.minecraft.data.worldgen.Structures;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Structures.class)
public interface StructuresAccessor {

    @Invoker("structure")
    static Structure.StructureSettings structure(HolderSet<Biome> biomeHolderSet, TerrainAdjustment terrainAdjustment) {
        throw new IllegalStateException("Mixin did not apply");
    }
}
