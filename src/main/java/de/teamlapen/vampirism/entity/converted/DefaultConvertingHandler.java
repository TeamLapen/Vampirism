package de.teamlapen.vampirism.entity.converted;

import de.teamlapen.vampirism.api.entity.convertible.IConvertedCreature;
import de.teamlapen.vampirism.api.entity.convertible.IConvertingHandler;
import de.teamlapen.vampirism.config.Balance;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.monster.EntityPolarBear;
import net.minecraft.entity.passive.*;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;

import javax.annotation.Nullable;

/**
 * Default converting handler for entities.
 * Used for some vanilla entities, but can also be used for third party entities.
 * Converts the entity into a {@link EntityConvertedCreature}
 */
public class DefaultConvertingHandler<T extends EntityCreature> implements IConvertingHandler<T> {

    /**
     * Used if no helper is specified
     */
    private final static IDefaultHelper defaultHelper = new IDefaultHelper() {


        @Override
        public void dropConvertedItems(EntityCreature entity, boolean recentlyHit, int looting) {
            if (entity instanceof EntityCow) {
                int j = entity.getRNG().nextInt(3) + entity.getRNG().nextInt(1 + looting);

                for (int k = 0; k < j; ++k) {
                    entity.dropItem(Items.LEATHER, 1);
                }

                j = entity.getRNG().nextInt(3) + entity.getRNG().nextInt(1 + looting);

                for (int k = 0; k < j; ++k) {
                    entity.dropItem(Items.ROTTEN_FLESH, 1);
                }

            } else if (entity instanceof EntityPig || entity instanceof EntityHorse) {
                int j = entity.getRNG().nextInt(2) + entity.getRNG().nextInt(1 + looting);

                for (int k = 0; k < j; ++k) {
                    entity.dropItem(Items.ROTTEN_FLESH, 1);
                }
            } else if (entity instanceof EntityLlama) {
                int j = entity.getRNG().nextInt(3);
                if (j > 0) entity.dropItem(Items.LEATHER, j);
            } else if (entity instanceof EntityPolarBear) {
                int j = entity.getRNG().nextInt(3);
                if (j > 0) entity.dropItem(Items.FISH, j);
                int k = entity.getRNG().nextInt(2);
                if (k > 0) entity.dropItem(Items.ROTTEN_FLESH, k);
            } else if (entity instanceof EntityRabbit) {
                int j = entity.getRNG().nextInt(2);
                if (j > 0) entity.dropItem(Items.RABBIT_HIDE, j);
            } else {
                //TODO maybe call dropFewItems via reflection
            }

        }


        @Override
        public double getConvertedDMG(EntityCreature entity) {
            IAttributeInstance dmg = entity.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
            if (dmg != null) {
                return dmg.getBaseValue() * 1.3;
            } else {
                return Balance.mobProps.CONVERTED_MOB_DEFAULT_DMG;
            }
        }

        @Override
        public double getConvertedKnockbackResistance(EntityCreature entity) {
            return entity.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).getBaseValue();
        }

        @Override
        public double getConvertedMaxHealth(EntityCreature entity) {
            return entity.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getBaseValue() * 1.5;
        }

        @Override
        public double getConvertedSpeed(EntityCreature entity) {
            return Math.min(entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getBaseValue() * 1.2, 2.9D);
        }
    };

    protected final IDefaultHelper helper;

    /**
     * @param helper If null a default one will be used
     */
    public DefaultConvertingHandler(@Nullable IDefaultHelper helper) {
        if (helper == null) {
            this.helper = defaultHelper;
        } else {
            this.helper = helper;
        }
    }

    @Override
    public IConvertedCreature<T> createFrom(T entity) {
        EntityConvertedCreature<T> convertedCreature = new EntityConvertedCreature<T>(entity.getEntityWorld());
        copyImportantStuff(convertedCreature, entity);
        convertedCreature.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 200, 2));
        return convertedCreature;
    }

    /**
     * @return The helper for this handler
     */
    public IDefaultHelper<T> getHelper() {
        return helper;
    }

    protected void copyImportantStuff(EntityConvertedCreature converted, T entity) {
        converted.copyLocationAndAnglesFrom(entity);
        converted.setEntityCreature(entity);
        converted.updateEntityAttributes();
        converted.setHealth(converted.getMaxHealth() / 3 * 2);
    }
}
