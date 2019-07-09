package de.teamlapen.vampirism.entity.vampire;

import de.teamlapen.vampirism.api.entity.convertible.IConvertedCreature;
import de.teamlapen.vampirism.entity.ExtendedCreature;
import de.teamlapen.vampirism.entity.converted.EntityConvertedCreature;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.world.World;

/**
 * Just a simple dummy class, which makes it possible to spawn bitten mobs
 */
public class EntityDummyBittenAnimal extends MobEntity {
    public EntityDummyBittenAnimal(EntityType<? extends EntityDummyBittenAnimal> type, World world) {
        super(type, world);
    }

    @Override
    public boolean isCreatureType(EntityClassification type, boolean forSpawnCount) {
        return type == EntityClassification.CREATURE;
    }


    @Override
    public void livingTick() {
        super.livingTick();
        if (this.ticksExisted > 4 && !this.getEntityWorld().isRemote) {
            CreatureEntity entity;
            int rand = this.rand.nextInt(3);
            switch (rand) {
                case 0:
                    entity = new PigEntity(world);
                    break;
                case 1:
                    entity = new SheepEntity(world);
                    break;
                default:
                    entity = new CowEntity(world);
                    break;
            }
            entity.copyLocationAndAnglesFrom(this);
            if (ExtendedCreature.get(entity).canBecomeVampire()) {
                IConvertedCreature c = ExtendedCreature.get(entity).makeVampire();
                if (c instanceof EntityConvertedCreature) {
                    ((EntityConvertedCreature) c).setCanDespawn();
                }
            }


            this.remove();

        }
    }

}