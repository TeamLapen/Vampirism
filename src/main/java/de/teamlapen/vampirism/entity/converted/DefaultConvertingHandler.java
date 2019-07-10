package de.teamlapen.vampirism.entity.converted;

import de.teamlapen.vampirism.api.entity.convertible.IConvertedCreature;
import de.teamlapen.vampirism.api.entity.convertible.IConvertingHandler;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.core.ModEntities;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.passive.PolarBearEntity;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.entity.passive.horse.HorseEntity;
import net.minecraft.entity.passive.horse.LlamaEntity;
import net.minecraft.item.Items;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;

import javax.annotation.Nullable;

/**
 * Default converting handler for entities.
 * Used for some vanilla entities, but can also be used for third party entities.
 * Converts the entity into a {@link ConvertedCreatureEntity}
 */
public class DefaultConvertingHandler<T extends CreatureEntity> implements IConvertingHandler<T> {

    /**
     * Used if no helper is specified
     */
    private final static IDefaultHelper defaultHelper = new IDefaultHelper() {


        @Override
        public void dropConvertedItems(CreatureEntity converted, CreatureEntity entity, boolean recentlyHit, int looting) {
            if (entity instanceof CowEntity) {
                int j = converted.getRNG().nextInt(3) + converted.getRNG().nextInt(1 + looting);

                for (int k = 0; k < j; ++k) {
                    converted.entityDropItem(Items.LEATHER, 1);
                }
                j = converted.getRNG().nextInt(3) + converted.getRNG().nextInt(1 + looting);

                for (int k = 0; k < j; ++k) {
                    converted.entityDropItem(Items.ROTTEN_FLESH, 1);
                }

            } else if (entity instanceof PigEntity || entity instanceof HorseEntity) {
                int j = converted.getRNG().nextInt(2) + converted.getRNG().nextInt(1 + looting);

                for (int k = 0; k < j; ++k) {
                    converted.entityDropItem(Items.ROTTEN_FLESH, 1);
                }
            } else if (entity instanceof LlamaEntity) {
                int j = converted.getRNG().nextInt(3);
                if (j > 0) converted.entityDropItem(Items.LEATHER, j);
            } else if (entity instanceof PolarBearEntity) {
                int j = converted.getRNG().nextInt(3);
                if (j > 0) converted.entityDropItem(Items.PUFFERFISH, j);
                int k = converted.getRNG().nextInt(2);
                if (k > 0) converted.entityDropItem(Items.ROTTEN_FLESH, k);
            } else if (entity instanceof RabbitEntity) {
                int j = converted.getRNG().nextInt(2);
                if (j > 0) converted.entityDropItem(Items.RABBIT_HIDE, j);
            } else {
                //TODO maybe call dropFewItems via reflection
            }

        }


        @Override
        public double getConvertedDMG(CreatureEntity entity) {
            IAttributeInstance dmg = entity.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
            if (dmg != null) {
                return dmg.getBaseValue() * 1.3;
            } else {
                return Balance.mobProps.CONVERTED_MOB_DEFAULT_DMG;
            }
        }

        @Override
        public double getConvertedKnockbackResistance(CreatureEntity entity) {
            return entity.getAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).getBaseValue();
        }

        @Override
        public double getConvertedMaxHealth(CreatureEntity entity) {
            return entity.getAttribute(SharedMonsterAttributes.MAX_HEALTH).getBaseValue() * 1.5;
        }

        @Override
        public double getConvertedSpeed(CreatureEntity entity) {
            return Math.min(entity.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getBaseValue() * 1.2, 2.9D);
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
        ConvertedCreatureEntity<T> convertedCreature = ModEntities.converted_creature.create(entity.getEntityWorld());
        copyImportantStuff(convertedCreature, entity);
        convertedCreature.addPotionEffect(new EffectInstance(Effects.WEAKNESS, 200, 2));
        return convertedCreature;
    }

    /**
     * @return The helper for this handler
     */
    public IDefaultHelper<T> getHelper() {
        return helper;
    }

    protected void copyImportantStuff(ConvertedCreatureEntity converted, T entity) {
        converted.copyLocationAndAnglesFrom(entity);
        converted.setEntityCreature(entity);
        converted.updateEntityAttributes();
        converted.setHealth(converted.getMaxHealth() / 3 * 2);
    }
}
