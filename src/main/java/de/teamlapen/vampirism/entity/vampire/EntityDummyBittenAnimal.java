package de.teamlapen.vampirism.entity.vampire;

import de.teamlapen.vampirism.api.entity.convertible.IConvertedCreature;
import de.teamlapen.vampirism.entity.ExtendedCreature;
import de.teamlapen.vampirism.entity.converted.EntityConvertedCreature;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
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
    public void livingTick() {
        super.livingTick();
        if (this.ticksExisted > 4 && !this.getEntityWorld().isRemote) {
            EntityCreature entity;
            int rand = this.rand.nextInt(3);
            switch (rand) {
                case 0:
                    entity = new EntityPig(world);
                    break;
                case 1:
                    entity = new EntitySheep(world);
                    break;
                default:
                    entity = new EntityCow(world);
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