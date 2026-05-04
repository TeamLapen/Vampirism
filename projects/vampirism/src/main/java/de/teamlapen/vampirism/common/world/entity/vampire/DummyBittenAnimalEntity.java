package de.teamlapen.vampirism.common.world.entity.vampire;

import de.teamlapen.vampirism.api.world.entity.convertible.IConvertedCreature;
import de.teamlapen.vampirism.common.tags.ModBlockTags;
import de.teamlapen.vampirism.common.world.entity.ExtendedCreature;
import de.teamlapen.vampirism.common.world.entity.converted.ConvertedCreatureEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;

/**
 * Just a simple dummy class, which makes it possible to spawn bitten mobs
 */
public class DummyBittenAnimalEntity extends Mob {

    public static boolean spawnPredicate(EntityType<? extends DummyBittenAnimalEntity> entityType, LevelAccessor iWorld, EntitySpawnReason spawnReason, BlockPos blockPos, RandomSource random) {
        return (iWorld.getBlockState(blockPos.below()).getBlock() == Blocks.GRASS_BLOCK || iWorld.getBlockState(blockPos.below()).is(ModBlockTags.CURSED_EARTH));
    }

    public DummyBittenAnimalEntity(EntityType<? extends DummyBittenAnimalEntity> type, Level world) {
        super(type, world);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.tickCount > 4 && !this.level().isClientSide()) {
            PathfinderMob entity;
            int rand = this.random.nextInt(3);
            entity = switch (rand) {
                case 0 -> EntityType.PIG.create(level(), EntitySpawnReason.CONVERSION);
                case 1 -> EntityType.SHEEP.create(level(), EntitySpawnReason.CONVERSION);
                default -> EntityType.COW.create(level(), EntitySpawnReason.CONVERSION);
            };
            if (entity == null) return;
            entity.copyPosition(this);
            ExtendedCreature.getSafe(entity).ifPresent(e -> {
                if (e.canBecomeVampire()) {
                    IConvertedCreature<?> c = e.makeVampire();
                    if (c instanceof ConvertedCreatureEntity) {
                        ((ConvertedCreatureEntity<?>) c).setCanDespawn();
                    }
                }
            });

            this.discard();

        }
    }

}