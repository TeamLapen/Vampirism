package de.teamlapen.vampirism.entity.converted;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.convertible.IConvertedCreature;
import de.teamlapen.vampirism.api.entity.convertible.IConvertingHandler;
import de.teamlapen.vampirism.config.Balance;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import javax.annotation.Nullable;

/**
 * Default converting handler for entities.
 * Used for some vanilla entities, but can also be used for third party entities.
 * Converts the entity into a {@link EntityConvertedCreature}
 */
public class DefaultConvertingHandler<T extends EntityCreature> implements IConvertingHandler<T>{

    /**
     * Used if no helper is specified
     */
    private final static IDefaultHelper defaultHelper= new IDefaultHelper() {


        @Override
        public void dropConvertedItems(EntityCreature entity, boolean recentlyHit, int looting) {
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
            } else{
                //TODO maybe call dropFewItems via reflection
            }

        }



        @Override
        public double getConvertedDMG(EntityCreature entity) {
            IAttributeInstance dmg = entity.getEntityAttribute(SharedMonsterAttributes.attackDamage);
            if (dmg != null) {
                return dmg.getBaseValue() * 1.3;
            } else {
                return Balance.mobProps.CONVERTED_MOB_DEFAULT_DMG;
            }
        }

        @Override
        public double getConvertedSpeed(EntityCreature entity) {
            return Math.min(entity.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getBaseValue() * 1.2, 2.9D);
        }

        @Override
        public double getConvertedKnockbackResistance(EntityCreature entity) {
            return entity.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).getBaseValue();
        }

        @Override
        public double getConvertedMaxHealth(EntityCreature entity) {
            return entity.getEntityAttribute(SharedMonsterAttributes.maxHealth).getBaseValue() * 1.5;
        }
    };

    protected final IDefaultHelper helper;

    /**
     *
     * @param helper If null a default one will be used
     */
    public DefaultConvertingHandler(@Nullable IDefaultHelper helper) {
        if(helper==null){
            this.helper=defaultHelper;
        }
        else{
            this.helper=helper;
        }
    }

    @Override
    public IConvertedCreature<T> createFrom(T entity) {
        EntityConvertedCreature<T> convertedCreature = new EntityConvertedCreature<T>(entity.worldObj);
        copyImportantStuff(convertedCreature,entity);
        convertedCreature.addPotionEffect(new PotionEffect(Potion.weakness.id,200,2));
        return convertedCreature;
    }

    protected void copyImportantStuff(EntityConvertedCreature converted, T entity) {
        converted.copyLocationAndAnglesFrom(entity);
        converted.setHealth(converted.getMaxHealth() / 3 * 2);
        converted.setEntityCreature(entity);
    }

    /**
     *
     * @return The helper for this handler
     */
    public IDefaultHelper<T> getHelper(){
        return helper;
    }
}
