package de.teamlapen.vampirism.entity.convertible;

import de.teamlapen.vampirism.entity.EntityConvertedCreature;
import de.teamlapen.vampirism.util.BALANCE;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.init.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

/**
 * Created by Max on 15.08.2015.
 */
public class ConvertingHandler<T extends EntityCreature> {

    public EntityConvertedCreature createFrom(T entity) {
        EntityConvertedCreature convertedCreature = new EntityConvertedCreature(entity.worldObj);
        copyImportantStuff(convertedCreature, entity);
        convertedCreature.addPotionEffect(new PotionEffect(Potion.weakness.id, 200, 2));
        return convertedCreature;
    }

    protected void copyImportantStuff(EntityConvertedCreature convertedCreature, T entity) {
        convertedCreature.copyLocationAndAnglesFrom(entity);
        convertedCreature.setHealth(convertedCreature.getMaxHealth() / 3 * 2);
        convertedCreature.setEntityCreature(entity);
    }

    public void dropConvertedItems(T entity, boolean recentlyHit, int looting) {
        if (entity instanceof EntityCow) {
            int j = entity.getRNG().nextInt(3) + entity.getRNG().nextInt(1 + looting);

            for (int k = 0; k < j; ++k) {
                entity.dropItem(Items.leather, 1);
            }

            j = entity.getRNG().nextInt(3) + entity.getRNG().nextInt(1 + looting);

            for (int k = 0; k < j; ++k) {
                entity.dropItem(Items.rotten_flesh, 1);
            }

        } else if (entity instanceof EntityPig) {
            int j = entity.getRNG().nextInt(3) + entity.getRNG().nextInt(1 + looting);

            for (int k = 0; k < j; ++k) {
                entity.dropItem(Items.rotten_flesh, 1);
            }
        }
    }

    public double getConvertedDMG(T entity) {
        IAttributeInstance dmg = entity.getEntityAttribute(SharedMonsterAttributes.attackDamage);
        if (dmg != null) {
            return dmg.getBaseValue() * 1.3;
        } else {
            return BALANCE.MOBPROP.VAMPIRE_MOB_DEFAULT_DMG;
        }
    }

    public double getConvertedSpeed(T entity) {
        return Math.min(entity.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getBaseValue() * 1.2, 2.9D);
    }

    public double getConvertedKnockBackResistance(T entity) {
        return entity.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).getBaseValue();
    }

    public double getConvertedMaxHealth(T entity) {
        return entity.getEntityAttribute(SharedMonsterAttributes.maxHealth).getBaseValue() * 1.5;
    }
}
