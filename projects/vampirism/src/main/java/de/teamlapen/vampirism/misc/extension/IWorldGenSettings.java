package de.teamlapen.vampirism.misc.extension;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.LevelStem;

public interface IWorldGenSettings {

    void vampirism$addDimension(ResourceKey<LevelStem> key, LevelStem dimension);

    void vampirism$removeDimension(ResourceKey<Level>... key);
}
