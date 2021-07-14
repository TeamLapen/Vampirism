package de.teamlapen.vampirism.api.entity.convertible;

import de.teamlapen.vampirism.api.VampirismAPI;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Enables the option to cure a {@link IConvertedCreature}
 * <p>
 * to implement this feature there are some requirements:<p>
 * - override {@link #startConverting} to save the conversion started and conversion time <p>
 * - create a {@link DataParameter<Boolean> } the is returned by {@link #getConvertingDataParam()}<p>
 * - call {@link #registerConvertingData(CreatureEntity)} in {@link MobEntity#registerData()}<p>
 * - call {@link #interactWithCureItem(PlayerEntity, ItemStack, CreatureEntity)} in {@link MobEntity#getEntityInteractionResult(PlayerEntity, Hand)} if the cure item is in the players hand<p>
 * - call {@link #handleSound(byte, CreatureEntity)} in {@link MobEntity#handleStatusUpdate(byte)}<p>
 * - check in {@link MobEntity#livingTick()} if the conversion timer has ended. If so call {@link #cureEntity(ServerWorld, CreatureEntity, EntityType)}<p>
 */
public interface ICurableConvertedCreature<T extends CreatureEntity> extends IConvertedCreature<T> {

    /**
     * creates the new entity <p>
     * <p>
     * override to use already an already existing entity
     *
     * @param entity  the entity that extends this interface
     * @param newType the entity type of the cured entity
     * @return the new entity
     */
    default T createCuredEntity(CreatureEntity entity, EntityType<T> newType) {
        T newEntity = newType.create(entity.level);
        newEntity.load(entity.saveWithoutId(new CompoundNBT()));
        newEntity.yBodyRot = entity.yBodyRot;
        newEntity.yHeadRot = entity.yHeadRot;
        newEntity.setUUID(UUID.randomUUID());
        return newEntity;
    }

    /**
     * creates the cured entity and copies attributes <p>
     * adds/removes the entities to/from the world <p>
     * <p>
     * override to copy additionally attributes
     *
     * @param world   the world of the curing process
     * @param entity  the entity that extends this interface
     * @param newType the entity type of the cured entity
     * @return the new cured entity
     */
    default T cureEntity(ServerWorld world, CreatureEntity entity, EntityType<T> newType) {
        T newEntity = createCuredEntity(entity, newType);
        entity.remove();
        entity.level.addFreshEntity(newEntity);
        newEntity.addEffect(new EffectInstance(Effects.CONFUSION, 200, 0));
        if (!entity.isSilent()) {
            world.levelEvent(null, 1027, entity.blockPosition(), 0);
        }
        VampirismAPI.getExtendedCreatureVampirism(newEntity).ifPresent(creature -> {
            creature.setBlood(1);
        });
        net.minecraftforge.event.ForgeEventFactory.onLivingConvert(entity, newEntity);
        return newEntity;
    }

    DataParameter<Boolean> getConvertingDataParam();

    /**
     * call in {@link Entity#handleStatusUpdate(byte)}
     *
     * @param id     the status id
     * @param entity the entity that extends this interface
     * @return if the staus update was handled
     */
    default boolean handleSound(byte id, CreatureEntity entity) {
        if (id == 16) {
            if (!entity.isSilent()) {
                entity.level.playLocalSound(entity.getX(), entity.getEyeY(), entity.getZ(), SoundEvents.ZOMBIE_VILLAGER_CURE, entity.getSoundSource(), 1.0F + entity.getRandom().nextFloat(), entity.getRandom().nextFloat() * 0.7F + 0.3F, false);
            }
            return true;
        }
        return false;
    }

    /**
     * call in {@link MobEntity#getEntityInteractionResult(PlayerEntity, Hand)}
     *
     * @param player the interacting player
     * @param stack  the itemstack in the players hand
     * @param entity the entity that extends this interface
     * @return the action result
     */
    default ActionResultType interactWithCureItem(PlayerEntity player, ItemStack stack, CreatureEntity entity) {
        if (!entity.hasEffect(Effects.WEAKNESS)) return ActionResultType.CONSUME;
        if (!player.abilities.instabuild) {
            stack.shrink(1);
        }
        if (!entity.level.isClientSide) {
            this.startConverting(player.getUUID(), entity.getRandom().nextInt(2400) + 2400, entity);
        }
        return ActionResultType.SUCCESS;
    }

    /**
     * @param entity the entity that extends this interface
     * @return if the entity is in progress of converting
     */
    default boolean isConverting(CreatureEntity entity) {
        return entity.getEntityData().get(this.getConvertingDataParam());
    }

    /**
     * call in {@link Entity#defineSynchedData()}
     *
     * @param entity the entity that extends this interface
     */
    default void registerConvertingData(@Nonnull CreatureEntity entity) {
        entity.getEntityData().define(this.getConvertingDataParam(), false);
    }

    /**
     * called in {@link #interactWithCureItem(PlayerEntity, ItemStack, CreatureEntity)} override to save values to attributes
     *
     * @param conversionStarterIn uuid of the player that started the curing process
     * @param conversionTimeIn    ticks the conversion should takes
     * @param entity              the entity that extends this interface
     */
    default void startConverting(@Nullable UUID conversionStarterIn, int conversionTimeIn, @Nonnull CreatureEntity entity) {
        entity.getEntityData().set(this.getConvertingDataParam(), true);
        entity.removeEffect(Effects.WEAKNESS);
        entity.level.broadcastEntityEvent(entity, (byte) 16);
    }
}
