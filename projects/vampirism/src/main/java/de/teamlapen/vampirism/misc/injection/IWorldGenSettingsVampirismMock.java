package de.teamlapen.vampirism.misc.injection;

import de.teamlapen.vampirism.misc.extension.IWorldGenSettings;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.LevelStem;

@Deprecated
public interface IWorldGenSettingsVampirismMock extends IWorldGenSettings {
    @Override
    default void vampirism$addDimension(ResourceKey<LevelStem> key, LevelStem dimension) {
        throw new IllegalStateException("This class is only supported as injection class");
    }

    @Override
    default void vampirism$removeDimension(ResourceKey<Level>... key) {
        throw new IllegalStateException("This class is only supported as injection class");
    }
}
