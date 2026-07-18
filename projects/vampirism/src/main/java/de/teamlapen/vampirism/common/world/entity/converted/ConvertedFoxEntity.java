package de.teamlapen.vampirism.common.world.entity.converted;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.fox.Fox;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

/**
 * Handwritten subclass of the generated {@link ConvertedFox} base. Only needed for the fox-specific
 * spawn rules ({@link Fox#checkFoxSpawnRules}); the rest of the boilerplate comes from the base.
 */
public class ConvertedFoxEntity extends ConvertedFox {

    public ConvertedFoxEntity(EntityType<? extends ConvertedFox> type, Level level) {
        super(type, level);
    }

    public static boolean checkSpawnRules(EntityType<? extends Animal> type, LevelAccessor level, EntitySpawnReason reason, BlockPos pos, RandomSource random) {
        //noinspection unchecked
        return level.getDifficulty() != Difficulty.PEACEFUL && Fox.checkFoxSpawnRules((EntityType<Fox>) type, level, reason, pos, random);
    }
}
