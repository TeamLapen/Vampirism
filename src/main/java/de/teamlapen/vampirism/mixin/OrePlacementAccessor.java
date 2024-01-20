package de.teamlapen.vampirism.mixin;

import net.minecraft.data.worldgen.placement.OrePlacements;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(OrePlacements.class)
public interface OrePlacementAccessor {

    @Invoker("commonOrePlacement(ILnet/minecraft/world/level/levelgen/placement/PlacementModifier;)Ljava/util/List;")
    static List<PlacementModifier> commonOrePlacement(int pCount, PlacementModifier pHeightRange) {
        throw new IllegalStateException("Mixin failed to apply");
    }
}
