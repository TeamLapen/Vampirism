package de.teamlapen.vampirism.entity.vampire;

import de.teamlapen.vampirism.api.entity.convertible.IConvertedCreature;
import de.teamlapen.vampirism.core.ModTags;
import de.teamlapen.vampirism.entity.ExtendedCreature;
import de.teamlapen.vampirism.entity.converted.ConvertedCreatureEntity;
import net.minecraft.block.Blocks;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import java.util.Random;

/**
 * Just a simple dummy class, which makes it possible to spawn bitten mobs
 */
public class DummyBittenAnimalEntity extends MobEntity {

    public static boolean spawnPredicate(EntityType<? extends DummyBittenAnimalEntity> entityType, IWorld iWorld, SpawnReason spawnReason, BlockPos blockPos, Random random) {
        return (iWorld.getBlockState(blockPos.below()).getBlock() == Blocks.GRASS_BLOCK || iWorld.getBlockState(blockPos.below()).is(ModTags.Blocks.CURSEDEARTH));
    }

    public DummyBittenAnimalEntity(EntityType<? extends DummyBittenAnimalEntity> type, World world) {
        super(type, world);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.tickCount > 4 && !this.getCommandSenderWorld().isClientSide) {
            CreatureEntity entity;
            int rand = this.random.nextInt(3);
            switch (rand) {
                case 0:
                    entity = EntityType.PIG.create(level);
                    break;
                case 1:
                    entity = EntityType.SHEEP.create(level);
                    break;
                default:
                    entity = EntityType.COW.create(level);
                    break;
            }
            if (entity == null) return;
            entity.copyPosition(this);
            ExtendedCreature.getSafe(entity).ifPresent(e -> {
                if (e.canBecomeVampire()) {
                    IConvertedCreature c = e.makeVampire();
                    if (c instanceof ConvertedCreatureEntity) {
                        ((ConvertedCreatureEntity) c).setCanDespawn();
                    }
                }
            });

            this.remove();

        }
    }

}