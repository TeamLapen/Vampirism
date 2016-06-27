package de.teamlapen.vampirism.entity.vampire;

import de.teamlapen.vampirism.api.entity.convertible.IConvertedCreature;
import de.teamlapen.vampirism.entity.ExtendedCreature;
import de.teamlapen.vampirism.entity.converted.EntityConvertedCreature;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.World;

/**
 * Just a simple dummy class, which makes it possible to spawn bitten mobs
 */
public class EntityDummyBittenAnimal extends EntityLiving {
    public EntityDummyBittenAnimal(World p_i1595_1_) {
        super(p_i1595_1_);
    }

    @Override
    public boolean isCreatureType(EnumCreatureType type, boolean forSpawnCount) {
        return type == EnumCreatureType.CREATURE;
    }


    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (this.ticksExisted > 4 && !this.worldObj.isRemote) {
            String entity;
            int rand = this.rand.nextInt(3);
            switch (rand) {
                case 0:
                    entity = "Pig";
                    break;
                case 1:
                    entity = "Sheep";
                    break;
                default:
                    entity = "Cow";
                    break;
            }
            EntityCreature entity1 = (EntityCreature) EntityList.createEntityByName(entity, worldObj);
            if (entity1 != null) {
                entity1.copyLocationAndAnglesFrom(this);
                if (ExtendedCreature.get(entity1).canBecomeVampire()) {
                    IConvertedCreature c = ExtendedCreature.get(entity1).makeVampire();
                    if (c instanceof EntityConvertedCreature) {
                        ((EntityConvertedCreature) c).setCanDespawn();
                    }
                }

            }
            this.setDead();

        }
    }

}