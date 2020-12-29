package de.teamlapen.vampirism.entity.vampire;

import de.teamlapen.vampirism.api.entity.convertible.IConvertedCreature;
import de.teamlapen.vampirism.core.ModBlocks;
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
        return (iWorld.getBlockState(blockPos.down()).getBlock() == Blocks.GRASS_BLOCK || iWorld.getBlockState(blockPos.down()).getBlock() == ModBlocks.cursed_earth);
    }
    public DummyBittenAnimalEntity(EntityType<? extends DummyBittenAnimalEntity> type, World world) {
        super(type, world);
    }

    @Override
    public void livingTick() {
        super.livingTick();
        if (this.ticksExisted > 4 && !this.getEntityWorld().isRemote) {
            CreatureEntity entity;
            int rand = this.rand.nextInt(3);
            switch (rand) {
                case 0:
                    entity = EntityType.PIG.create(world);
                    break;
                case 1:
                    entity = EntityType.SHEEP.create(world);
                    break;
                default:
                    entity = EntityType.COW.create(world);
                    break;
            }
            if (entity == null) return;
            entity.copyLocationAndAnglesFrom(this);
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