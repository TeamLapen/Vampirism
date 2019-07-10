package de.teamlapen.vampirism.entity.vampire;

import de.teamlapen.vampirism.api.entity.convertible.IConvertedCreature;
import de.teamlapen.vampirism.entity.ExtendedCreature;
import de.teamlapen.vampirism.entity.converted.ConvertedCreatureEntity;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.world.World;

/**
 * Just a simple dummy class, which makes it possible to spawn bitten mobs
 */
public class DummyBittenAnimalEntity extends MobEntity {
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
            entity.copyLocationAndAnglesFrom(this);
            if (ExtendedCreature.get(entity).canBecomeVampire()) {
                IConvertedCreature c = ExtendedCreature.get(entity).makeVampire();
                if (c instanceof ConvertedCreatureEntity) {
                    ((ConvertedCreatureEntity) c).setCanDespawn();
                }
            }


            this.remove();

        }
    }

}