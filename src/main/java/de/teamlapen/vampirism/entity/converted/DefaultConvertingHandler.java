package de.teamlapen.vampirism.entity.converted;

import de.teamlapen.vampirism.api.entity.convertible.IConvertedCreature;
import de.teamlapen.vampirism.api.entity.convertible.IConvertingHandler;
import de.teamlapen.vampirism.config.BalanceMobProps;
import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Default converting handler for entities.
 * Used for some vanilla entities, but can also be used for third party entities.
 * Converts the entity into a {@link ConvertedCreatureEntity}
 */
public class DefaultConvertingHandler<T extends PathfinderMob> implements IConvertingHandler<T> {

    /**
     * Used if no helper is specified
     */
    private final static IDefaultHelper defaultHelper = new IDefaultHelper() {


        @Override
        public double getConvertedDMG(@NotNull EntityType<? extends PathfinderMob> entityType) {
            AttributeSupplier map = DefaultAttributes.getSupplier(entityType);
            if (map.hasAttribute(Attributes.ATTACK_DAMAGE)) {
                return map.getBaseValue(Attributes.ATTACK_DAMAGE) * 1.3;
            } else {
                return BalanceMobProps.mobProps.CONVERTED_MOB_DEFAULT_DMG;
            }
        }

        @Override
        public double getConvertedKnockbackResistance(@NotNull EntityType<? extends PathfinderMob> entityType) {
            AttributeSupplier map = DefaultAttributes.getSupplier(entityType);
            return map.getBaseValue(Attributes.KNOCKBACK_RESISTANCE) * 1.3;
        }

        @Override
        public double getConvertedMaxHealth(@NotNull EntityType<? extends PathfinderMob> entityType) {
            AttributeSupplier map = DefaultAttributes.getSupplier(entityType);
            return map.getBaseValue(Attributes.MAX_HEALTH) * 1.5;
        }

        @Override
        public double getConvertedSpeed(@NotNull EntityType<? extends PathfinderMob> entityType) {
            AttributeSupplier map = DefaultAttributes.getSupplier(entityType);
            return Math.min(map.getBaseValue(Attributes.MOVEMENT_SPEED) * 1.2, 2.9D);
        }
    };

    protected final @NotNull IDefaultHelper helper;

    /**
     * @param helper If null a default one will be used
     */
    public DefaultConvertingHandler(@Nullable IDefaultHelper helper) {
        this.helper = Objects.requireNonNullElse(helper, defaultHelper);
    }

    @Nullable
    @Override
    public IConvertedCreature<T> createFrom(@NotNull T entity) {
        //noinspection unchecked
        return Helper.createEntity((EntityType<ConvertedCreatureEntity<T>>) (Object) ModEntities.CONVERTED_CREATURE.get(), entity.getCommandSenderWorld()).map(convertedCreature -> {
            copyImportantStuff(convertedCreature, entity);
            convertedCreature.setUUID(Mth.createInsecureUUID(convertedCreature.getRandom())); //Set a new uuid to avoid confusion as the class of the entity associated with the uuid changes
            convertedCreature.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 2));
            return convertedCreature;
        }).orElse(null);
    }

    /**
     * @return The helper for this handler
     */
    public IDefaultHelper getHelper() {
        return helper;
    }

    protected void copyImportantStuff(@NotNull ConvertedCreatureEntity<T> converted, @NotNull T entity) {
        converted.copyPosition(entity);
        converted.setEntityCreature(entity);
        converted.updateEntityAttributes();
        converted.setHealth(converted.getMaxHealth() / 3 * 2);
        converted.yBodyRot = entity.yBodyRot;
        converted.yHeadRot = entity.yHeadRot;
    }
}
