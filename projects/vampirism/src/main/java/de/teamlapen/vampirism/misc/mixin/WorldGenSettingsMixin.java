package de.teamlapen.vampirism.misc.mixin;

import de.teamlapen.vampirism.misc.extension.IWorldGenSettings;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.WorldDimensions;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.saveddata.SavedData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import java.util.HashMap;

@Mixin(WorldGenSettings.class)
public abstract class WorldGenSettingsMixin extends SavedData implements IWorldGenSettings {


    @Shadow
    @Final
    @Mutable
    private WorldDimensions dimensions;

    @Override
    public void vampirism$addDimension(ResourceKey<LevelStem> key, LevelStem dimension) {
        HashMap<ResourceKey<LevelStem>, LevelStem> resourceKeyLevelStemHashMap = new HashMap<>(dimensions.dimensions());
        resourceKeyLevelStemHashMap.put(key, dimension);
        dimensions = new WorldDimensions(resourceKeyLevelStemHashMap);
        setDirty();
    }

    @SafeVarargs
    @Override
    public final void vampirism$removeDimension(ResourceKey<Level>... key) {
        HashMap<ResourceKey<LevelStem>, LevelStem> resourceKeyLevelStemHashMap = new HashMap<>(dimensions.dimensions());
        for (ResourceKey<Level> levelStemResourceKey : key) {
            resourceKeyLevelStemHashMap.remove(levelStemResourceKey);
        }
        dimensions = new WorldDimensions(resourceKeyLevelStemHashMap);
        setDirty();
    }
}
